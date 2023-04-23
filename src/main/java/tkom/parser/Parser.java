package tkom.parser;

import tkom.common.ExceptionHandler;
import tkom.common.tokens.Token;
import tkom.common.tokens.TokenType;
import tkom.components.*;
import tkom.exception.ExceededLimitsException;
import tkom.exception.InvalidTokenException;
import tkom.lexer.Lexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
        else if (isCurrToken(TokenType.T_REG_BRACKET_L)){
            nextToken();
            parseExpression();
            if (!isCurrToken(TokenType.T_REG_BRACKET_R))
                return false;
        }

        return false;
    }

    /**
     * Parse: “if”, “(“, expr, “)”, block, [“else”, block];
     * @return
     * @throws InvalidTokenException
     */
    private boolean parseIfStatement() throws InvalidTokenException, IOException, ExceededLimitsException {
        if (isCurrToken(TokenType.T_IF))
            return false;
        nextToken();
        if (!isCurrToken( TokenType.T_REG_BRACKET_L))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_L);
        nextToken();
        parseExpression();
        if (!isCurrToken(TokenType.T_REG_BRACKET_R))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_R);
        nextToken();
        parseBlock();
        if (isCurrToken(TokenType.T_ELSE)) {
            nextToken();
            parseBlock();
        }
        return true;
    }

    /**
     * Parse: “while”, “(“, expr, “)”, block;
     * @return
     */
    private boolean parseWhileStatement() throws InvalidTokenException, IOException {
        if (!isCurrToken(TokenType.T_WHILE))
            return false;
        nextToken();
        if (!isCurrToken(TokenType.T_REG_BRACKET_L))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_L);
        nextToken();
        parseExpression();
        if (!isCurrToken(TokenType.T_REG_BRACKET_R))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_R);
        nextToken();
        parseBlock();
        return true;
    }

    /**
     * Parse: “return”, expr, “;”;
     * @return
     * @throws InvalidTokenException
     * @throws IOException
     */
    private boolean parseReturnStatement() throws InvalidTokenException, IOException {
        if (!isCurrToken(TokenType.T_RETURN))
            return false;
        nextToken();
        parseExpression();
        if (!isCurrToken(TokenType.T_SEMICOLON))
            throw new InvalidTokenException(currToken, TokenType.T_SEMICOLON);
        nextToken();
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

    private IStatement parseStatement() throws InvalidTokenException, IOException {
        if (parseIfStatement() || parseWhileStatement() || parseReturnStatement() ||
        parseAssignStmtOrFunctionCall() || parsePrintStatement() || parseBlock() )
        ;
    }

    /**
     * Parse: “{“, { statement }, “}”;
     * @return
     * @throws InvalidTokenException
     * @throws IOException
     */
    private Block parseBlock() throws InvalidTokenException, IOException, ExceededLimitsException {
        if (!isCurrToken(TokenType.T_CURLY_BRACKET_L))
            return null;
        nextToken();
        ArrayList<IStatement> statements = new ArrayList<>();
        IStatement stmt;
        stmt = parseStatement();
        while (stmt!=null){
            statements.add(stmt);
            stmt = parseStatement();
        }
        if (!isCurrToken(TokenType.T_CURLY_BRACKET_R))
            throw new InvalidTokenException(currToken, TokenType.T_CURLY_BRACKET_R);
        nextToken();
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
            while (isCurrToken(TokenType.T_COLON)) {
                nextToken();
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
        if (!isCurrToken(TokenType.T_FUNCTION))
            throw new InvalidTokenException(currToken, TokenType.T_FUNCTION);
        nextToken();
        if (!isCurrToken(TokenType.T_IDENT))
            throw new InvalidTokenException(currToken, TokenType.T_IDENT);
        String name = currToken.getStringValue();
        nextToken();
        if (!isCurrToken(TokenType.T_REG_BRACKET_L))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_L);
        nextToken();
        //TODO
        ArrayList<Parameter> params;
        params = parseParameters();
        if (!isCurrToken(TokenType.T_REG_BRACKET_R))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_R);
        nextToken();
        //TODO
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
