package tkom.common.tokens;

import tkom.common.Position;

public class TokenString extends Token{

    String value;
    /**
     * Token for Lexer
     *
     * @param t   type of the token
     * @param pos position of first character in the text of the token
     * @param val actual string-text of the token
     */
    public TokenString(TokenType t, Position pos, String val) {
        super(t, pos);
        value = val;
    }

    public String getValue(){
        return value;
    }
}
