package tkom.components;

import tkom.exception.InvalidMethodException;
import tkom.exception.MissingPartException;
import tkom.visitor.Visitable;
import tkom.visitor.Visitor;

import java.util.HashMap;

public class Program implements Visitable {
    public HashMap<String, FunctionDef> functions;

    public Program(HashMap<String, FunctionDef> func){
        functions = func;
    }

    public FunctionDef getFunction(String key) throws InvalidMethodException {
        FunctionDef funcDef = functions.get(key);
        if (funcDef == null)
            throw new InvalidMethodException("function definition " + key, "get function by name");
        return funcDef;
    }

    public FunctionDef[] getFunctions(){
        return functions.values().toArray(new FunctionDef[0]);
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.accept(this);
    }
}
