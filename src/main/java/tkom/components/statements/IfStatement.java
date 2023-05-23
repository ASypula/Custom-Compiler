package tkom.components.statements;

import tkom.components.Block;
import tkom.components.expressions.IExpression;
import tkom.visitor.Visitor;

public class IfStatement implements IStatement {
    IExpression condition;
    Block blockTrue;
    Block blockElse;

    public IfStatement (IExpression cond, Block blkTrue, Block blkElse){
        condition = cond;
        blockTrue = blkTrue;
        blockElse = blkElse;
    }

    public IExpression getCondition(){
        return condition;
    }

    public Block getBlockTrue(){
        return blockTrue;
    }

    public Block getBlockElse(){
        return blockElse;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        visitor.visit(this);
    }
}
