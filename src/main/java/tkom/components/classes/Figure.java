package tkom.components.classes;

import tkom.common.ParserComponentTypes.ValueType;
import tkom.components.Block;
import tkom.components.FunctionDef;
import tkom.components.Parameter;
import tkom.components.Value;
import tkom.exception.IncorrectFigureException;
import tkom.exception.IncorrectTypeException;
import tkom.exception.InvalidMethodException;
import tkom.interpreter.Interpreter;
import tkom.visitor.Visitor;

import java.util.ArrayList;
import java.util.HashMap;

public class Figure implements IClass{

    public ArrayList<Point> points;

    public HashMap<String, FunctionDef> methods = new HashMap<>();

    public int colorR=0;
    public int colorG=0;
    public int colorB=0;

    public Figure(Value value) throws IncorrectFigureException {
        if (value.getType() != ValueType.V_LIST)
            throw new IncorrectFigureException("Figure constructor requires a List");
        ListS l = (ListS)(value.getObject());
        if (l.list.size()<2)
            throw new IncorrectFigureException("Figure constructor got insufficient nr of elements to create a Figure");
        if (l.list.get(0).getType() == ValueType.V_POINT)
            points = getListPoints(l.list);
        else if (l.list.get(0).getType() == ValueType.V_LINE)
            points = createFigureLines(getListLines(l.list));
        else
            throw new IncorrectFigureException("Incorrect type, Figure constructor requires List of points or lines");
        methods.put("color", new setColor());
    }

    private ArrayList<Point> createFigureLines(ArrayList<Line> lines) throws IncorrectFigureException {
        ArrayList<Point> points = new ArrayList<>();
        if (!areLinesAligned(lines.get(0), lines.get(lines.size()-1)))
            throw new IncorrectFigureException("Provided lines in that order do not form a closed Figure");
        for (Line line: lines){
            if (points.size() == 0){
                points.add(line.pL);
                points.add(line.pR);
            }
            else{
                if (line != lines.get(lines.size()-1))
                    points.add(getSecondPoint(points.get(points.size()-1), line));
            }
        }
        return points;
    }

    private Point getSecondPoint(Point p, Line line) throws IncorrectFigureException {
        if (p.x == line.pL.x && p.y == line.pL.y)
            return line.pR;
        else if (p.x == line.pR.x && p.y == line.pR.y)
            return line.pL;
        else
            throw new IncorrectFigureException("Figure does not have closed lines");
    }

    private boolean areLinesAligned(Line line1, Line line2){
        if ((line1.pL.x == line2.pL.x && line1.pL.y == line2.pL.y) ||
                (line1.pR.x == line2.pL.x && line1.pR.y == line2.pL.y) ||
                (line1.pR.x == line2.pR.x && line1.pR.y == line2.pR.y) ||
                (line1.pL.x == line2.pR.x && line1.pL.y == line2.pR.y))
            return true;
        else
            return false;
    }

    private ArrayList<Point> getListPoints(ArrayList<Value> valueList){
        ArrayList<Point> newList = new ArrayList<>();
        for (Value value: valueList)
            newList.add((Point)(value.getObject()));
        return newList;
    }

    private ArrayList<Line> getListLines(ArrayList<Value> valueList){
        ArrayList<Line> newList = new ArrayList<>();
        for (Value value: valueList)
            newList.add((Line)(value.getObject()));
        return newList;
    }

    @Override
    public boolean containsMethod(String name) {return methods.containsKey(name);}

    @Override
    public FunctionDef getMethod(String method) throws InvalidMethodException {
        if (methods.containsKey(method))
            return methods.get(method);
        else
            throw new InvalidMethodException(method, "Figure method");
    }

    @Override
    public void accept(Visitor visitor, String name) throws Exception {
        methods.get(name).accept(visitor);
    }

    public class setColor extends FunctionDef {
        String name = "color";

        String param1 = "r";
        String param2 = "g";
        String param3 = "b";
        ArrayList<Parameter> parameters = new ArrayList<>();
        Block block = null;

        public setColor() {
            parameters.add(new Parameter(param1));
            parameters.add(new Parameter(param2));
            parameters.add(new Parameter(param3));
        }

        public String getName(){
            return name;
        }
        public ArrayList<Parameter> getParams(){
            return parameters;
        }

        public void accept(Visitor visitor) throws Exception {
            Value v1 = ((Interpreter)visitor).getValue(param1);
            Value v2 = ((Interpreter)visitor).getValue(param2);
            Value v3 = ((Interpreter)visitor).getValue(param3);
            if (v1.getType() != ValueType.V_INT || v2.getType() != ValueType.V_INT || v3.getType() != ValueType.V_INT)
                throw new IncorrectTypeException("int", "non int", "setting Figure color");
            colorR = v1.getIntValue();
            colorG = v2.getIntValue();
            colorB = v3.getIntValue();
        }
    }
}
