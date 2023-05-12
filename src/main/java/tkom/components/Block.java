package tkom.components;

import tkom.components.statements.IStatement;
import tkom.exception.InvalidMethodException;

import java.util.ArrayList;

public class Block implements Node{
    ArrayList<IStatement> statements;

    public Block(ArrayList<IStatement> stmts){
        statements = stmts;
    }

    public IStatement getStmt(int i) throws InvalidMethodException {
        if (i<statements.size())
            return statements.get(i);
        else
            throw new InvalidMethodException("non-existing statement number", "block");
    }
}
