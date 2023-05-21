package tkom.exception;

public class IncorrectTypeException extends Exception {
    public IncorrectTypeException (String expected, String received){
        super("Variable was already assigned with type " + expected + " and type " + received + " was evaluated");
    }
}
