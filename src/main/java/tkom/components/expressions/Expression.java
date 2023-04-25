package tkom.components.expressions;

public class Expression implements IExpression{
    // For expressions that are divided by OR ('||')
    IExpression left;
    IExpression right;

    public Expression(IExpression l, IExpression r){
        left = l;
        right = r;
    }

    public Expression(IExpression l){
        left = l;
        right = null;
    }
}
