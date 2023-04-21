package tkom.parser;

import tkom.common.tokens.Token;
import tkom.common.tokens.TokenType;
import tkom.components.FunctionDef;
import tkom.components.Program;
import tkom.exception.InvalidTokenException;
import tkom.lexer.Lexer;

import java.io.IOException;
import java.util.HashMap;

public class Parser {
    Lexer lexer;
    Token currToken;

    public Parser(Lexer lex){
        lexer = lex;
    }

    private void nextToken() throws InvalidTokenException, IOException {
        currToken = lexer.getToken();
        while (currToken.getType() == TokenType.T_COMMENT)
            currToken = lexer.getToken();
    }

    //TODO: finish
    private boolean parseExpression() {
        return false;
    }

    /**
     * Parse: “if”, “(“, condition, “)”, block, [“else”, block];
     * @return
     * @throws InvalidTokenException
     */
    private boolean parseIfStatement() throws InvalidTokenException, IOException {
        if (currToken.getType() != TokenType.T_IF)
            return false;
        nextToken();
        if (currToken.getType() != TokenType.T_REG_BRACKET_L)
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_L);
        nextToken();
        parseCondition();
        if (currToken.getType() != TokenType.T_REG_BRACKET_R)
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_R);
        nextToken();
        parseBlock();
        if (currToken.getType() == TokenType.T_ELSE) {
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
        if (currToken.getType() != TokenType.T_WHILE)
            return false;
        nextToken();
        if (currToken.getType() != TokenType.T_REG_BRACKET_L)
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_L);
        nextToken();
        parseExpression();
        if (currToken.getType() != TokenType.T_REG_BRACKET_R)
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
        if (currToken.getType() != TokenType.T_RETURN)
            return false;
        nextToken();
        parseExpression();
        if (currToken.getType() != TokenType.T_SEMICOLON)
            throw new InvalidTokenException(currToken, TokenType.T_SEMICOLON);
        nextToken();
        return true;
    }

    /**
     * Parse: “=”, expr;
     */
    //TODO what about creating new types e.g. Line?
    private boolean parseAssignStatement() throws InvalidTokenException, IOException {
        if (currToken.getType() != TokenType.T_EQUALS)
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
        if (currToken.getType() != TokenType.T_IDENT)
            return false;
        nextToken();
        if (parseAssignStatement() || parseFunctionCall());
        if (currToken.getType() != TokenType.T_SEMICOLON)
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
        if (currToken.getType() != TokenType.T_CURLY_BRACKET_L)
            throw new InvalidTokenException(currToken, TokenType.T_CURLY_BRACKET_L);
        nextToken();
        //TODO
        parseStatement();
        if (currToken.getType() != TokenType.T_CURLY_BRACKET_R)
            throw new InvalidTokenException(currToken, TokenType.T_CURLY_BRACKET_R);
        nextToken();
        return false;
    }

    /**
     * Parses: “function”, identifier, “(“, params, “)”, block;
     * @param functions
     * @return
     * @throws InvalidTokenException
     * @throws IOException
     */
    private boolean parseFuncDef(HashMap<String, FunctionDef> functions) throws InvalidTokenException, IOException {
        if (currToken.getType() == TokenType.T_EOF)
            return false;
        nextToken();
        if (currToken.getType() != TokenType.T_FUNCTION)
            throw new InvalidTokenException(currToken, TokenType.T_FUNCTION);
        nextToken();
        if (currToken.getType() != TokenType.T_IDENT)
            throw new InvalidTokenException(currToken, TokenType.T_IDENT);
        String name = currToken.getStringValue();
        nextToken();
        if (currToken.getType() != TokenType.T_REG_BRACKET_L)
            throw new InvalidTokenException(currToken, TokenType.T_REG_BRACKET_L);
        nextToken();
        //TODO
        parseParameters();
        if (currToken.getType() != TokenType.T_REG_BRACKET_R)
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
