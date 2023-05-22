package tkom.components;

import tkom.components.expressions.IExpression;
import tkom.components.statements.IStatement;
import tkom.visitor.Visitable;
import tkom.visitor.Visitor;

import java.util.ArrayList;

public class FunctionCall implements Visitable, IStatement, IExpression {
    String name;
    ArrayList<IExpression> arguments;

    public FunctionCall(String nm, ArrayList<IExpression> expressions){
        name = nm;
        arguments = expressions;
    }

    public String getName (){
        return name;
    }
    public ArrayList<IExpression> getArguments(){
        return arguments;
    }
    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.accept(this);
    }
}


