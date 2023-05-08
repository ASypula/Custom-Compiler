package tkom.exception;

public class InvalidMethodException extends Exception{
    public InvalidMethodException(String element, String funcPlace){
        super(funcPlace + " does not have the " + element);
    }
}
