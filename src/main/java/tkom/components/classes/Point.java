package tkom.components.classes;

import tkom.common.ParserComponentTypes.ValueType;
import tkom.components.Block;
import tkom.components.FunctionDef;
import tkom.components.Parameter;
import tkom.components.Value;
import tkom.exception.IncorrectTypeException;
import tkom.exception.InvalidMethodException;
import tkom.interpreter.Interpreter;
import tkom.visitor.Visitor;

import java.util.ArrayList;
import java.util.HashMap;

public class Point implements IClass{

    public int x;
    public int y;

    public HashMap<String, FunctionDef> methods = new HashMap<>();

    public Point(Value v1, Value v2) throws IncorrectTypeException, InvalidMethodException {
        if (v1.getType() != ValueType.V_INT || v2.getType() != ValueType.V_INT)
            throw new IncorrectTypeException("int", v1.getType().toString());
        x = v1.getIntValue();
        y = v2.getIntValue();
        methods.put("x", new getX());
        methods.put("y", new getY());
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
            throw new InvalidMethodException(method, "Point method");
    }

    @Override
    public void accept(Visitor visitor, String name) throws Exception {
        methods.get(name).accept(visitor);
    }

    public class getX extends FunctionDef {
        String name = "x";
        ArrayList<Parameter> parameters = new ArrayList<>();
        Block block = null;

        public getX() {}

        public String getName(){
            return name;
        }
        public ArrayList<Parameter> getParams(){
            return parameters;
        }

        public Value get(){
            return new Value(x);
        }
//TODO: add visit
        public void accept(Visitor visitor) throws Exception {
            visitor.visit(this);
        }
    }

    public class getY extends FunctionDef {
        String name = "y";
        ArrayList<Parameter> parameters = new ArrayList<>();
        Block block = null;

        public getY() {}

        public String getName(){
            return name;
        }
        public ArrayList<Parameter> getParams(){
            return parameters;
        }

        public Value get(){
            return new Value(y);
        }

        public void accept(Visitor visitor) throws Exception {
            visitor.visit(this);
        }
    }
}
