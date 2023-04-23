package tkom.components;

public class WhileStatement implements IStatement {
    IExpression condition;
    Block block;

    public WhileStatement (IExpression cond, Block blk){
        condition = cond;
        block = blk;
    }
}
