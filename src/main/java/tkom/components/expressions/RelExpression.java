package tkom.components.expressions;

import tkom.common.tokens.Token;

public class RelExpression implements IExpression{
    IExpression left;
    IExpression right;

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
}
