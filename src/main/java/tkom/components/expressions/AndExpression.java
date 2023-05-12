package tkom.components.expressions;

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
}
