package tkom.components.classes;

import tkom.components.FunctionDef;
import tkom.visitor.Visitor;

public class Line implements IClass{
    @Override
    public boolean containsMethod(String x) {
        return false;
    }

    @Override
    public FunctionDef getMethod(String x) {
        return null;
    }

    @Override
    public void accept(Visitor visitor, String name) throws Exception {

    }
}
