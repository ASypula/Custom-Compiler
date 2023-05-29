package tkom.components.classes;

import tkom.common.ParserComponentTypes.ValueType;
import tkom.components.Block;
import tkom.components.FunctionDef;
import tkom.components.Parameter;
import tkom.components.Value;
import tkom.components.classes.IClass;
import tkom.exception.ExceededLimitsException;
import tkom.exception.IncorrectTypeException;
import tkom.exception.InvalidMethodException;
import tkom.interpreter.Interpreter;
import tkom.visitor.Visitor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ListS implements IClass {
    public ArrayList<Value> list = new ArrayList<>();

    public HashMap<String, FunctionDef> methods = new HashMap<>();

    public ListS(){
        methods.put("add", new ListAddFunc());
        methods.put("remove", new ListRemoveFunc());
        methods.put("show", new ShowFigures());
    }

    @Override
    public boolean containsMethod(String name){
        return methods.containsKey(name);
    }

    @Override
    public FunctionDef getMethod(String method) throws InvalidMethodException {
        if (methods.containsKey(method))
            return methods.get(method);
        else
            throw new InvalidMethodException(method, "List method");
    }

    public void accept(Visitor visitor, String name) throws Exception {
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

        public void add(Value v) throws IncorrectTypeException {
            if (list.size() > 0 && list.get(0).getType() != v.getType())
                throw new IncorrectTypeException((list.get(0).getType()).toString(), (v.getType() ).toString());
            list.add(v);
        }

        public void accept(Visitor visitor) throws Exception {
            visitor.visit(this);
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

        public Value remove() throws ExceededLimitsException {
            if (list.size()>0)
                return list.get(list.size()-1);
            else
                throw new ExceededLimitsException("remove in list", "not enough elements");
        }

        public void accept(Visitor visitor) throws Exception {
            visitor.visit(this);
        }
    }

    public class ShowFigures extends FunctionDef {
        String name = "show";
        ArrayList<Parameter> parameters = new ArrayList<>();
        Block block = null;

        public ShowFigures() {}

        public String getName(){
            return name;
        }
        public ArrayList<Parameter> getParams(){
            return parameters;
        }

        public ArrayList<Value> getList(){
            return list;
        }

        public void accept(Visitor visitor) throws Exception {
            visitor.visit(this);
        }

        public ArrayList<Figure> getListFigures(ArrayList<Value> valueList){
            ArrayList<Figure> newList = new ArrayList<>();
            for (Value value: valueList)
                newList.add((Figure)(value.getObject()));
            return newList;
        }
    }

}
