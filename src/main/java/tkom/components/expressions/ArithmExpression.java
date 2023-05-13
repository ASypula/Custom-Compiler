package tkom.components.expressions;

import tkom.common.tokens.Token;
import tkom.visitor.Visitor;

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

    @Override
    public void accept(Visitor visitor){
        visitor.accept(this);
    }
}
