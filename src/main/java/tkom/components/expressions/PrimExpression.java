package tkom.components.expressions;

import tkom.common.ParserComponentTypes.ExpressionType;
import tkom.components.Literal;
import tkom.components.statements.IStatement;

public class PrimExpression implements IExpression{
    public boolean negated;

    public Literal literal = null;
    public IStatement stmt = null;
    public IExpression expr = null;

    public ExpressionType type;


    public PrimExpression(boolean isNegated, Literal lit){
        negated = isNegated;
        literal = lit;
        type = ExpressionType.E_LITERAL;
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

}
