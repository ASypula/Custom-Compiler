package tkom.exception;

import tkom.common.Position;
import tkom.common.tokens.Token;

public class MissingPartException extends Exception{

    public MissingPartException(Token t, String missingPart, String missingFrom){
        super("Program is missing " + missingPart + " in the " + missingFrom + " at " + t.getPosition().toString());
    }
}
