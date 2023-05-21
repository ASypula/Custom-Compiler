package tkom.exception;

public class OverflowException extends Exception{
    public OverflowException(String operation){
        super("Overflow occurred in operation: " + operation);
    }
}
