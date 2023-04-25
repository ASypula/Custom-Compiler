package tkom.components.statements;

import tkom.components.expressions.IExpression;

public class AssignStatement implements IStatement {
    String identifier;
    IExpression right;

    public AssignStatement(String ident, IExpression expr){
        identifier = ident;
        right = expr;
    }
}
