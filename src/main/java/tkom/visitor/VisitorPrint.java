package tkom.visitor;

import org.apache.commons.lang3.StringUtils;
import tkom.components.*;
import tkom.components.expressions.*;
import tkom.components.statements.*;

public class VisitorPrint implements Visitor{

    int spaces = 0;

    @Override
    public void accept(AndExpression andExpr) throws Exception {
        print(andExpr);
        spaces += 2;
        andExpr.left.accept(this);
        andExpr.right.accept(this);
        spaces -= 2;
    }

    @Override
    public void accept(ArithmExpression arithmExpr) throws Exception {
        print(arithmExpr);
        spaces += 2;
        arithmExpr.left.accept(this);
        arithmExpr.right.accept(this);
        spaces -= 2;
    }

    @Override
    public void accept(Expression expr) throws Exception {
        print(expr);
        spaces += 2;
        expr.left.accept(this);
        expr.right.accept(this);
        spaces -= 2;
    }

    @Override
    public void accept(MultExpression multExpr) throws Exception {
        print(multExpr);
        spaces += 2;
        multExpr.left.accept(this);
        multExpr.right.accept(this);
        spaces -= 2;
    }

    @Override
    public void accept(PrimExpression primExpr) {
        print(primExpr);
    }

    @Override
    public void accept(RelExpression relExpr) throws Exception {
        print(relExpr);
        spaces += 2;
        relExpr.left.accept(this);
        relExpr.right.accept(this);
        spaces -= 2;
    }

    @Override
    public void accept(AssignStatement assignStmt) throws Exception {
        print(assignStmt);
        spaces+=2;
        assignStmt.getExpression().accept(this);
        spaces-=2;
    }

    @Override
    public void accept(IfStatement ifStmt) throws Exception {
        print(ifStmt);
        spaces+=2;
        ifStmt.getCondition().accept(this);
        ifStmt.getBlockTrue().accept(this);
        if (ifStmt.getBlockElse() != null)
            ifStmt.getBlockElse().accept(this);
        spaces-=2;
    }

    @Override
    public void accept(LiteralStatement literalStmt) {
        print(literalStmt);
    }

    @Override
    public void accept(PrintStatement printStmt) {
        print(printStmt);
    }

    @Override
    public void accept(ReturnStatement returnStmt) throws Exception {
        print(returnStmt);
        spaces+=2;
        returnStmt.getExpression().accept(this);
        spaces-=2;
    }

    @Override
    public void accept(WhileStatement whileStmt) throws Exception {
        print(whileStmt);
        spaces+=2;
        whileStmt.getCondition().accept(this);
        whileStmt.getBlock().accept(this);
        spaces-=2;
    }

    @Override
    public void accept(Block block) throws Exception {
        print(block);
        spaces += 2;
        for (IStatement statement : block.getStmts()) {
            statement.accept(this);
        }
        spaces -= 2;
    }

    @Override
    public void accept(FunctionCall funcCall) throws Exception {
        print(funcCall);
        spaces+=2;
        for (IExpression argument : funcCall.getArguments()) {
            argument.accept(this);
        }
        spaces-=2;
    }

    @Override
    public void accept(FunctionDef funcDef) throws Exception {
        print(funcDef);
        spaces+=2;
        for (Parameter param : funcDef.getParams()) {
            param.accept(this);
        }
        funcDef.getBlock().accept(this);
        spaces-=2;
    }

    @Override
    public void accept(Value value) {
        print(value);
    }

    @Override
    public void accept(ObjectAccess objAccess) throws Exception {
        print(objAccess);
        spaces+=2;
        objAccess.getParent().accept(this);
        objAccess.getExpression().accept(this);
        spaces-=2;
    }

    @Override
    public void accept(Parameter parameter) {
        print(parameter);
    }

    @Override
    public void accept(Program program) throws Exception {
        print(program);
        spaces += 2;
        for (FunctionDef funcDef : program.getFunctions()) {
            funcDef.accept(this);
        }
        spaces -= 2;
    }

    private String space() {
        return "-".repeat(spaces);
    }

    private void print(Object object) {
        System.out.println(StringUtils.left(space() + object.toString(), 300));
    }
}
