package tkom.components;

import tkom.components.expressions.IExpression;
import tkom.components.statements.IStatement;
import tkom.visitor.Visitable;
import tkom.visitor.Visitor;

public class ObjectAccess implements Visitable, IStatement, IExpression {
    String name;
    IExpression expr;
    ObjectAccess parent;

    public ObjectAccess(String identifier, IExpression expression){
        name = identifier;
        expr = expression;
    }

    public ObjectAccess(ObjectAccess parent, IExpression expression) {
        this.parent = parent;
        expr = expression;
    }

    public String getName(){
        return name;
    }

    public IExpression getExpression(){
        return expr;
    }

    public ObjectAccess getParent(){
        return parent;
    }
    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.visit(this);
    }

}