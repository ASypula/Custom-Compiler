package tkom.visitor;

import tkom.exception.MissingPartException;

public interface Visitable {

    void accept(Visitor visitor);
}
