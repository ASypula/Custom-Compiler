package tkom.components.statements;

import tkom.common.tokens.TokenType;
import tkom.visitor.Visitor;

public class PrintStatement implements IStatement{
    TokenType tType;
    String value;

    public PrintStatement (TokenType t, String val){
        tType = t;
        value = val;
    }

    @Override
    public void accept(Visitor visitor){
        visitor.accept(this);
    }
}
