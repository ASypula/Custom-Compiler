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

    static final int MAX_LENGTH = 200; // maximum number of chars available in a string or a comment

    public Lexer(BufferedReader br1) throws IOException {

        tokens = new ArrayList<Token>();
        br = br1;
        currPos = new Position();
        currPos.rowNo = 0;
        currPos.colNo = 0;
        running = true;
        nextChar();
    }

    /**
     * Assigns next character from the input stream to current character,
     * but omits any whitespace characters.
     * On EOF character finishes and makes the Lexer stop the work.
     * @throws IOException
     */
    public void nextChar() throws IOException {
        int currInt=br.read();
        currPos.rowNo++;
        while (currInt != -1 && Character.isWhitespace((char)currInt)){
            if ((char)currInt == '\n')
                currPos.colNo++;
            currInt=br.read();
        }
        if (currInt == -1) {
            running = false;
            // End of file
            //TODO: smarter return
        }
        currChar = (char)currInt;
    }

    public void nextCharCommText() throws IOException {
        int currInt=br.read();
        currPos.rowNo++;
        if (currInt == -1) {
            running = false;
            // End of file
            //TODO: smarter return
        }
        if ((char)currInt == '\n')
            currPos.colNo++;
        currChar = (char)currInt;
    }

    /**
     * Tries building one of the single or double signs from input characters
     * Example tokens are: '<' T_LESS or '==' T_EQUALS
     * @throws IOException
     */

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

    /**
     * Tries building an identifier: either user's custom defined variable
     * or a language keyword e.g. 'if'
     * @throws IOException
     */
    public void tryBuildIdent() throws IOException {
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

    /**
     * Tries building a regular number: both integer and double values are supported.
     * @throws IOException
     */
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

    /**
     * Depending on first character tries building either a number or an identifier
     * @throws IOException
     */
    public void tryBuildNumOrIdent() throws IOException {
        if (Character.isLetter(currChar))
            tryBuildIdent();
        else if (Character.isDigit(currChar))
            tryBuildNumber();
        else
            System.out.println(currChar);
    }

    /**
     * Tries building a comment that starts with '#' character and ends after a newline character.
     * Maximum possible length of comment is MAX_LENGTH.
     * Next input characters are obtained differently as newline character should not be omitted
     * as it ends the comment.
     * @throws IOException
     */
    public void tryBuildComment() throws IOException {
        int commentLen = 0;
        Position startToken = currPos;
        StringBuilder builder = new StringBuilder();
        while (currChar != '\n' || commentLen>MAX_LENGTH){
            commentLen++;
            builder.append(currChar);
            nextCharCommText();
        }
        if (currChar == '\n')
            nextChar();
        if (commentLen > MAX_LENGTH){
            //TODO: throw an exception
            System.out.println("Too long comment");
        }
        String comment = builder.toString();
        Token t = new Token(TokenType.T_COMMENT, comment, startToken);
    }

    /**
     * Tries building a text string that starts and ends with a ' " ' character.
     * Maximum possible length of text is MAX_LENGTH.
     * Next input characters are obtained differently as escape characters need to be handled
     * and characters as newline or tab should be included in the resulting string.
     * @throws IOException
     */
//    public void tryBuildText() throws IOException {
//        int textLen = 0;
//        Position startToken = currPos;
//        StringBuilder builder = new StringBuilder();
//        while (currChar != '\"' || textLen>MAX_LENGTH){
//        }
//    }

    /**
     * Depending on first character invokes functions that will start building
     * appropriate tokens.
     * @throws IOException
     */
    public void getToken() throws IOException {
        if (Character.isLetterOrDigit(currChar))
            tryBuildNumOrIdent();
        else if (TMap.T_SIGNS.containsKey(Character. toString(currChar))){
            tryBuildSign();
        }
        else if (currChar=='#') {
            tryBuildComment();
        }
//        else if (currChar=='\"') {
//            tryBuildText();
//        }
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
