package tkom.components.statements;

import tkom.visitor.Visitor;

public class LiteralStatement implements IStatement{
    String identifier;

    public LiteralStatement(String ident){
        identifier = ident;
    }

    public String getIdentifier(){
        return identifier;
    }

    @Override
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
