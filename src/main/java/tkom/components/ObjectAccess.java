package tkom.components;

import tkom.components.expressions.IExpression;
import tkom.components.statements.IStatement;

public class ObjectAccess implements IStatement {
    String name;
    IExpression expr;

    public ObjectAccess(String identifier, IExpression expression){
        name = identifier;
        expr = expression;
    }
}
