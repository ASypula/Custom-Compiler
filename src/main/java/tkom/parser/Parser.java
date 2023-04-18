package tkom.parser;

import tkom.common.tokens.Token;
import tkom.common.tokens.TokenType;
import tkom.components.FunctionDef;
import tkom.components.Program;
import tkom.exception.InvalidTokenException;
import tkom.lexer.Lexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Parser {
    Lexer lexer;
    Token currToken;

    TokenType[] relationOpArray = {TokenType.T_EQUALS, TokenType.T_GREATER, TokenType.T_GREATER_OR_EQ,
            TokenType.T_LESS, TokenType.T_LESS_OR_EQ, TokenType.T_NOT_EQ};

    public Parser(Lexer lex){
        lexer = lex;
    }

    private void nextToken() throws InvalidTokenException, IOException {
        currToken = lexer.getToken();
        while (currToken.getType() == TokenType.T_COMMENT)
            currToken = lexer.getToken();
    }

    private boolean isCurrToken(TokenType tType){
        return currToken.getType() == tType;
    }

    private boolean parseLiteral(){
        return (isCurrToken(TokenType.T_INT) || isCurrToken(TokenType.T_DOUBLE) ||
        isCurrToken(TokenType.T_STRING) || isCurrToken(TokenType.T_TRUE) || isCurrToken(TokenType.T_FALSE));
    }

    private boolean parseMultExpression() throws InvalidTokenException, IOException {
        parsePrimExpression();
        if (isCurrToken(TokenType.T_MULT) || isCurrToken(TokenType.T_DIV)){
            nextToken();
            parsePrimExpression();
        }
        return false;
    }

    private boolean parseArithmExpression(){
        parseMultExpression();
        if (isCurrToken(TokenType.T_PLUS) || isCurrToken(TokenType.T_MINUS)){
            nextToken();
            parseMultExpression();
        }
        return false;
    }

    private boolean parseRelExpression(){
        parseArithmExpression();
        if (Arrays.asList(relationOpArray).contains(currToken.getType())){
            nextToken();
            parseArithmExpression();
        }
        return false;
    }

    private boolean parseAndExpression(){
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
    private boolean parsePrimExpression() throws InvalidTokenException, IOException {
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

    private boolean parseParameters(){
        if (!isCurrToken(TokenType.T_IDENT))
            return false;
        while (isCurrToken(TokenType.T_COLON)){
            nextToken();
            if (!isCurrToken(TokenType.T_IDENT))
                return false;
        }
    }

    /**
     * Parse: “if”, “(“, expr, “)”, block, [“else”, block];
     * @return
     * @throws InvalidTokenException
     */
    private boolean parseIfStatement() throws InvalidTokenException, IOException {
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

    private void parseStatement() throws InvalidTokenException, IOException {
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
    private boolean parseBlock() throws InvalidTokenException, IOException {
        if (!isCurrToken(TokenType.T_CURLY_BRACKET_L))
            throw new InvalidTokenException(currToken, TokenType.T_CURLY_BRACKET_L);
        nextToken();
        //TODO
        parseStatement();
        if (!isCurrToken(TokenType.T_CURLY_BRACKET_R))
            throw new InvalidTokenException(currToken, TokenType.T_CURLY_BRACKET_R);
        nextToken();
        return false;
    }

    /**
     * Parses: “function”, identifier, “(“, [ params ], “)”, block;
     * @param functions
     * @return
     * @throws InvalidTokenException
     * @throws IOException
     */
    private boolean parseFuncDef(HashMap<String, FunctionDef> functions) throws InvalidTokenException, IOException {
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
        parseParameters();
        if (!isCurrToken(TokenType.T_REG_BRACKET_R))
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_R);
        nextToken();
        //TODO
        parseBlock();
        return false;
    }

    public Program parse() throws InvalidTokenException, IOException {
        HashMap<String, FunctionDef> functions = new HashMap<>();
        while (parseFuncDef(functions));
        return new Program(functions);
    }
}
