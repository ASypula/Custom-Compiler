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
