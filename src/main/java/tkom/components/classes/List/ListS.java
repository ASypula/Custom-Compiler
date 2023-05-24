package tkom.components.classes.List;

import tkom.components.Block;
import tkom.components.FunctionDef;
import tkom.components.Parameter;
import tkom.components.Value;
import tkom.components.classes.IClass;
import tkom.exception.ExceededLimitsException;
import tkom.exception.IncorrectTypeException;
import tkom.interpreter.Interpreter;
import tkom.visitor.Visitor;

import java.util.ArrayList;
import java.util.HashMap;

public class ListS implements IClass {
    public ArrayList<Value> list = new ArrayList<>();

    public HashMap<String, FunctionDef> methods = new HashMap<>();

    public ListS(){
        methods.put("add", new ListAddFunc());
        methods.put("remove", new ListRemoveFunc());
    }

    @Override
    public boolean containsMethod(String name){
        return methods.containsKey(name);
    }

    @Override
    public FunctionDef getMethod(String method) {
        //TODO raise Exception
        return methods.get(method);
    }

    public void accept(Visitor visitor, String name) throws Exception {
        //TODO add exception if name not found
        methods.get(name).accept(visitor);
    }

    public class ListAddFunc extends FunctionDef {
        String name = "add";
        String param1 = "x";
        ArrayList<Parameter> parameters = new ArrayList<>();
        Block block = null;

        public ListAddFunc() {
            parameters.add(new Parameter(param1));
        }

        public String getName(){
            return name;
        }
        public ArrayList<Parameter> getParams(){
            return parameters;
        }

        public void accept(Visitor visitor) throws Exception {
            Value v = ((Interpreter)visitor).getValue(param1);
            if (list.size() > 0 && list.get(0).getType() != v.getType())
                throw new IncorrectTypeException((list.get(0).getType()).toString(), (v.getType() ).toString());
            list.add(v);
        }
    }

    public class ListRemoveFunc extends FunctionDef {
        String name = "remove";
        ArrayList<Parameter> parameters = new ArrayList<>();
        Block block = null;
        public ListRemoveFunc() {}

        public String getName(){
            return name;
        }
        public ArrayList<Parameter> getParams(){
            return parameters;
        }

        public void accept(Visitor visitor) throws Exception {
            if (list.size()>0)
                ((Interpreter)visitor).results.push(list.get(list.size()-1));
            else
                throw new ExceededLimitsException("remove in list", "not enough elements");
        }
    }
}