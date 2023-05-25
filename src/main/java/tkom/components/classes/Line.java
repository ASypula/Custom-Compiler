package tkom.components.classes;

import tkom.common.ParserComponentTypes.ValueType;
import tkom.components.FunctionDef;
import tkom.components.Value;
import tkom.exception.IncorrectTypeException;
import tkom.exception.InvalidMethodException;
import tkom.visitor.Visitor;

import java.util.HashMap;

public class Line implements IClass{

    Point pL;
    Point pR;

    public HashMap<String, FunctionDef> methods = new HashMap<>();

    public Line(Value v1, Value v2) throws IncorrectTypeException, InvalidMethodException {
        if (v1.getType() != ValueType.V_POINT || v2.getType() != ValueType.V_POINT)
            throw new IncorrectTypeException("Point", v1.getType().toString());
        pL = (Point)(v1.getObject());
        pR = (Point)(v2.getObject());
        if (pL.x == pR.x && pL.y == pR.y)
            throw new IncorrectTypeException("two different points for a Line", "same points");
        if (pL.x > pL.x)
            swap(pL, pR);

    }

    public static void swap(Point p1, Point p2)
    {
        Point temp = p1;
        p1 = p2;
        p2 = temp;
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
            throw new InvalidMethodException(method, "Line method");
    }

    @Override
    public void accept(Visitor visitor, String name) throws Exception {

    }
}
