package tkom.components.expressions;

import tkom.common.tokens.Token;

public class ArithmExpression implements IExpression{

    IExpression left;
    IExpression right;
    Token signT;

    public ArithmExpression(IExpression l, IExpression r, Token token){
        left = l;
        right = r;
        signT = token;
    }

    public ArithmExpression(IExpression l){
        left = l;
        right = null;
    }
}
