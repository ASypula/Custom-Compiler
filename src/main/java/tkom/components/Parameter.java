package tkom.components;

import tkom.visitor.Visitable;
import tkom.visitor.Visitor;

public class Parameter implements Visitable {
    public String name;

    public Parameter(String nm){
        name = nm;
    }

    @Override
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
