package tkom.common.tokens;

import tkom.common.Position;

public class TokenDouble extends Token{

    double value;

    /**
     * Token for Lexer
     *
     * @param t   type of the token
     * @param pos position of first character in the text of the token
     * @param val actual double value of the token
     */
    public TokenDouble(TokenType t, Position pos, double val) {
        super(t, pos);
        value = val;
    }


    public double getDoubleValue(){
        return value;
    }
}
