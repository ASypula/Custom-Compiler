package tkom.common.tokens;

import tkom.common.Position;

public class Token {
    TokenType type;
    Position startPos;

    /**
     * Token for Lexer
     * @param t     type of the token
     * @param pos   position of first character in the text of the token
     */
    public Token(TokenType t, Position pos){
        type = t;
        startPos = pos;
    }

    public TokenType getType(){
        return type;
    }
    public String getTypeString(){
        return type.toString();
    }
    public Position getPosition(){
        return startPos;
    }
}

