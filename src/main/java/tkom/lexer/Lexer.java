package tkom.lexer;

import tkom.common.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Lexer {
    StringBuilder builder;
    ArrayList<Token> tokens;
    BufferedReader br;
    char currChar;
    int currRow;
    int currCol;

    int MAX_LENGTH = 200; // maximum number of chars available in a string or a comment

    public Lexer(BufferedReader br1) throws IOException {
        builder = new StringBuilder();
        tokens = new ArrayList<Token>();
        currRow = 0;
        currCol = 0;
        br = br1;

        currChar = nextChar();
    }

    public char nextChar() throws IOException {
        int currInt=br.read();
        while (currInt != -1 && Character.isWhitespace((char)currInt)){
            System.out.println((char)currInt);
            currInt=br.read();
        }
        if (currInt == -1)
            // End of file
            //TODO: smarter return
            return 'o';

        return (char)currInt;
    }

    public void tryBuildString() throws IOException {
        while (Character.isLetter(currChar))
            builder.append(currChar);
        currChar=nextChar();
    }
//    public void tryBuildNumOrString(){
//        if (Character.isLetter(currChar))
//            tryBuildString();
//        else if (Character.isDigit(currChar))
//            tryBuildNumber();
//        else
//            System.out.println(currChar);
//    }

//    public void getToken() throws IOException {
//        while (currChar!='9'){
//            if (Character.isLetterOrDigit(currChar))
//                tryBuildNumOrString();
//            if (currChar=='\n')
//                System.out.println("Nowa linia");
//            currChar=(char)br.read();
//        }
//        System.out.println("jej");
//    }

}
