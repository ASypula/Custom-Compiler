package tkom.common.tokens;

import tkom.common.Position;

public class TokenInt extends Token {
    int value;

    /**
     * Token for Lexer
     *
     * @param t   type of the token
     * @param pos position of first character in the text of the token
     * @param val actual int value of the token
     */
    public TokenInt(TokenType t, Position pos, int val) {
        super(t, pos);
        value = val;
    }

    public int getIntValue(){
        return value;
    }

}
