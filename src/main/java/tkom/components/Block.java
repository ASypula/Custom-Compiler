package tkom.components;

import tkom.components.statements.IStatement;

import java.util.ArrayList;

public class Block implements Node{
    ArrayList<IStatement> statements;

    public Block(ArrayList<IStatement> stmts){
        statements = stmts;
    }
}
