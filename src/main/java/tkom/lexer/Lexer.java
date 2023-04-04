package tkom.lexer;

import tkom.common.Position;
import tkom.common.Token;
import tkom.common.TokenMap;
import tkom.common.TokenType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Lexer {
    // StringBuilder builder;
    boolean running;

    TokenMap TMap;

    Token newToken;
    public ArrayList<Token> tokens;
    BufferedReader br;
    public char currChar;
    Position currPos;

    int MAX_LENGTH = 200; // maximum number of chars available in a string or a comment

    public Lexer(BufferedReader br1) throws IOException {

        tokens = new ArrayList<Token>();
        br = br1;
        currPos = new Position();
        currPos.rowNo = 0;
        currPos.colNo = 0;
        running = true;
        nextChar();
    }

    public void nextChar() throws IOException {
        int currInt=br.read();
        currPos.rowNo++;
        while (currInt != -1 && Character.isWhitespace((char)currInt)){
            if ((char)currInt == '\n')
                currPos.colNo++;
            System.out.println((char)currInt);
            currInt=br.read();
        }
        if (currInt == -1) {
            running = false;
            // End of file
            //TODO: smarter return
        }
        currChar = (char)currInt;
    }

    public void tryBuildSign() throws IOException {
        Position startToken = currPos;
        Token t;
        String newString;
        StringBuilder builder = new StringBuilder();
        builder.append(currChar);
        nextChar();
        builder.append(currChar);
        if (TMap.T_SIGNS.containsKey(builder.toString())){
            newString = builder.toString();
            t = new Token(TMap.T_SIGNS.get(newString), newString, startToken);
            nextChar();
        }
        else {
            builder.deleteCharAt(builder.length() - 1);
            newString = builder.toString();
            t = new Token(TMap.T_SIGNS.get(newString), newString, startToken);
        }
        newToken = t;
    }

    public void tryBuildString() throws IOException {
        Position startToken = currPos;
        Token t;
        StringBuilder builder = new StringBuilder();
        builder.append(currChar);
        nextChar();
        while (Character.isLetterOrDigit(currChar) || currChar == '_'){
            builder.append(currChar);
            nextChar();
        }
        String newString = builder.toString();
        if (TMap.T_KEYWORDS.containsKey(newString)){
            t = new Token(TMap.T_KEYWORDS.get(newString), newString, startToken);
        } else {
            t = new Token(TokenType.T_IDENT, newString, startToken);
        }
        newToken = t;
    }

    public void tryBuildNumber() throws IOException {
        Position startToken = currPos;
        int value = Character.getNumericValue(currChar);
        if (value != 0){
            nextChar();
            //TODO: limit number -> NOT overflow
            while (Character.isDigit(currChar)){
                value = value*10 + Character.getNumericValue(currChar);
                nextChar();
            }
        }
        if (currChar == '.'){
            //TODO: create double
        }
        Token t = new Token(TokenType.T_INT, Integer.toString(value), startToken);
        newToken = t;
    }
    public void tryBuildNumOrString() throws IOException {
        if (Character.isLetter(currChar))
            tryBuildString();
        else if (Character.isDigit(currChar))
            tryBuildNumber();
        else
            System.out.println(currChar);
    }

    public void getToken() throws IOException {
        if (Character.isLetterOrDigit(currChar))
            tryBuildNumOrString();
        else if (TMap.T_SIGNS.containsKey(Character. toString(currChar))){
            tryBuildSign();
        }
        else {
            //TODO: throw Exception
            System.out.println("Unknown sign");
            nextChar();
        }
        tokens.add(newToken);
    }

    public boolean ifRunning(){
        return running;
    }

}
