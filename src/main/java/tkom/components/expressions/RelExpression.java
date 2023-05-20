package tkom.components.expressions;

import tkom.common.ParserComponentTypes.OperatorType;
import tkom.visitor.Visitor;

public class RelExpression implements IExpression{
    public IExpression left;
    public IExpression right;

    OperatorType relOperator;

    public RelExpression(IExpression l, IExpression r, OperatorType op){
        left = l;
        right = r;
        relOperator = op;
    }

    public RelExpression(IExpression l){
        left = l;
        right = null;
        relOperator = null;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.accept(this);
    }
}
