package tkom.components;

import tkom.common.ParserComponentTypes.ValueType;
import tkom.exception.InvalidMethodException;
import tkom.visitor.Visitable;
import tkom.visitor.Visitor;

public class Value implements Visitable {
    ValueType type;
    int number;
    double value;
    String text;
    boolean bool;

    public ValueType getType(){
        return type;
    }
    public Value(int nr){
        number = nr;
        type = ValueType.V_INT;
    }

    public Value(double val){
        value = val;
        type = ValueType.V_DOUBLE;
    }

    public Value(String txt, ValueType lType){
        type = lType;
        text = txt;
    }

    public Value(boolean val){
        bool = val;
        type = ValueType.V_BOOL;
    }

    public int getIntValue() throws InvalidMethodException {
        if (type == ValueType.V_INT)
            return number;
        else
            throw new InvalidMethodException("Value", "int value");
    }

    public double getDoubleValue() throws InvalidMethodException {
        if (type == ValueType.V_DOUBLE)
            return value;
        else
            throw new InvalidMethodException("Value", "double value");
    }

    public boolean getBoolValue() throws InvalidMethodException {
        if (type == ValueType.V_BOOL)
            return bool;
        else
            throw new InvalidMethodException("Value", "bool value");
    }

    public String getIdentifierValue() throws InvalidMethodException {
        if (type == ValueType.V_IDENT)
            return text;
        else
            throw new InvalidMethodException("Value", "identifier value");
    }

    public String getStringValue() throws InvalidMethodException {
        if (type == ValueType.V_STRING)
            return text;
        else
            throw new InvalidMethodException("Value", "string value");
    }

    @Override
    public void accept(Visitor visitor){
        visitor.accept(this);
    }
}
