package tkom.ParserTest;

import tkom.common.Position;
import tkom.common.tokens.Token;
import tkom.common.tokens.TokenType;
import tkom.lexer.ILexer;

import java.util.ArrayList;

public class MockLexer implements ILexer {

    ArrayList<Token> tokens;
    int pos;

    public MockLexer(ArrayList<Token> tokensList){
        tokens = tokensList;
        pos=-1;
    }

    public Token getToken() {
        if (pos+1<tokens.size()) {
            pos+=1;
            return tokens.get(pos);
        }
        return new Token(TokenType.T_EOF, new Position(9,9));
    }
}
