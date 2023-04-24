package tkom.components.statements;

import tkom.components.expressions.IExpression;

public class ReturnStatement implements IStatement {
    IExpression expression;

    public ReturnStatement(IExpression expr){
        expression = expr;
    }

}
