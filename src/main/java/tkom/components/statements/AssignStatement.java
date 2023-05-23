package tkom.components.statements;

import tkom.components.expressions.IExpression;
import tkom.visitor.Visitor;

public class AssignStatement implements IStatement {
    String identifier;
    IExpression right;

    public AssignStatement(String ident, IExpression expr){
        identifier = ident;
        right = expr;
    }

    public String getIdentifier() { return identifier; }

    public IExpression getExpression(){
        return right;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.visit(this);
    }
}
