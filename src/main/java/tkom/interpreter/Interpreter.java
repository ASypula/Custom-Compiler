package tkom.interpreter;

import tkom.common.ParserComponentTypes.ExpressionType;
import tkom.common.ParserComponentTypes.ValueType;
import tkom.components.*;
import tkom.components.expressions.*;
import tkom.components.statements.*;
import tkom.exception.*;
import tkom.visitor.Visitor;

import java.util.*;

public class Interpreter implements Visitor {

    public final HashMap<String, FunctionDef> functions;
    private final Deque<Context> contexts;

    private final Stack<Value> results;

    private final String mainFunc = "main";
    private final ArrayList<String> classNames = new ArrayList<>(Arrays.asList("Point", "Figure", "FigCollection", "Line", "List"));

    private final double epsilon = Math.pow(10, -6);

    private boolean createNewContext = true;
    private boolean functionReturn = false;
    public Interpreter(HashMap<String, FunctionDef> funcs) throws MissingPartException {
        functions = funcs;
        contexts = new ArrayDeque<>();
        results =new Stack<>();
    }

    public void runMain() throws Exception {
        if (!containsMain())
            throw new MissingPartException("main function", "runMain in interpreter");
        functions.get(mainFunc).accept(this);
    }

    private boolean containsMain(){
        return functions.containsKey(mainFunc);
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
//            ArrayList<String> listOfKeys
//            = functions.keySet().stream().collect(
//            Collectors.toCollection(ArrayList::new));
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
            if (notNumber(element) || notNumber(result))
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
    public void accept(PrimExpression primExpr) throws Exception {
        Value value;
        if (primExpr.type != ExpressionType.E_VALUE){
            primExpr.expr.accept(this);
            value = results.pop();
        }
        else
            value = primExpr.value;
        if (testValueType(ValueType.V_IDENT, value)){
            if (isCorrectIdentifier(value.getIdentifierValue())){
                results.push(getValue(value.getIdentifierValue()));
                return;
            }
        }
        results.push(value);
    }

    @Override
    public void accept(RelExpression relExpr) throws Exception {
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
    public void accept(AssignStatement assignStmt) throws Exception {
        if (!isCorrectIdentifier(assignStmt.getIdentifier()))
            throw new IncorrectValueException("AssignStatement", "built-in name: class or function name", "identifier");
        assignStmt.getExpression().accept(this);
        Value result = results.pop();
        updateContext(assignStmt.getIdentifier(), result);
    }

    @Override
    public void accept(IfStatement ifStmt) throws Exception{
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
    public void accept(LiteralStatement literalStmt) {}

    @Override
    public void accept(PrintStatement printStmt) throws Exception {
        if (printStmt.vType == ValueType.V_STRING)
            System.out.println(printStmt.value);
        else if (printStmt.vType == ValueType.V_INT)
            System.out.println(printStmt.value);
        else {
            Value value = getValue(printStmt.value);
            if (testValueType(ValueType.V_INT, value))
                System.out.println(value.getIntValue());
            else if (testValueType(ValueType.V_DOUBLE, value))
                System.out.println(value.getDoubleValue());
            else if (testValueType(ValueType.V_STRING, value))
                System.out.println(value.getStringValue());
            else
                throw new IncorrectValueException("PrintStatement", "non-string", "string identifier");
        }
    }

    @Override
    public void accept(ReturnStatement returnStmt) throws Exception {
        if (returnStmt.getExpression() != null)
            returnStmt.getExpression().accept(this);
        functionReturn = true;
    }

    @Override
    public void accept(WhileStatement whileStmt) throws Exception {
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
    public void accept(Block block) throws Exception {
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
    public void accept(FunctionCall funcCall) throws Exception {
        String name = funcCall.getName();
        if (!functions.containsKey(name))
            throw new MissingPartException("function definition for " + name, "program");
        contexts.push(new Context());
        addArgumentsToContext(functions.get(name).getParams(), funcCall.getArguments());
        functions.get(name).getBlock().accept(this);
        contexts.pop();
        functionReturn = false;
    }

    @Override
    public void accept(FunctionDef funcDef) throws Exception {
        funcDef.getBlock().accept(this);
    }

    @Override
    public void accept(Value value) {}

    @Override
    public void accept(ObjectAccess objAccess) {
    //TODO
    }

    @Override
    public void accept(Parameter parameter) {}

    public void accept(Program program) {}

}
