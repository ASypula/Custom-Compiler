package tkom.exception;

public class InvalidMethodException extends Exception{
    public InvalidMethodException(String element, String funcPlace){
        super(funcPlace + " should not have the " + element);
    }
}
