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

    private boolean parseFuncDef(HashMap<String, FunctionDef> functions) throws InvalidTokenException, IOException {
        if (currToken.getType() == TokenType.T_EOF)
            return false;
        nextToken();
        return false;
    }

    public Program parse(){
        HashMap<String, FunctionDef> functions = new HashMap<>();
        // while (parseFuncDef(functions));
        return new Program(functions);
    }
}
