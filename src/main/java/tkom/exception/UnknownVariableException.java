package tkom.exception;

public class UnknownVariableException extends Exception {
    public UnknownVariableException (String name){
        super("Unknown variable " + name + " cannot be found in any context");
    }
}
