package tkom.exception;

import tkom.common.Position;

public class DuplicatedElementException extends Exception{
    public DuplicatedElementException(String element, String name, String place) {
        super("Duplicated " + element + name + " in the " + place);
    }
}
