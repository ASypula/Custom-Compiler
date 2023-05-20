package tkom.exception;

public class IncorrectValueException extends Exception{

    public IncorrectValueException(String place, String received, String expected){
        super("In " + place + " expected " + expected + " but got " + received);
    }
}
