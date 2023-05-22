package tkom.components.expressions;

import tkom.common.ParserComponentTypes.ValueType;
import tkom.components.Value;
import tkom.exception.InvalidMethodException;
import tkom.exception.OverflowException;
import tkom.exception.ZeroDivisionException;
import tkom.visitor.Visitor;

public class MultExpression implements IExpression{
    public IExpression left;
    public IExpression right;
    boolean division;

    private double epsilon = Math.pow(10, -6);

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

    public Value divide(Value dividend, Value divisor) throws ZeroDivisionException, InvalidMethodException {
        Value result;
        if (ValueType.V_INT == divisor.getType()) {
            if (divisor.getIntValue() < epsilon)
                throw new ZeroDivisionException();
            if (ValueType.V_INT == dividend.getType())
                result = new Value(dividend.getIntValue()/divisor.getIntValue());
            else
                result = new Value(dividend.getDoubleValue()/divisor.getIntValue());
        }
        else {
            if (divisor.getDoubleValue() < epsilon)
                throw new ZeroDivisionException();
            if (ValueType.V_INT == dividend.getType())
                result = new Value(dividend.getIntValue()/divisor.getDoubleValue());
            else
                result = new Value(dividend.getDoubleValue()/divisor.getDoubleValue());
        }
        return result;
    }

    public Value multiply(Value multiplicand, Value multiplier) throws OverflowException, InvalidMethodException {
        Value result;
        double product;
        if (ValueType.V_INT == multiplier.getType()) {
            if (ValueType.V_INT == multiplicand.getType()) {
                try {
                    result = new Value(Math.multiplyExact(multiplicand.getIntValue(), multiplier.getIntValue()));
                    return result;
                } catch (ArithmeticException | InvalidMethodException e) {
                    throw new OverflowException("multiplication");
                }
            }
            else
                product = multiplicand.getDoubleValue() * multiplier.getIntValue();
        }
        else {
            if (ValueType.V_INT == multiplicand.getType())
                product = multiplicand.getIntValue() * multiplier.getDoubleValue();
            else
                product = multiplicand.getDoubleValue() * multiplier.getDoubleValue();
        }
        if (product == Double.POSITIVE_INFINITY || product == Double.NEGATIVE_INFINITY)
            throw new OverflowException("multiplication");
        return new Value(product);
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.accept(this);
    }
}
