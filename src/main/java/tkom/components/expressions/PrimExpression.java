package tkom.components.expressions;

import tkom.common.ParserComponentTypes.ExpressionType;
import tkom.components.Value;
import tkom.components.statements.IStatement;
import tkom.visitor.Visitor;

public class PrimExpression implements IExpression{
    public boolean negated;

    public Value value = null;
    public IStatement stmt = null;
    public IExpression expr = null;

    public ExpressionType type;


    public PrimExpression(boolean isNegated, Value lit){
        negated = isNegated;
        value = lit;
        type = ExpressionType.E_VALUE;
    }

    public PrimExpression(boolean isNegated, IStatement st){
        negated = isNegated;
        stmt = st;
        type = ExpressionType.E_STMT;
    }

    public PrimExpression(boolean isNegated, IExpression exp){
        negated = isNegated;
        expr = exp;
        type = ExpressionType.E_EXPR;
    }

    @Override
    public void accept(Visitor visitor){
        visitor.accept(this);
    }

}
