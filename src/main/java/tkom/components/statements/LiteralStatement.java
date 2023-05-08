package tkom.components.statements;

public class LiteralStatement implements IStatement{
    String identifier;

    public LiteralStatement(String ident){
        identifier = ident;
    }

    public String getIdentifier(){
        return identifier;
    }
}
