package tkom.components.statements;

import tkom.components.expressions.IExpression;
import tkom.visitor.Visitor;

public class ReturnStatement implements IStatement {
    IExpression expression;

    public ReturnStatement(IExpression expr){
        expression = expr;
    }

    public IExpression getExpression() {
        return expression;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.visit(this);
    }

}
