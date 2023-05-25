package tkom.exception;

public class IncorrectTypeException extends Exception {
    public IncorrectTypeException (String expected, String received){
        super("Variable was already assigned with type " + expected + " and type " + received + " was evaluated");
    }

    public IncorrectTypeException (String expected, String received, String place){
        super("Incorrect type in " + place + " expected " + expected + " and type " + received + " was given");
    }
}
