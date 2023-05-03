package tkom.components;

import java.util.HashMap;

public class Program implements Node{
    public HashMap<String, FunctionDef> functions;

    public Program(HashMap<String, FunctionDef> func){
        functions = func;
    }
}
