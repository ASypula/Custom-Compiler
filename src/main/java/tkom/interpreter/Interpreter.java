package tkom.interpreter;

import tkom.common.ParserComponentTypes.ValueType;
import tkom.components.*;
import tkom.components.expressions.*;
import tkom.components.statements.*;
import tkom.exception.*;
import tkom.visitor.Visitor;

import java.util.*;
import java.util.stream.Collectors;

public class Interpreter implements Visitor {

    private final HashMap<String, FunctionDef> functions;
    private Deque<Context> contexts;

    private Stack<Value> results;

    private String mainFunc = "main";
    private ArrayList<String> classNames = new ArrayList<>(Arrays.asList("Point", "Figure", "FigCollection", "Line", "List"));

    private double epsilon = 10^-6;
    public Interpreter(HashMap<String, FunctionDef> funcs) throws MissingPartException {
        functions = funcs;
        contexts = new ArrayDeque<>();
        results =new Stack<>();
//        if (!functions.containsKey(mainFunc))
//            throw new MissingPartException("main function", "runMain in interpreter");
    }

    private void assureValue(ValueType type, Value value, String place) throws IncorrectValueException {
        if (type != value.getType())
            throw new IncorrectValueException(place, value.getType().name(), type.name());
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

    private boolean isNumber(Value value){
        return testValueType(ValueType.V_INT, value) || testValueType(ValueType.V_DOUBLE, value);
    }

    /**
     * Check if provided identifier can be used as variable name:
     * cannot be a name of a class nor a function name
     */
    private boolean isCorrectIdentifier(String identifier){
        //TODO:
        return true;
//        if (classNames.contains(identifier))
//            return false;
//        ArrayList<String> listOfKeys
//                = functions.keySet().stream().collect(
//                Collectors.toCollection(ArrayList::new));
//        return !listOfKeys.contains(identifier);
    }

    private boolean isDifferentType(Value x, Value y){
        return x.getType() != y.getType();
    }

    private void updateContext(String identifier, Value value) throws IncorrectTypeException {
        Context currContext = contexts.pop();
        if (currContext.map.containsKey(identifier) && isDifferentType(value, currContext.map.get(identifier)))
            throw new IncorrectTypeException(value.getType().toString(), currContext.map.get(identifier).getType().toString());
        currContext.map.put(identifier, value);
        contexts.push(currContext);
    }

    private Value getValue(String name) throws UnknownVariableException {
        Iterator<Context> it = contexts.iterator();
        Context context;
        while (it.hasNext()) {
            context = it.next();
            if (context.map.containsKey(name))
                return context.map.get(name);
        }
        throw new UnknownVariableException(name);
    }

    @Override
    public void accept(AndExpression andExpr) throws Exception {
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
    public void accept(ArithmExpression arithmExpr) throws Exception {
        arithmExpr.left.accept(this);
        Value result = results.pop();
        if (arithmExpr.right != null) {
            arithmExpr.right.accept(this);
            Value element = results.pop();
            if (!isNumber(element) || !isNumber(result))
                throw new InvalidMethodException("Non numerical value", "arithmetic operation");
            if (arithmExpr.isSubtraction())
                result = arithmExpr.subtract(result, element);
            else
                result = arithmExpr.add(result, element);
        }
        results.push(result);
    }

    @Override
    public void accept(Expression expr) throws Exception {
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

    @Override
    public void accept(MultExpression multExpr) throws Exception {
        multExpr.left.accept(this);
        Value result = results.pop();
        if (multExpr.right != null) {
            multExpr.right.accept(this);
            Value element = results.pop();
            if (!isNumber(element) || !isNumber(result))
                throw new InvalidMethodException("Non numerical value", "multiplication");
            if (multExpr.isDivision())
                result = multExpr.divide(result, element);
            else
                result = multExpr.multiply(result, element);
        }
        results.push(result);
    }

    @Override
    public void accept(PrimExpression primExpr) throws Exception {
        Value value = primExpr.value;
        if (testValueType(ValueType.V_IDENT, value)){
            if (isCorrectIdentifier(value.getIdentifierValue())){
                results.push(getValue(value.getIdentifierValue()));
                return;
            }
        }
        results.push(primExpr.value);
    }

    @Override
    public void accept(RelExpression relExpr) throws Exception {
        relExpr.left.accept(this);
        Value result = results.pop();
        if (relExpr.right == null)
            results.push(result);
        else {
            //TODO
        }
    }

    @Override
    public void accept(AssignStatement assignStmt) throws Exception {
        if (!isCorrectIdentifier(assignStmt.getIdentifier()))
            throw new IncorrectValueException("AssignStatement", "built-in name: class or function name", "identifier");
        assignStmt.getExpression().accept(this);
        Value result = results.pop();
        updateContext(assignStmt.getIdentifier(), result);
    }

    @Override
    public void accept(IfStatement ifStmt) {

    }

    @Override
    public void accept(LiteralStatement literalStmt) {

    }

    @Override
    public void accept(PrintStatement printStmt) {

    }

    @Override
    public void accept(ReturnStatement returnStmt) {

    }

    @Override
    public void accept(WhileStatement whileStmt) {

    }

    @Override
    public void accept(Block block) throws Exception {
        contexts.push(new Context());
        ArrayList<IStatement> stmts = block.getStmts();
        for (IStatement stmt : stmts){
            stmt.accept(this);
        }
        contexts.pop();
    }

    @Override
    public void accept(FunctionCall funcCall) {

    }

    @Override
    public void accept(FunctionDef funcDef) {

    }

    @Override
    public void accept(Value value) {

    }

    @Override
    public void accept(ObjectAccess objAccess) {

    }

    @Override
    public void accept(Parameter parameter) {

    }

    public void accept(Program program) {

    }

}
