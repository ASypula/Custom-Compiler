package tkom.components.statements;

import tkom.common.tokens.TokenType;

public class PrintStatement implements IStatement{
    TokenType tType;
    String value;

    public PrintStatement (TokenType t, String val){
        tType = t;
        value = val;
    }
}
