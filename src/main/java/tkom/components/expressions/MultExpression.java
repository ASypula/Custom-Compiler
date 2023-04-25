package tkom.components.expressions;

import tkom.common.tokens.Token;

public class MultExpression implements IExpression{
    IExpression left;
    IExpression right;
    Token signT;

    public MultExpression(IExpression l, IExpression r, Token token){
        left = l;
        right = r;
        signT = token;
    }

    public MultExpression(IExpression l){
        left = l;
        right = null;
    }
}
