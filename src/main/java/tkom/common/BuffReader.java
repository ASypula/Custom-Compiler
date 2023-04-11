package tkom.common;

import java.io.BufferedReader;
import java.io.IOException;

public class BuffReader {
    BufferedReader reader;
    public Position currPos;

    public BuffReader(BufferedReader br1, Position pos){
        reader = br1;
        currPos = pos;
    }

    public int read() throws IOException {
        return reader.read();
    }
}
