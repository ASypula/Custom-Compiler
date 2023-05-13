package tkom.components;

import java.util.ArrayList;

public class FunctionDef implements Node{
    String name;
    ArrayList<Parameter> parameters;
    Block block;

    public FunctionDef(String nm, ArrayList<Parameter> params, Block blk){
        name = nm;
        parameters = params;
        block = blk;
    }

    public String getName(){
        return name;
    }

    public ArrayList<Parameter> getParams(){
        return parameters;
    }

    public Block getBlock(){
        return block;
    }
}
