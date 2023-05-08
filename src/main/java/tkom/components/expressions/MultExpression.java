package tkom.components.expressions;

import tkom.common.tokens.Token;

public class MultExpression implements IExpression{
    public IExpression left;
    public IExpression right;
    boolean division;

    public MultExpression(IExpression l, IExpression r, boolean isDivision){
        left = l;
        right = r;
        division = isDivision;
    }

    public MultExpression(IExpression l){
        left = l;
        right = null;
        division = false;
    }

    public boolean isDivision() {
        return division;
    }
}
