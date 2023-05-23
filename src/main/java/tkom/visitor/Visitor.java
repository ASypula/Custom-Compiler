package tkom.visitor;

import tkom.components.*;
import tkom.components.expressions.*;
import tkom.components.statements.*;

public interface Visitor {

    // expressions
    void visit(AndExpression andExpr) throws Exception;
    void visit(ArithmExpression arithmExpr) throws Exception;
    void visit(Expression expr) throws Exception;
    void visit(MultExpression multExpr) throws Exception;
    void visit(PrimExpression primExpr) throws Exception;
    void visit(RelExpression relExpe) throws Exception;

    // statements
    void visit(AssignStatement assignStmt) throws Exception;
    void visit(IfStatement ifStmt) throws Exception;
    void visit(LiteralStatement literalStmt);
    void visit(PrintStatement printStmt) throws Exception;
    void visit(ReturnStatement returnStmt) throws Exception;
    void visit(WhileStatement whileStmt) throws Exception;

    // others
    void visit(Block block) throws Exception;
    void visit(FunctionCall funcCall) throws Exception;
    void visit(FunctionDef funcDef) throws Exception;
    void visit(Value value);
    void visit(ObjectAccess objAccess) throws Exception;
    void visit(Parameter parameter);
    void visit(Program program) throws Exception;

}
