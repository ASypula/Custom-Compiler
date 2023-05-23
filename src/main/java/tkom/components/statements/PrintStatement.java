package tkom.components.statements;

import tkom.common.ParserComponentTypes.ValueType;
import tkom.visitor.Visitor;

public class PrintStatement implements IStatement{
    public ValueType vType;
    public String value;

    public PrintStatement (ValueType v, String val){
        vType = v;
        value = val;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.visit(this);
    }
}
