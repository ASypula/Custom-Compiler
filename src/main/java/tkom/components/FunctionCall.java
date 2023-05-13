package tkom.components;

import tkom.components.expressions.IExpression;
import tkom.components.statements.IStatement;

import java.util.ArrayList;

public class FunctionCall implements IStatement, IExpression {
    String name;
    ArrayList<IExpression> arguments;

    public FunctionCall(String nm, ArrayList<IExpression> expressions){
        name = nm;
        arguments = expressions;
    }

    public String getName (){
        return name;
    }
}
