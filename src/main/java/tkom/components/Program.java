package tkom.components;

import java.util.HashMap;

public class Program implements Node{
    HashMap<String, FunctionDef> functions;

    public Program(HashMap<String, FunctionDef> func){
        functions = func;
    }
}
