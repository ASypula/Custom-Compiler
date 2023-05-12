package tkom.components;

import tkom.components.expressions.IExpression;
import tkom.components.statements.IStatement;

public class ObjectAccess implements IStatement, IExpression {
    String name;
    IExpression expr;
    ObjectAccess parent;

    public ObjectAccess(String identifier, IExpression expression){
        name = identifier;
        expr = expression;
    }

    public ObjectAccess(ObjectAccess parent, IExpression expression) {
        this.parent = parent;
        expr = expression;
    }

    public String getName(){
        return name;
    }

    public IExpression getExpression(){
        return expr;
    }

}