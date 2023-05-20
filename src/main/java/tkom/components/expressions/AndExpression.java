package tkom.components.expressions;

import tkom.visitor.Visitor;

public class AndExpression implements IExpression{
    public IExpression left;
    public IExpression right;

    public AndExpression(IExpression l, IExpression r){
        left = l;
        right = r;
    }

    public AndExpression(IExpression l){
        left = l;
        right = null;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.accept(this);
    }
}
