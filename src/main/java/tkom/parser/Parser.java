package tkom.parser;

import tkom.common.ExceptionHandler;
import tkom.common.tokens.Token;
import tkom.common.tokens.TokenType;
import tkom.components.*;
import tkom.exception.ExceededLimitsException;
import tkom.exception.InvalidTokenException;
import tkom.exception.MissingPartException;
import tkom.lexer.Lexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Parser {
    Lexer lexer;
    Token currToken;

    TokenType[] relationOpArray = {TokenType.T_EQUALS, TokenType.T_GREATER, TokenType.T_GREATER_OR_EQ,
            TokenType.T_LESS, TokenType.T_LESS_OR_EQ, TokenType.T_NOT_EQ};
    public ExceptionHandler excHandler;
    public Parser(Lexer lex, ExceptionHandler eh){

        lexer = lex;
        excHandler = eh;
    }

    /**
     * Get next token from lexer but omit the comment tokens
     */
    private void nextToken() throws InvalidTokenException, IOException, ExceededLimitsException {
        currToken = lexer.getToken();
        while (currToken.getType() == TokenType.T_COMMENT)
            currToken = lexer.getToken();
    }

    /**
     * Compare type of current token
     * @param tType     token type for comparison
     * @return          true if the types are equal, false otherwise
     */
    private boolean isCurrToken(TokenType tType){
        return currToken.getType() == tType;
    }

    /**
     * ojoj
     * @param tType
     * @return
     * @throws InvalidTokenException
     * @throws ExceededLimitsException
     * @throws IOException
     */
    private boolean consumeIfToken(TokenType tType) throws InvalidTokenException, ExceededLimitsException, IOException {
        if (!isCurrToken(tType))
            return false;
        nextToken();
        return true;
    }

    private boolean parseLiteral(){
        return (isCurrToken(TokenType.T_INT) || isCurrToken(TokenType.T_DOUBLE) ||
        isCurrToken(TokenType.T_STRING) || isCurrToken(TokenType.T_TRUE) || isCurrToken(TokenType.T_FALSE));
    }

    private IExpression parseMultExpression() throws InvalidTokenException, IOException, ExceededLimitsException {
        parsePrimExpression();
        if (isCurrToken(TokenType.T_MULT) || isCurrToken(TokenType.T_DIV)){
            nextToken();
            parsePrimExpression();
        }
        return false;
    }

    private IExpression parseArithmExpression() throws InvalidTokenException, ExceededLimitsException, IOException {
        parseMultExpression();
        if (isCurrToken(TokenType.T_PLUS) || isCurrToken(TokenType.T_MINUS)){
            nextToken();
            parseMultExpression();
        }
        return false;
    }

    private IExpression parseRelExpression(){
        parseArithmExpression();
        if (Arrays.asList(relationOpArray).contains(currToken.getType())){
            nextToken();
            parseArithmExpression();
        }
        return false;
    }

    private IExpression parseAndExpression(){
        parseRelExpression();
        if (isCurrToken(TokenType.T_AND)){
            nextToken();
            parseRelExpression();
        }
        return false;
    }

    private boolean parseExpression(){
        parseAndExpression();
        if (isCurrToken(TokenType.T_OR)){
            nextToken();
            parseAndExpression();
        }
        return false;
    }

    //TODO: finish
    private boolean parsePrimExpression() throws InvalidTokenException, IOException, ExceededLimitsException {
        if (isCurrToken(TokenType.T_NOT) || isCurrToken(TokenType.T_MINUS)){
            // do sth
            nextToken();
        }
        if (parseLiteral() || cos()){
            ;
        }
        else if (consumeIfToken(TokenType.T_REG_BRACKET_L)){
            parseExpression();
            if (!consumeIfToken(TokenType.T_REG_BRACKET_R))
                return false;
        }

        return false;
    }

    /**
     * Parse: “if”, “(“, expr, “)”, block, [“else”, block];
     * @return
     * @throws InvalidTokenException
     */
    private IStatement parseIfStatement() throws InvalidTokenException, IOException, ExceededLimitsException, MissingPartException {
        if (!consumeIfToken(TokenType.T_IF))
            return null;
        if (!consumeIfToken( TokenType.T_REG_BRACKET_L))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_L);
        IExpression expr = parseExpression();
        if (expr == null)
            throw new MissingPartException(currToken, "expression", "IfStatement");
        if (!consumeIfToken(TokenType.T_REG_BRACKET_R))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_R);
        Block blockTrue = parseBlock();
        Block blockElse = null;
        if (consumeIfToken(TokenType.T_ELSE))
            blockElse = parseBlock();
        return new IfStatement(expr, blockTrue, blockElse);
    }

    /**
     * Parse: “while”, “(“, expr, “)”, block;
     * @return
     */
    private IStatement parseWhileStatement() throws InvalidTokenException, IOException, ExceededLimitsException, MissingPartException {
        if (!consumeIfToken(TokenType.T_WHILE))
            return null;
        if (!consumeIfToken(TokenType.T_REG_BRACKET_L))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_L);
        IExpression expr = parseExpression();
        if (expr == null)
            throw new MissingPartException(currToken, "expression", "WhileStatement");
        if (!consumeIfToken(TokenType.T_REG_BRACKET_R))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_R);
        Block block = parseBlock();
        return new WhileStatement(expr, block);
    }

    /**
     * Parse: “return”, expr, “;”;
     * @return
     * @throws InvalidTokenException
     * @throws IOException
     */
    private IStatement parseReturnStatement() throws InvalidTokenException, IOException, ExceededLimitsException {
        if (!consumeIfToken(TokenType.T_RETURN))
            return null;
        parseExpression();
        if (!consumeIfToken(TokenType.T_SEMICOLON))
            throw new InvalidTokenException(currToken, TokenType.T_SEMICOLON);
        return true;
    }

    /**
     * Parse: “=”, expr;
     */
    //TODO what about creating new types e.g. Line?
    private boolean parseAssignStatement() throws InvalidTokenException, IOException {
        if (!isCurrToken(TokenType.T_EQUALS))
            return false;
        nextToken();
        if (parseExpression() || parseString());
        return false;
    }

    /**
     * Parse: identifier, ( assign_stmt | func_call ), “;”;
     * @return
     * @throws InvalidTokenException
     * @throws IOException
     */
    private boolean parseAssignStmtOrFunctionCall() throws InvalidTokenException, IOException {
        if (!isCurrToken(TokenType.T_IDENT))
            return false;
        nextToken();
        if (parseAssignStatement() || parseFunctionCall());
        if (!isCurrToken(TokenType.T_SEMICOLON))
            throw new InvalidTokenException(currToken, TokenType.T_SEMICOLON);
        nextToken();
        return true;
    }

    private IStatement parseStatement() throws InvalidTokenException, IOException, ExceededLimitsException, MissingPartException {
        IStatement stmt = parseIfStatement();
        if (stmt != null)
            return stmt;
        stmt = parseWhileStatement();
        if (stmt != null)
            return stmt;
        stmt = parseReturnStatement();
        if (stmt != null)
            return stmt;
        stmt = parseAssignStmtOrFunctionCall();
        if (stmt != null)
            return stmt;
        return parsePrintStatement();
    }

    /**
     * Parse: “{“, { statement }, “}”;
     * @return
     * @throws InvalidTokenException
     * @throws IOException
     */
    private Block parseBlock() throws InvalidTokenException, IOException, ExceededLimitsException {
        if (!consumeIfToken(TokenType.T_CURLY_BRACKET_L))
            return null;
        ArrayList<IStatement> statements = new ArrayList<>();
        IStatement stmt;
        stmt = parseStatement();
        while (stmt!=null){
            statements.add(stmt);
            stmt = parseStatement();
        }
        if (!consumeIfToken(TokenType.T_CURLY_BRACKET_R))
            throw new InvalidTokenException(currToken, TokenType.T_CURLY_BRACKET_R);
        return new Block(statements);
    }

    private boolean containsName(ArrayList<Parameter> list, String name){
        List<String> containsList = list.stream()
                .map(s -> s.name)
                .filter(s -> s == name).toList();
        return !(containsList.size() == 0);
    }

    private ArrayList<Parameter> parseParameters() throws InvalidTokenException, ExceededLimitsException, IOException {
        ArrayList<Parameter> params = new ArrayList<>();
        if (isCurrToken(TokenType.T_IDENT)) {
            params.add(new Parameter(currToken.getStringValue()));
            nextToken();
            while (consumeIfToken(TokenType.T_COLON)) {
                if (isCurrToken(TokenType.T_IDENT)) {
                    String name = currToken.getStringValue();
                    if (containsName(params, name))
                        throw Exception("TODO different exception");
                    params.add(new Parameter(name));
                    nextToken();
                }
            }
        }
        return params;
    }

    /**
     * Parses: “function”, identifier, “(“, [ params ], “)”, block;
     * @param functions
     * @return
     * @throws InvalidTokenException
     * @throws IOException
     */
    private boolean parseFuncDef(HashMap<String, FunctionDef> functions) throws InvalidTokenException, IOException, ExceededLimitsException {
        if (isCurrToken(TokenType.T_EOF))
            return false;
        nextToken();
        if (!consumeIfToken(TokenType.T_FUNCTION))
            throw new InvalidTokenException(currToken, TokenType.T_FUNCTION);
        if (!isCurrToken(TokenType.T_IDENT))
            throw new InvalidTokenException(currToken, TokenType.T_IDENT);
        String name = currToken.getStringValue();
        nextToken();
        if (!consumeIfToken(TokenType.T_REG_BRACKET_L))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_L);
        ArrayList<Parameter> params;
        params = parseParameters();
        if (!consumeIfToken(TokenType.T_REG_BRACKET_R))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_R);
        Block block = parseBlock();
        functions.put(name, new FunctionDef(name, params, block));
        return true;
    }

    /**
     * Parse the given program as long as the EOF is not encountered
     * @return      parsed program
     * @throws InvalidTokenException
     * @throws IOException
     */
    public Program parse() throws InvalidTokenException, IOException, ExceededLimitsException {
        HashMap<String, FunctionDef> functions = new HashMap<>();
        while (parseFuncDef(functions));
        return new Program(functions);
    }
}
