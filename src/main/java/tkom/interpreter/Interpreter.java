package tkom.interpreter;

import tkom.common.ParserComponentTypes.ValueType;
import tkom.components.*;
import tkom.components.expressions.*;
import tkom.components.statements.*;
import tkom.exception.IncorrectValueException;
import tkom.exception.MissingPartException;
import tkom.visitor.Visitor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Stack;

public class Interpreter implements Visitor {

    private final HashMap<String, FunctionDef> functions;
    private Deque<Context> contexts;

    private Stack<Value> results;

    private String mainFunc = "main";
    public Interpreter(HashMap<String, FunctionDef> funcs) throws MissingPartException {
        functions = funcs;
        contexts = new ArrayDeque<>();
        if (!functions.containsKey(mainFunc))
            throw new MissingPartException("main function", "runMain in interpreter");
    }

    private void assureValue(ValueType type, Value value, String place) throws IncorrectValueException {
        if (type != value.getType())
            throw new IncorrectValueException(place, value.getType().name(), type.name());
    }

    @Override
    public void accept(AndExpression andExpr)  {
        andExpr.left.accept(this);
        Value result = results.pop();
//        assureValue(ValueType.V_BOOL, result, "AndExpression");
        andExpr.right.accept(this);
    }

    @Override
    public void accept(ArithmExpression arithmExpr) {

    }

    @Override
    public void accept(Expression expr) {

    }

    @Override
    public void accept(MultExpression multExpr) {

    }

    @Override
    public void accept(PrimExpression primExpr) {

    }

    @Override
    public void accept(RelExpression relExpe) {

    }

    @Override
    public void accept(AssignStatement assignStmt) {

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
    public void accept(Block block) {

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
