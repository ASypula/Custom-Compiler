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
    boolean running;    // set to true as long as EOF is not encountered
    BufferedReader br;
    char currChar;
    Position currPos;

    static final int MAX_LENGTH = 200; // maximum number of chars available in a string or a comment
    static final int MAX_INT_PRECISION = 9;
    static final int MAX_DOUBLE_PRECISION = 32;

    /**
     * Lexer constructor
     * Already the first character from the input is obtained
     * @param buffRead          BufferedReader for the input either String or a file
     * @throws IOException      on BufferedReader error
     */
    public Lexer(BufferedReader buffRead) throws IOException {
        br = buffRead;
        currPos = new Position(-1, 0);
        running = true;
        nextChar();
    }

    /**
     * Assigns next character from the input stream to current character,
     * but omits any whitespace characters.
     * On EOF character finishes and makes the Lexer stop the work.
     * @throws IOException      on BufferedReader error
     */
    public void nextChar() throws IOException {
        int currInt=br.read();
        currPos.rowNo++;
        while (currInt != -1 && Character.isWhitespace((char)currInt)){
            if ((char)currInt == '\n') {
                currPos.rowNo = 0;
                currPos.colNo++;
            }
            currInt=br.read();
        }
        if (currInt == -1)
            running = false;
        currChar = (char)currInt;
    }

    /**
     * Assigns next character from the input stream to current character,
     * but retains all whitespace characters.
     * On EOF character finishes and makes the Lexer stop the work.
     * @throws IOException      on BufferedReader error
     */
    public void nextCharCommText() throws IOException {
        int currInt=br.read();
        currPos.rowNo++;
        if (currInt == -1)
            running = false;
        else if ((char)currInt == '\n'){
            currPos.rowNo = 0;
            currPos.colNo++;
        }
        currChar = (char)currInt;
    }

    /**
     * Tries building one of the single or double signs from input characters
     * Example tokens are: '<' T_LESS or '==' T_EQUALS
     * @throws IOException              on BufferedReader error
     * @throws InvalidTokenException    on invalid token
     */
    public Token buildSign() throws IOException, InvalidTokenException {
        Position firstPos = new Position(currPos.rowNo, currPos.colNo);
        Token newToken;
        StringBuilder builder = new StringBuilder();
        builder.append(currChar);
        nextChar();
        builder.append(currChar);
        // First check for a two-character sign
        if (T_SIGNS.containsKey(builder.toString())){
            String newString = builder.toString();
            newToken = new Token(T_SIGNS.get(newString), newString, firstPos);
            nextChar();
        }
        // Next check for a single character sign
        else {
            builder.deleteCharAt(builder.length() - 1);
            String newString = builder.toString();
            if (T_SIGNS.containsKey(newString))
                newToken = new Token(T_SIGNS.get(newString), newString, firstPos);
            else
                throw new InvalidTokenException(firstPos, newString);
        }
        return newToken;
    }

    /**
     * Builds an identifier: either user's custom defined variable
     * or a language keyword e.g. 'if'
     * @throws IOException              on BufferedReader error
     */
    public Token buildIdentifier() throws IOException {
        Position firstPos = new Position(currPos.rowNo, currPos.colNo);
        StringBuilder builder = new StringBuilder();
        builder.append(currChar);
        nextChar();
        while (running && Character.isLetterOrDigit(currChar) || currChar == '_'){
            builder.append(currChar);
            nextChar();
        }
        String newString = builder.toString();
        return new Token(T_KEYWORDS.getOrDefault(newString, TokenType.T_IDENT), newString, firstPos);
    }

    /**
     * Builds a number as a String.
     * Separately takes care of the total and fractional part.
     * @param firstPos                  position of the first char of the token
     * @return                          part of the number as a String
     * @throws IOException              on BufferedReader error
     * @throws InvalidTokenException    on too long token
     */
    public String buildStringNumber(Position firstPos) throws IOException, InvalidTokenException {
        int precision=1;
        StringBuilder builder = new StringBuilder();
        while (running && Character.isDigit(currChar)) {
            precision++;
            if (precision>MAX_DOUBLE_PRECISION)
                throw new InvalidTokenException(firstPos, builder.toString(), MAX_DOUBLE_PRECISION);
            builder.append(currChar);
            nextChar();
        }
        return builder.toString();
    }


    /**
     * Builds a number, both Int and Double are supported.
     * @return                          new numerical Token of type: T_INT or T_DOUBLE
     * @throws IOException              on BufferedReader error
     * @throws InvalidTokenException    on too long token
     */
    public Token buildNumber() throws IOException, InvalidTokenException {
        Position firstPos = new Position(currPos.rowNo, currPos.colNo);
        String number = "";
        if (currChar=='0') {
            number+=currChar;
            // omit any additional leading zero's
            while (currChar == '0')
                nextChar();
        }
        number+=buildStringNumber(firstPos);
        if (currChar == '.'){
            number+=currChar;
            nextChar();
            number+=buildStringNumber(firstPos);
            return new Token(TokenType.T_DOUBLE, number, firstPos);
        }
        if (number.length() > MAX_INT_PRECISION)
            return new Token(TokenType.T_DOUBLE, number, firstPos);
        return new Token(TokenType.T_INT, number, firstPos);
    }

    /**
     * Depending on first character tries building either a number or an identifier
     * @return                          new Token of numerical or string type
     * @throws IOException              on BufferedReader error
     * @throws InvalidTokenException    on too long token
     */
    public Token buildNumOrIdentifier() throws IOException, InvalidTokenException {
        Token newToken;
        if (Character.isLetter(currChar))
            newToken = buildIdentifier();
        else
            newToken = buildNumber();
        return newToken;
    }

    /**
     * Builds a comment that starts with '#' character and ends after a newline character.
     * Maximum possible length of comment is MAX_LENGTH.
     * Next input characters are obtained differently as newline character should not be omitted
     * as it ends the comment.
     * @return                          new Token with type T_COMMENT
     * @throws IOException              on BufferedReader error
     * @throws InvalidTokenException    on too long token
     */
    public Token buildComment() throws IOException, InvalidTokenException {
        int commentLen = 0;
        Position firstPos = new Position(currPos.rowNo, currPos.colNo);
        StringBuilder builder = new StringBuilder();
        while (running && currChar != '\n' && commentLen<=MAX_LENGTH){
            commentLen++;
            builder.append(currChar);
            nextCharCommText();
        }
        if (commentLen > MAX_LENGTH)
            throw new InvalidTokenException(firstPos, builder.toString(), MAX_LENGTH);
        if (currChar == '\n')
            nextChar();
        String comment = builder.toString();
        return new Token(TokenType.T_COMMENT, comment, firstPos);
    }

    /**
     * Tries building a text string that starts and ends with a " character.
     * Maximum possible length of text is MAX_LENGTH.
     * Next input characters are obtained differently as escape characters need to be handled
     * and characters as tab should be included in the resulting string.
     * @return                          new Token with type T_STRING
     * @throws IOException              on BufferedReader error
     * @throws InvalidTokenException    on too long token
     */
    public Token buildString() throws IOException, InvalidTokenException {
        int stringLen = 0;
        Position firstPos = new Position(currPos.rowNo, currPos.colNo);
        StringBuilder builder = new StringBuilder();
        nextCharCommText();
        while (running && currChar != '\"' && stringLen<=MAX_LENGTH){
            stringLen++;
            // This may be a start of escape character
            if (currChar == '\\') {
                nextCharCommText();
                if (currChar != '\"')
                    builder.append('\\');
            }
            builder.append(currChar);
            nextCharCommText();
        }
        if (stringLen > MAX_LENGTH)
            throw new InvalidTokenException(firstPos, builder.toString(), MAX_LENGTH);
        if (currChar == '\"') {
            nextChar();
            return new Token(TokenType.T_STRING, builder.toString(), firstPos);
        }
        else
            throw new InvalidTokenException(firstPos, builder.toString());
    }

    /**
     * Depending on first character invokes functions that will start building
     * appropriate tokens.
     */
    public Token getToken() throws IOException, InvalidTokenException {
        Token newToken;
        if (!running)
            return new Token(TokenType.T_EOF, "EOF", new Position(currPos.rowNo, currPos.colNo));
        if (Character.isLetterOrDigit(currChar))
            newToken = buildNumOrIdentifier();
        else if (currChar=='#')
            newToken = buildComment();
        else if (currChar=='\"')
            newToken = buildString();
        else
            newToken = buildSign();
        return newToken;
    }

    public boolean isRunning(){
        return running;
    }
}
