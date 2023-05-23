package tkom.components.functions;

import tkom.common.ParserComponentTypes.ValueType;
import tkom.components.Block;
import tkom.components.FunctionDef;
import tkom.components.Parameter;
import tkom.components.Value;
import tkom.exception.IncorrectValueException;
import tkom.interpreter.Interpreter;
import tkom.visitor.Visitor;

import java.util.ArrayList;

public class PrintFunction extends FunctionDef {

    String name = "print";
    ArrayList<Parameter> parameters = new ArrayList<>();
    Block block = null;

    public PrintFunction() {
        parameters.add(new Parameter("x"));
    }

    public String getName(){
        return name;
    }

    public ArrayList<Parameter> getParams(){
        return parameters;
    }

    public void accept(Visitor visitor) throws Exception {
        Value value = ((Interpreter)visitor).getValue("x");
        ValueType type = value.getType();
        if (type == ValueType.V_INT)
            System.out.println(value.getIntValue());
        else if (type == ValueType.V_DOUBLE)
            System.out.println(value.getDoubleValue());
        else if (type == ValueType.V_STRING)
            System.out.println(value.getStringValue());
        else
            throw new IncorrectValueException("PrintStatement", "non-string", "string identifier");
    }

}
