package tkom.components;

public class ReturnStatement implements IStatement {
    IExpression expression;

    public ReturnStatement(IExpression expr){
        expression = expr;
    }

}
