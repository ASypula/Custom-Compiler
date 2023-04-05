package tkom.lexer;

import tkom.common.Position;
import tkom.common.Token;
import tkom.common.TokenType;
import tkom.exception.InvalidTokenException;

import java.io.BufferedReader;
import java.io.IOException;

import static tkom.common.TokenMap.T_KEYWORDS;
import static tkom.common.TokenMap.T_SIGNS;

public class Lexer {
    boolean running;
    BufferedReader br;
    public char currChar;
    Position currPos;

    static final int MAX_LENGTH = 200; // maximum number of chars available in a string or a comment
    static final int MAX_PRECISION = 20; //TODO: change and separate int from double max values

    public Lexer(BufferedReader br1) throws IOException {
        br = br1;
        currPos = new Position(-1, 0);
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
            // return;
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
            // return;
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
    public Token buildSign() throws IOException, InvalidTokenException {
        Position startToken = new Position(currPos.rowNo, currPos.colNo);
        Token newToken;
        String newString;
        StringBuilder builder = new StringBuilder();
        builder.append(currChar);
        nextChar();
        builder.append(currChar);
        if (T_SIGNS.containsKey(builder.toString())){
            newString = builder.toString();
            newToken = new Token(T_SIGNS.get(newString), newString, startToken);
            nextChar();
        }
        else {
            builder.deleteCharAt(builder.length() - 1);
            newString = builder.toString();
            if (T_SIGNS.containsKey(newString))
                newToken = new Token(T_SIGNS.get(newString), newString, startToken);
            else
                throw new InvalidTokenException(currPos, Character.toString(currChar));
        }
        return newToken;
    }

    /**
     * Tries building an identifier: either user's custom defined variable
     * or a language keyword e.g. 'if'
     * @throws IOException
     */
    public Token buildIdent() throws IOException {
        Position startToken = new Position(currPos.rowNo, currPos.colNo);
        Token t;
        StringBuilder builder = new StringBuilder();
        builder.append(currChar);
        nextChar();
        while (Character.isLetterOrDigit(currChar) || currChar == '_'){
            builder.append(currChar);
            nextChar();
        }
        String newString = builder.toString();
        if (T_KEYWORDS.containsKey(newString)){
            return new Token(T_KEYWORDS.get(newString), newString, startToken);
        } else {
            return new Token(TokenType.T_IDENT, newString, startToken);
        }
    }

    public String buildStringNumber(Position startToken) throws IOException, InvalidTokenException {
        int precision=1;
        StringBuilder builder = new StringBuilder();
        while (Character.isDigit(currChar)) {
            precision++;
            if (precision>MAX_PRECISION)
                throw new InvalidTokenException(startToken, builder.toString(), MAX_PRECISION);
            builder.append(currChar);
            nextChar();
        }
        return builder.toString();
    }

    /**
     * Tries building a regular number: both integer and double values are supported.
     */
    public Token buildNumber() throws IOException, InvalidTokenException {
        int precision = 1;
        Position startToken = new Position(currPos.rowNo, currPos.colNo);
        String number = "";
        if (currChar=='0') {
            number+=currChar;
            while (currChar == '0')
                nextChar();
        }
        number+=buildStringNumber(startToken);
        if (currChar == '.'){
            number+=currChar;
            nextChar();
            number+=buildStringNumber(startToken);
            return new Token(TokenType.T_DOUBLE, number, startToken);
        }
        return new Token(TokenType.T_INT, number, startToken);
    }

    /**
     * Depending on first character tries building either a number or an identifier
     */
    public Token buildNumOrIdent() throws IOException, InvalidTokenException {
        Token newToken;
        if (Character.isLetter(currChar))
            newToken = buildIdent();
        else
            newToken = buildNumber();
        return newToken;
    }

    /**
     * Tries building a comment that starts with '#' character and ends after a newline character.
     * Maximum possible length of comment is MAX_LENGTH.
     * Next input characters are obtained differently as newline character should not be omitted
     * as it ends the comment.
     */
    public Token buildComment() throws IOException, InvalidTokenException {
        int commentLen = 0;
        Position startToken = new Position(currPos.rowNo, currPos.colNo);
        StringBuilder builder = new StringBuilder();
        while (currChar != '\n' && commentLen<=MAX_LENGTH){
            commentLen++;
            if (commentLen > MAX_LENGTH){
                throw new InvalidTokenException(startToken, builder.toString(), MAX_LENGTH);
            }
            builder.append(currChar);
            nextCharCommText();
        }
        if (currChar == '\n')
            nextChar();
        String comment = builder.toString();
        return new Token(TokenType.T_COMMENT, comment, startToken);
    }

    /**
     * Tries building a text string that starts and ends with a " character.
     * Maximum possible length of text is MAX_LENGTH.
     * Next input characters are obtained differently as escape characters need to be handled
     * and characters as newline or tab should be included in the resulting string.
     */
    public Token buildString() throws IOException, InvalidTokenException {
        //TODO: Include also escape characters and "
        int stringLen = 0;
        Position startToken = new Position(currPos.rowNo, currPos.colNo);
        StringBuilder builder = new StringBuilder();
        nextCharCommText();
        while (currChar != '\"' && stringLen<=MAX_LENGTH){
            stringLen++;
            if (stringLen > MAX_LENGTH){
                throw new InvalidTokenException(startToken, builder.toString(), MAX_LENGTH);
            }
            builder.append(currChar);
            nextCharCommText();
        }
        return new Token(TokenType.T_STRING, builder.toString(), startToken);
    }

    /**
     * Depending on first character invokes functions that will start building
     * appropriate tokens.
     */
    public Token getToken() throws IOException, InvalidTokenException {
        Token newToken;
        //TODO: not working Token EOF
        if (!running)
            return new Token(TokenType.T_EOF, "EOF", new Position(currPos.rowNo, currPos.colNo));
        if (Character.isLetterOrDigit(currChar))
            newToken = buildNumOrIdent();
        else if (currChar=='#') {
            newToken = buildComment();
        }
        else if (currChar=='\"') {
            newToken = buildString();
        }
        else {
            newToken = buildSign();
        }
        return newToken;
    }

    public boolean ifRunning(){
        return running;
    }

}
