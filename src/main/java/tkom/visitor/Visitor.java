package tkom.visitor;

import tkom.components.*;
import tkom.components.expressions.*;
import tkom.components.statements.*;

public interface Visitor {

    // expressions
    void accept(AndExpression andExpr) throws Exception;
    void accept(ArithmExpression arithmExpr) throws Exception;
    void accept(Expression expr) throws Exception;
    void accept(MultExpression multExpr) throws Exception;
    void accept(PrimExpression primExpr) throws Exception;
    void accept(RelExpression relExpe) throws Exception;

    // statements
    void accept(AssignStatement assignStmt) throws Exception;
    void accept(IfStatement ifStmt) throws Exception;
    void accept(LiteralStatement literalStmt);
    void accept(PrintStatement printStmt) throws Exception;
    void accept(ReturnStatement returnStmt) throws Exception;
    void accept(WhileStatement whileStmt) throws Exception;

    // others
    void accept(Block block) throws Exception;
    void accept(FunctionCall funcCall) throws Exception;
    void accept(FunctionDef funcDef) throws Exception;
    void accept(Value value);
    void accept(ObjectAccess objAccess) throws Exception;
    void accept(Parameter parameter);
    void accept(Program program) throws Exception;

}
