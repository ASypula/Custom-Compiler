package tkom.components.expressions;

import tkom.common.tokens.Token;

public class ArithmExpression implements IExpression{

    public IExpression left;
    public IExpression right;
    boolean subtraction;

    public ArithmExpression(IExpression l, IExpression r, boolean isSubtraction){
        left = l;
        right = r;
        subtraction = isSubtraction;
    }

    public ArithmExpression(IExpression l){
        left = l;
        right = null;
        subtraction = false;
    }

    public boolean isSubtraction() {
        return subtraction;
    }
}
