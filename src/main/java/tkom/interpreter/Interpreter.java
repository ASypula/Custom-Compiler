package tkom.interpreter;

import tkom.common.ParserComponentTypes.ExpressionType;
import tkom.common.ParserComponentTypes.ValueType;
import tkom.components.*;
import tkom.components.classes.*;
import tkom.components.classes.Point;
import tkom.components.expressions.*;
import tkom.components.functions.PrintFunction;
import tkom.components.statements.*;
import tkom.exception.*;
import tkom.visitor.Visitor;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Interpreter implements Visitor {

    public final HashMap<String, FunctionDef> functions;
    private final Deque<Context> contexts;

    public final Stack<Value> results;

    private final String mainFunc = "main";
    private final ArrayList<String> classNames = new ArrayList<>(Arrays.asList("Point", "Figure", "FigCollection", "Line", "List"));

    private final ArrayList<ValueType> classTypes = new ArrayList<>(Arrays.asList(ValueType.V_LIST, ValueType.V_LINE, ValueType.V_POINT, ValueType.V_FIGURE));

    private final ArrayList<String> functionNames = new ArrayList<>(Arrays.asList("print"));

    public JFrame fr = new JFrame();

    private final double epsilon = Math.pow(10, -6);

    private boolean createNewContext = true;
    private boolean functionReturn = false;

    private boolean withResultStmt = false;
    public boolean objectAccess = false;
    public Interpreter(HashMap<String, FunctionDef> funcs) {
        functions = funcs;
        contexts = new ArrayDeque<>();
        results =new Stack<>();
        prepareFunctions();
        fr.setBounds(10, 10, 500, 500);
        fr.setDefaultCloseOperation(3);
    }

    public void runMain() throws Exception {
        if (!containsMain())
            throw new MissingPartException("main function", "runMain in interpreter");
        functions.get(mainFunc).accept(this);
    }

    private boolean containsMain(){
        return functions.containsKey(mainFunc);
    }

    private void prepareFunctions(){
        functions.put("print", new PrintFunction());
    }

    private boolean testValueType(ValueType type, Value value){
        return type == value.getType();
    }

    private boolean isValueTrue(Value value) throws Exception {
        if (testValueType(ValueType.V_BOOL, value))
            return value.getBoolValue();
        if (testValueType(ValueType.V_DOUBLE, value))
            return value.getDoubleValue() > epsilon;
        if (testValueType(ValueType.V_INT, value))
            return value.getIntValue() > epsilon;
        if (testValueType(ValueType.V_STRING, value))
            return value.getStringValue() != null;
        else
            throw new IncorrectValueException("test value", value.getType().name(), "value evaluated to true or false");
    }

    private boolean notNumber(Value value){
        return !testValueType(ValueType.V_INT, value) && !testValueType(ValueType.V_DOUBLE, value);
    }

    /**
     * Check if provided identifier can be used as variable name:
     * cannot be a name of a class nor a function name
     */
    private boolean isCorrectIdentifier(String identifier){
        if (classNames.contains(identifier))
            return false;
        ArrayList<String> listOfKeys
                = new ArrayList<>(functions.keySet());
        return !listOfKeys.contains(identifier);
    }

    private boolean isDifferentType(Value x, Value y){
        return x.getType() != y.getType();
    }

    private void updateContext(String identifier, Value value) throws IncorrectTypeException {
        Iterator<Context> it = contexts.iterator();
        Context context;
        while (it.hasNext()) {
            context = it.next();
            if (context.map.containsKey(identifier)) {
                if (isDifferentType(value, context.map.get(identifier)))
                    throw new IncorrectTypeException(value.getType().toString(), context.map.get(identifier).getType().toString());
                else {
                    context.map.put(identifier, value);
                    return;
                }
            }
        }
        Context currContext = contexts.pop();
        currContext.map.put(identifier, value);
        contexts.push(currContext);
    }

    public Value getValue(String name) throws UnknownVariableException {
        Iterator<Context> it = contexts.iterator();
        Context context;
        while (it.hasNext()) {
            context = it.next();
            if (context.map.containsKey(name))
                return context.map.get(name);
        }
        throw new UnknownVariableException(name);
    }

    private void addArgumentsToContext (ArrayList<Parameter> params, ArrayList<IExpression> args) throws Exception {
        if (params.size() != args.size())
            throw new InvalidMethodException("function call", "same number of args as params in functionDef");
        Context context = contexts.pop();
        for (int i = 0; i< params.size(); ++i){
            args.get(i).accept(this);
            Value value = results.pop();
            context.map.put(params.get(i).name, value);
        }
        contexts.push(context);
    }

    @Override
    public void visit(AndExpression andExpr) throws Exception {
        andExpr.left.accept(this);
        Value result = results.pop();
        if (andExpr.right == null)
            results.push(result);
        else {
            if (!isValueTrue(result)) {
                // false - no need for further processing
                results.push(new Value(false));
            } else {
                andExpr.right.accept(this);
                result = results.pop();
                results.push(new Value(isValueTrue(result)));
            }
        }
    }

    @Override
    public void visit(ArithmExpression arithmExpr) throws Exception {
        arithmExpr.left.accept(this);
        Value result = results.pop();
        if (arithmExpr.right != null) {
            arithmExpr.right.accept(this);
            Value element = results.pop();
            if (element.getType() == ValueType.V_STRING && result.getType()==ValueType.V_STRING)
                result = arithmExpr.concat(result, element);
            else if (notNumber(element) || notNumber(result))
                throw new InvalidMethodException("Non numerical value", "arithmetic operation");
            else if (arithmExpr.isSubtraction())
                result = arithmExpr.subtract(result, element);
            else
                result = arithmExpr.add(result, element);
        }
        results.push(result);
    }

    @Override
    public void visit(Expression expr) throws Exception {
        expr.left.accept(this);
        Value result = results.pop();
        if (expr.right == null)
            results.push(result);
        else {
            if (isValueTrue(result)) {
                // true - no need for further processing
                results.push(new Value(true));
            } else {
                expr.right.accept(this);
                result = results.pop();
                results.push(new Value(isValueTrue(result)));
            }
        }
    }
//TODO change stack to single value if possible
    @Override
    public void visit(MultExpression multExpr) throws Exception {
        multExpr.left.accept(this);
        Value result = results.pop();
        if (multExpr.right != null) {
            multExpr.right.accept(this);
            Value element = results.pop();
            if (notNumber(element) || notNumber(result))
                throw new InvalidMethodException("Non numerical value", "multiplication");
            if (multExpr.isDivision())
                result = multExpr.divide(result, element);
            else
                result = multExpr.multiply(result, element);
        }
        results.push(result);
    }

    @Override
    public void visit(PrimExpression primExpr) throws Exception {
        Value value;
        if (primExpr.type != ExpressionType.E_VALUE){
            primExpr.expr.accept(this);
            value = results.pop();
        }
        else
            value = primExpr.value;
        if (!objectAccess && testValueType(ValueType.V_IDENT, value)){
            if (isCorrectIdentifier(value.getIdentifierValue())){
                results.push(getValue(value.getIdentifierValue()));
                return;
            }
        }
        results.push(value);
    }

    @Override
    public void visit(RelExpression relExpr) throws Exception {
        relExpr.left.accept(this);
        Value result = results.pop();
        if (relExpr.right == null)
            results.push(result);
        else {
            relExpr.right.accept(this);
            Value rightRes = results.pop();
            if (isDifferentType(result, rightRes)) {
                results.push(new Value(false));
                return;
            }
            boolean bool = switch(result.getType()){
                case V_INT:
                    yield relExpr.evaluate(result.getIntValue(), rightRes.getIntValue());
                case V_DOUBLE:
                    yield relExpr.evaluate(result.getDoubleValue(), rightRes.getDoubleValue());
                case V_STRING:
                    yield relExpr.evaluate(result.getStringValue(), rightRes.getStringValue());
                default:
                    yield false;
            };
            results.push(new Value(bool));
        }
    }

    @Override
    public void visit(AssignStatement assignStmt) throws Exception {
        if (!isCorrectIdentifier(assignStmt.getIdentifier()))
            throw new IncorrectValueException("AssignStatement", "built-in name: class or function name", "identifier");
        withResultStmt = true;
        assignStmt.getExpression().accept(this);
        Value result = results.pop();
        updateContext(assignStmt.getIdentifier(), result);
        withResultStmt = false;
    }

    @Override
    public void visit(IfStatement ifStmt) throws Exception{
        contexts.push(new Context());
        ifStmt.getCondition().accept(this);
        Value result = results.pop();
        if (isValueTrue(result)) {
            ifStmt.getBlockTrue().accept(this);
        } else {
            if (ifStmt.getBlockElse() != null)
                ifStmt.getBlockElse().accept(this);
        }
        contexts.pop();
    }

    @Override
    public void visit(LiteralStatement literalStmt) {}

    @Override
    public void visit(ReturnStatement returnStmt) throws Exception {
        if (returnStmt.getExpression() != null)
            returnStmt.getExpression().accept(this);
        functionReturn = true;
    }

    @Override
    public void visit(WhileStatement whileStmt) throws Exception {
        contexts.push(new Context());
        createNewContext = false;
        whileStmt.getCondition().accept(this);
        Value condition = results.pop();
        while (isValueTrue(condition)){
            whileStmt.getBlock().accept(this);
            whileStmt.getCondition().accept(this);
            condition = results.pop();
        }
        createNewContext = true;
        contexts.pop();
    }

    @Override
    public void visit(Block block) throws Exception {
        if (createNewContext)
            contexts.push(new Context());
        ArrayList<IStatement> stmts = block.getStmts();
        for (IStatement stmt : stmts){
            stmt.accept(this);
            if (functionReturn)
                break;
        }
        if (createNewContext)
            contexts.pop();
    }

    @Override
    public void visit(FunctionCall funcCall) throws Exception {
        int size = results.size();
        String name = funcCall.getName();
        if (!functions.containsKey(name) && !classNames.contains(name))
            throw new MissingPartException("function definition for " + name, "program");
        else if (classNames.contains(name)){
            Value valueObj = createObjValue(name, funcCall);
            results.push(valueObj);
        }
        else{
            contexts.push(new Context());
            addArgumentsToContext(functions.get(name).getParams(), funcCall.getArguments());
            if (functionNames.contains(name))
                functions.get(name).accept(this);
            else
                functions.get(name).getBlock().accept(this);
            contexts.pop();
        }
        functionReturn = false;
        if (!withResultStmt && results.size() > size)
            results.pop();

    }

    private Value createObjValue(String className, FunctionCall funcCall) throws Exception {
        ArrayList<IExpression> arguments = funcCall.getArguments();
        ArrayList<Value> args = new ArrayList<>();
        for (int i = 0; i < arguments.size(); ++i) {
            arguments.get(i).accept(this);
            args.add(results.pop());
        }
        Value objValue = switch (className) {
            case "Line":
                yield new Value(new Line(args.get(0), args.get(1)));
            case "List":
                yield new Value(new ListS());
            case "Point":
                yield new Value(new Point(args.get(0), args.get(1)));
            case "Figure":
                yield new Value(new Figure(args.get(0)));
            default:
                yield null;
        };
        return objValue;
    }

    @Override
    public void visit(FunctionDef funcDef) throws Exception {
        funcDef.getBlock().accept(this);
    }

    @Override
    public void visit(Value value) {}

    @Override
    public void visit(ObjectAccess objAccess) throws Exception{
        withResultStmt = true;
        if (objAccess.getExpression() instanceof FunctionCall){
            Value value = getValue(objAccess.getName());
            if (! classTypes.contains(value.getType()))
                throw new IncorrectTypeException("unknown class type", (value.getType()).toString());
            IClass obj = value.getObject();
            String method = ((FunctionCall) objAccess.getExpression()).getName();
            if (obj.containsMethod(method)){
                FunctionDef function = obj.getMethod(method);
                contexts.push(new Context());
                addArgumentsToContext(function.getParams(), ((FunctionCall) objAccess.getExpression()).getArguments());
                obj.accept(this, method);
                contexts.pop();
            }
        }
        else if (objAccess.getExpression() instanceof PrimExpression){
            Value value = getValue(objAccess.getName());
            IClass obj = value.getObject();
            objectAccess = true;
            objAccess.getExpression().accept(this);
            objectAccess = false;
            Value v = results.pop();
            if (v.getType() != ValueType.V_IDENT)
                throw new IncorrectTypeException("identifier", (v.getType()).toString());
            String method = v.getIdentifierValue();
            if (obj.containsMethod(method)){
                obj.accept(this, method);
            }
        }
        withResultStmt = false;
    }

    public void visit(Point.getX functionGetX){
        results.push(functionGetX.get());
    }

    public void visit(Point.getY functionGetY){
        results.push(functionGetY.get());
    }

    public void visit(Figure.setColor funcSetColor) throws Exception {
        Value v1 = getValue("r");
        Value v2 = getValue("g");
        Value v3 = getValue("b");
        if (v1.getType() != ValueType.V_INT || v2.getType() != ValueType.V_INT || v3.getType() != ValueType.V_INT)
            throw new IncorrectTypeException("int", "non int", "setting Figure color");
        funcSetColor.changeColors(v1.getIntValue(), v2.getIntValue(), v3.getIntValue());
    }

    public void visit(ListS.ListAddFunc funcAdd) throws Exception {
        Value v = getValue("x");
        funcAdd.add(v);
    }

    public void visit(ListS.ListRemoveFunc funcRemove) throws Exception {
        Value v = funcRemove.remove();
        results.push(v);
    }

    public void visit(ListS.ShowFigures funcShow) throws Exception {
        ArrayList<Value> list = funcShow.getList();
        if (list.size() > 0 && list.get(0).getType() != ValueType.V_FIGURE)
            throw new IncorrectTypeException("Figure", (list.get(0).getType()).toString());

        ArrayList<Figure> figures = funcShow.getListFigures(list);
        JPanel pn = new JPanel(){
            @Override
            public void paint (Graphics g0) {
                Graphics2D g = (Graphics2D)g0.create();
                for (Figure fig : figures) {
                    ArrayList<Integer> listX = new ArrayList<>();
                    ArrayList<Integer> listY = new ArrayList<>();
                    for (Point p: fig.points){
                        listX.add(p.x);
                        listY.add(p.y);
                    }
                    int[] xs = listX.stream().mapToInt(i->i).toArray();
                    int[] ys = listY.stream().mapToInt(i->i).toArray();
                    Polygon polygon0 = new Polygon(xs, ys, listX.size());
                    g.setStroke(new BasicStroke(6));
                    g.setColor(new Color(fig.colorR, fig.colorG, fig.colorB));
                    g.drawPolygon(polygon0);
                }
            }
        };
        fr.add(pn);
        fr.setVisible(true);
    }

    @Override
    public void visit(Parameter parameter) {}

    public void visit(Program program) {}

}
