package tkom.components.expressions;

import tkom.visitor.Visitor;

public class Expression implements IExpression{
    // For expressions that are divided by OR ('||')
    public IExpression left;
    public IExpression right;

    public Expression(IExpression l, IExpression r){
        left = l;
        right = r;
    }

    public Expression(IExpression l){
        left = l;
        right = null;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.accept(this);
    }
}
