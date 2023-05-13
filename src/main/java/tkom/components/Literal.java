package tkom.components;

import tkom.common.ParserComponentTypes.LiteralType;
import tkom.common.tokens.TokenType;
import tkom.exception.InvalidMethodException;
import tkom.exception.MissingPartException;
import tkom.visitor.Visitable;
import tkom.visitor.Visitor;

public class Literal implements Visitable {
    LiteralType type;
    int number;
    double value;
    String text;
    boolean bool;

    TokenType tokenType;

    public LiteralType getType(){
        return type;
    }
    public Literal(int nr){
        number = nr;
        type = LiteralType.L_INT;
    }

    public Literal(double val){
        value = val;
        type = LiteralType.L_DOUBLE;
    }

    public Literal(String txt, LiteralType lType){
        type = lType;
        text = txt;
    }

    public Literal(String txt, LiteralType lType, TokenType tType){
        type = lType;
        text = txt;
        tokenType = tType;
    }

    public Literal(boolean val){
        bool = val;
        type = LiteralType.L_BOOL;
    }

    public int getIntValue() throws InvalidMethodException {
        if (type == LiteralType.L_INT)
            return number;
        else
            throw new InvalidMethodException("Literal", "int value");
    }

    public double getDoubleValue() throws InvalidMethodException {
        if (type == LiteralType.L_DOUBLE)
            return value;
        else
            throw new InvalidMethodException("Literal", "double value");
    }

    public boolean getBoolValue() throws InvalidMethodException {
        if (type == LiteralType.L_BOOL)
            return bool;
        else
            throw new InvalidMethodException("Literal", "bool value");
    }

    public String getIdentifierValue() throws InvalidMethodException {
        if (type == LiteralType.L_IDENT)
            return text;
        else
            throw new InvalidMethodException("Literal", "identifier value");
    }

    public String getStringValue() throws InvalidMethodException {
        if (type == LiteralType.L_STRING)
            return text;
        else
            throw new InvalidMethodException("Literal", "string value");
    }

    public TokenType getTokenType() throws InvalidMethodException {
        if (type == LiteralType.L_CLASS)
            return tokenType;
        else
            throw new InvalidMethodException("Literal", "token type");
    }

    @Override
    public void accept(Visitor visitor){
        visitor.accept(this);
    }
}
