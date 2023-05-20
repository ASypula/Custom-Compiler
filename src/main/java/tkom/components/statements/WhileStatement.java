package tkom.components.statements;

import tkom.components.Block;
import tkom.components.expressions.IExpression;
import tkom.visitor.Visitor;

public class WhileStatement implements IStatement {
    IExpression condition;
    Block block;

    public WhileStatement (IExpression cond, Block blk){
        condition = cond;
        block = blk;
    }

    public IExpression getCondition(){
        return condition;
    }

    public Block getBlock(){
        return block;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.accept(this);
    }
}
