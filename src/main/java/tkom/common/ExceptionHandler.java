package tkom.common;

import java.util.ArrayList;

public class ExceptionHandler {
    ArrayList<Exception> exceptions = new ArrayList<Exception>();

    public ExceptionHandler(){};

    public void add(Exception e){
        exceptions.add(e);
    }

    public Exception get(int i) throws Exception {
        if (exceptions.size()>=i)
            return exceptions.get(i);
        else
            throw new Exception("Token with given index not available");
    }
}
