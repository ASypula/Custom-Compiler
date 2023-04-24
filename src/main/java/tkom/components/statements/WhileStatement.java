package tkom.components.statements;

import tkom.components.Block;
import tkom.components.expressions.IExpression;

public class WhileStatement implements IStatement {
    IExpression condition;
    Block block;

    public WhileStatement (IExpression cond, Block blk){
        condition = cond;
        block = blk;
    }
}
