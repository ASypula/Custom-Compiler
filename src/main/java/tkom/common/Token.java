package tkom.common;

public class Token {
    TokenType type;
    String value;
    Position startPos;

    public Token(TokenType t, String val, Position pos){
        type = t;
        value = val;
        startPos = pos;
    }

    public String getType(){
        return type.toString();
    }
}
