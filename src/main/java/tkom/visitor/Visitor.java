package tkom.visitor;

import tkom.components.*;
import tkom.components.expressions.*;
import tkom.components.statements.*;

public interface Visitor {

    // expressions
    void accept(AndExpression andExpr);
    void accept(ArithmExpression arithmExpr);
    void accept(Expression expr);
    void accept(MultExpression multExpr);
    void accept(PrimExpression primExpr);
    void accept(RelExpression relExpe);

    // statements
    void accept(AssignStatement assignStmt);
    void accept(IfStatement ifStmt);
    void accept(LiteralStatement literalStmt);
    void accept(PrintStatement printStmt);
    void accept(ReturnStatement returnStmt);
    void accept(WhileStatement whileStmt);

    // others
    void accept(Block block);
    void accept(FunctionCall funcCall);
    void accept(FunctionDef funcDef);
    void accept(Value value);
    void accept(ObjectAccess objAccess);
    void accept(Parameter parameter);
    void accept(Program program);

}
