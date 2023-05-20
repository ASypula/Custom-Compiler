package tkom.components;

import tkom.components.statements.IStatement;
import tkom.exception.InvalidMethodException;
import tkom.visitor.Visitable;
import tkom.visitor.Visitor;

import java.util.ArrayList;

public class Block implements Visitable {
    ArrayList<IStatement> statements;

    public Block(ArrayList<IStatement> stmts){
        statements = stmts;
    }

    public ArrayList<IStatement> getStmts(){
        return statements;
    }

    public IStatement getStmt(int i) throws InvalidMethodException {
        if (i<statements.size())
            return statements.get(i);
        else
            throw new InvalidMethodException("non-existing statement number", "block");
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.accept(this);
    }
}
