package tkom.components.expressions;

public class PrimExpression implements IExpression{
    boolean negated;
    //TODO: remove isLiteral
    boolean isLiteral;

    public PrimExpression(boolean isNegated, boolean literal){
        negated = isNegated;
        isLiteral = literal;
    }

}
