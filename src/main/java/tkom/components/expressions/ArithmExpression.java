package tkom.components.expressions;

import tkom.common.ParserComponentTypes.ValueType;
import tkom.common.tokens.Token;
import tkom.components.Value;
import tkom.exception.InvalidMethodException;
import tkom.exception.OverflowException;
import tkom.exception.ZeroDivisionException;
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

    public Value add(Value v1, Value v2) throws InvalidMethodException, OverflowException {
        double sum;
        if (ValueType.V_INT == v2.getType()) {
            if (ValueType.V_INT == v1.getType()) {
                if (isIntOverflowAddition(v1.getIntValue(), v2.getIntValue()))
                    throw new OverflowException("addition");
                return new Value(v1.getIntValue() + v2.getIntValue());
            }
            else
                sum = v1.getDoubleValue() + v2.getIntValue();
        }
        else {
            if (ValueType.V_INT == v1.getType())
                sum = v1.getIntValue() + v2.getDoubleValue();
            else
                sum = v1.getDoubleValue() + v2.getDoubleValue();
        }
        if (sum == Double.POSITIVE_INFINITY || sum == Double.NEGATIVE_INFINITY)
            throw new OverflowException("addition");
        return new Value(sum);
    }

    public Value subtract(Value v1, Value v2) throws InvalidMethodException, OverflowException {
        Value negatedValue;
        if (ValueType.V_INT == v2.getType())
            negatedValue = new Value(-1*v2.getIntValue());
        else
            negatedValue = new Value(-1.0 * v2.getDoubleValue());
        return add(v1, negatedValue);
    }

    private boolean isIntOverflowAddition(int x, int y){
        if (x>0 && y>0){
            if (x >= Integer.MAX_VALUE - y)
                return true;
        }
        else if(x<0 && y<0){
            if (x <= Integer.MIN_VALUE - y)
                return true;
        }
        return false;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.visit(this);
    }
}
