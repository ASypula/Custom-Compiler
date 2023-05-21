package tkom.interpreter;

import tkom.components.Value;

import java.util.HashMap;

public class Context {

    public HashMap<String, Value> map;

    public Context(){
        map = new HashMap<>();
    }
    public Context(HashMap<String, Value> valueMap){
        map = valueMap;
    }
}
