package tkom.components;

import tkom.common.ParserComponentTypes.LiteralType;
import tkom.exception.InvalidMethodException;
import tkom.exception.MissingPartException;

public class Literal {
    LiteralType type;
    int number;
    double value;
    String text;
    boolean bool;

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
}
