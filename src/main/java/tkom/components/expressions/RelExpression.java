package tkom.components.expressions;

import tkom.common.ParserComponentTypes.OperatorType;
import tkom.components.Value;
import tkom.exception.InvalidMethodException;
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

    public boolean evaluate(int left, int right){
        boolean result = switch (relOperator){
            case O_EQUALS -> left==right;
            case O_GREATER -> left > right;
            case O_GREATER_OR_EQ -> left>= right;
            case O_LESS -> left < right;
            case O_LESS_OR_EQ -> left <= right;
            case O_NOT_EQ -> left != right;
        };
        return result;
    }

    public boolean evaluate(double left, double right){
        int comparison = Double.compare(left, right);
        boolean result = switch (relOperator){
            case O_EQUALS -> comparison == 0;
            case O_GREATER -> comparison > 0;
            case O_GREATER_OR_EQ -> comparison >= 0;
            case O_LESS -> comparison < 0;
            case O_LESS_OR_EQ -> comparison <= 0;
            case O_NOT_EQ -> comparison != 0;
        };
        return result;
    }

    public boolean evaluate(String left, String right) throws InvalidMethodException {
        boolean result = switch (relOperator){
            case O_EQUALS -> left == right;
            case O_NOT_EQ -> left != right;
            default -> throw new InvalidMethodException("Not supported String comparison operation", "evaluate");
        };
        return result;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.accept(this);
    }
}
