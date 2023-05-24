package tkom.components.classes;

import tkom.components.FunctionDef;
import tkom.exception.InvalidMethodException;
import tkom.visitor.Visitor;

public class Figure implements IClass{
    @Override
    public boolean containsMethod(String x) {
        return false;
    }

    @Override
    public FunctionDef getMethod(String x) throws InvalidMethodException {
        return null;
    }

    @Override
    public void accept(Visitor visitor, String name) throws Exception {

    }
}
