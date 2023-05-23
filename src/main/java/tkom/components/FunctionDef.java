package tkom.components;

import tkom.visitor.Visitable;
import tkom.visitor.Visitor;

import java.util.ArrayList;

public class FunctionDef implements Visitable {
    String name;
    ArrayList<Parameter> parameters;
    Block block;

    public FunctionDef(String nm, ArrayList<Parameter> params, Block blk){
        name = nm;
        parameters = params;
        block = blk;
    }

    public FunctionDef(){}

    public String getName(){
        return name;
    }

    public ArrayList<Parameter> getParams(){
        return parameters;
    }

    public Block getBlock(){
        return block;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.visit(this);
    }
}
