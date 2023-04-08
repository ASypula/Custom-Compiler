package tkom.common;

public class Token {
    TokenType type;
    String value;
    Position startPos;

    /**
     * Token for Lexer
     * @param t     type of the token
     * @param val   actual string-text of the token
     * @param pos   position of first character in the text of the token
     */
    public Token(TokenType t, String val, Position pos){
        type = t;
        value = val;
        startPos = pos;
    }

    public TokenType getType(){
        return type;
    }
    public String getTypeString(){
        return type.toString();
    }

    public String getValue() {
        return value;
    }

    public Position getPosition(){
        return startPos;
    }
}
