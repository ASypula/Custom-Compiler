package tkom.components.expressions;

public class RelExpression implements IExpression{
    IExpression left;
    IExpression right;

    public RelExpression(IExpression l, IExpression r){
        left = l;
        right = r;
    }

    public RelExpression(IExpression l){
        left = l;
        right = null;
    }
}
