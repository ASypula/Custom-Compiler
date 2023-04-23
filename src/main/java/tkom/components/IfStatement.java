package tkom.components;

public class IfStatement implements IStatement {
    IExpression condition;
    Block blockTrue;
    Block blockElse;

    public IfStatement (IExpression cond, Block blkTrue, Block blkElse){
        condition = cond;
        blockTrue = blkTrue;
        blockElse = blkElse;
    }
}
