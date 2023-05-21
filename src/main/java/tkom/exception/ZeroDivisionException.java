package tkom.exception;

public class ZeroDivisionException extends Exception{
    public ZeroDivisionException(){
        super("Invalid operation - division by zero");
    }
}
