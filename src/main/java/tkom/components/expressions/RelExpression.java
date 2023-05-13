package tkom.components.expressions;

import tkom.common.tokens.Token;
import tkom.visitor.Visitor;

public class RelExpression implements IExpression{
    public IExpression left;
    public IExpression right;

    Token relOperator;

    public RelExpression(IExpression l, IExpression r, Token op){
        left = l;
        right = r;
        relOperator = op;
    }

    public RelExpression(IExpression l){
        left = l;
        right = null;
        relOperator = null;
    }

    @Override
    public void accept(Visitor visitor){
        visitor.accept(this);
    }
}
