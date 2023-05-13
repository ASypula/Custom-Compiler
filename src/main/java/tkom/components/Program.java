package tkom.components;

import tkom.exception.InvalidMethodException;

import java.util.HashMap;

public class Program implements Node{
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
}
