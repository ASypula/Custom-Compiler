package tkom.components.classes;

import tkom.components.FunctionDef;
import tkom.exception.InvalidMethodException;
import tkom.visitor.Visitor;

public interface IClass {
    boolean containsMethod(String x);

    FunctionDef getMethod (String x) throws InvalidMethodException;

    void accept(Visitor visitor, String name) throws Exception ;
}
