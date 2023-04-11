package tkom.lexer;

import org.apache.commons.lang3.tuple.ImmutablePair;
import tkom.common.BuffReader;
import tkom.common.Position;
import tkom.common.tokens.*;
import tkom.exception.InvalidTokenException;

import java.io.BufferedReader;
import java.io.IOException;

import static tkom.common.tokens.TokenMap.T_KEYWORDS;
import static tkom.common.tokens.TokenMap.T_SIGNS;

public class Lexer {
    boolean running;    // set to true as long as EOF is not encountered
    BuffReader br;

    Token currToken;
    char currChar;

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
        br = new BuffReader(buffRead, new Position(-1, 0));
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
        br.currPos.rowNo++;
        while (currInt != -1 && Character.isWhitespace((char)currInt)){
            if ((char)currInt == '\n') {
                br.currPos.rowNo = 0;
                br.currPos.colNo++;
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
        br.currPos.rowNo++;
        if (currInt == -1)
            running = false;
        else if ((char)currInt == '\n'){
            br.currPos.rowNo = 0;
            br.currPos.colNo++;
        }
        currChar = (char)currInt;
    }

    /**
     * Tries building one of the single or double signs from input characters
     * Example tokens are: '<' T_LESS or '==' T_EQUALS
     * @throws IOException              on BufferedReader error
     * @throws InvalidTokenException    on invalid token
     */
    public boolean tryBuildSign() throws IOException, InvalidTokenException {
        Position firstPos = new Position(br.currPos.rowNo, br.currPos.colNo);
        StringBuilder builder = new StringBuilder();
        builder.append(currChar);
        nextChar();
        builder.append(currChar);
        // First check for a two-character sign
        if (T_SIGNS.containsKey(builder.toString())){
            String newString = builder.toString();
            currToken = new Token(T_SIGNS.get(newString), firstPos);
            nextChar();
        }
        // Next check for a single character sign
        else {
            builder.deleteCharAt(builder.length() - 1);
            String newString = builder.toString();
            if (T_SIGNS.containsKey(newString))
                currToken = new Token(T_SIGNS.get(newString), firstPos);
            else
                throw new InvalidTokenException(firstPos, newString);
        }
        return true;
    }

    /**
     * Builds an identifier: either user's custom defined variable
     * or a language keyword e.g. 'if'
     * @throws IOException              on BufferedReader error
     * @throws InvalidTokenException    on too long token
     */
    public boolean tryBuildIdentifier() throws IOException, InvalidTokenException {
        if (!Character.isLetter(currChar))
            return false;
        int identLen = 0;
        Position firstPos = new Position(br.currPos.rowNo, br.currPos.colNo);
        StringBuilder builder = new StringBuilder();
        builder.append(currChar);
        nextChar();
        while (running && (Character.isLetterOrDigit(currChar) || currChar == '_') && identLen<=MAX_LENGTH){
            identLen++;
            builder.append(currChar);
            nextChar();
        }
        if (identLen > MAX_LENGTH)
            throw new InvalidTokenException(firstPos, builder.toString(), MAX_LENGTH);
        String newString = builder.toString();
        if (T_KEYWORDS.containsKey(newString))
            currToken = new Token(T_KEYWORDS.get(newString), firstPos);
        else
            currToken = new TokenString(TokenType.T_IDENT, firstPos, newString);
        return true;
    }

    /**
     * Builds a number as an integer. Counts the total number of digits to disable overflow exception
     * and to later be able to create double value by dividing int number by the digit count.
     * Separately takes care of the total and fractional part.
     * @param firstPos                  position of the first char of the token
     * @return                          pair: L - calculated number, R - count of digits
     * @throws IOException              on BufferedReader error
     * @throws InvalidTokenException    on too long token
     */
    public ImmutablePair<Integer, Integer> buildNumber(Position firstPos) throws IOException, InvalidTokenException {
        int count = 0;
        int number =0;
        while (running && Character.isDigit(currChar)) {
            count++;
            if (count>MAX_DOUBLE_PRECISION)
                throw new InvalidTokenException(firstPos, Integer.toString(number), MAX_DOUBLE_PRECISION);
            number = number*10 + Character.getNumericValue(currChar);
            nextChar();
        }
        return new ImmutablePair (number, count);
    }


    /**
     * Tries to build a number, both int and double are supported.
     * If possible, assigns newly created token to currToken.
     * @return                          If a numerical token can be created
     * @throws IOException              on BufferedReader error
     * @throws InvalidTokenException    on too long token
     */
    public boolean tryBuildIntOrDouble() throws IOException, InvalidTokenException {
        if (!Character.isDigit(currChar))
            return false;
        Position firstPos = new Position(br.currPos.rowNo, br.currPos.colNo);
        int number = 0;
        if (currChar=='0') {
            number+=Character.getNumericValue(currChar);
            // omit any additional leading zero's
            while (currChar == '0')
                nextChar();
        }
        ImmutablePair<Integer, Integer> pair = buildNumber(firstPos);
        number+=pair.left;
        if (currChar == '.'){
            nextChar();
            ImmutablePair<Integer, Integer> pair1 = buildNumber(firstPos);
            double numberD = (double)number + pair1.left/Math.pow(10, pair1.right);
            currToken = new TokenDouble(TokenType.T_DOUBLE, firstPos, numberD);
        }
        else if (pair.right > MAX_INT_PRECISION)
            currToken =  new TokenDouble(TokenType.T_DOUBLE, firstPos, number);
        else
            currToken =  new TokenInt(TokenType.T_INT, firstPos, number);
        return true;
    }

    /**
     * Depending on first character tries building either a number or an identifier
     * @return                          new Token of numerical or string type
     * @throws IOException              on BufferedReader error
     * @throws InvalidTokenException    on too long token
     */
    public boolean tryBuildNumOrIdentifier() throws IOException, InvalidTokenException {
        if (!Character.isLetterOrDigit(currChar))
            return false;
        return tryBuildIntOrDouble() || tryBuildIdentifier();
    }

    /**
     * Builds a comment that starts with '#' character and ends after a newline character.
     * Maximum possible length of comment is MAX_LENGTH.
     * Next input characters are obtained differently as newline character should not be omitted
     * as it ends the comment.
     * @return                          If Token with type T_COMMENT can be created
     * @throws IOException              on BufferedReader error
     * @throws InvalidTokenException    on too long token
     */
    public boolean tryBuildComment() throws IOException, InvalidTokenException {
        if (!(currChar =='#'))
            return false;
        int commentLen = 0;
        Position firstPos = new Position(br.currPos.rowNo, br.currPos.colNo);
        StringBuilder builder = new StringBuilder();
        while (running && currChar != '\n' && commentLen<=MAX_LENGTH){ //TODO: wszystkie znaki konca linii
            commentLen++;
            builder.append(currChar);
            nextCharCommText();
        }
        if (commentLen > MAX_LENGTH)
            throw new InvalidTokenException(firstPos, builder.toString(), MAX_LENGTH);
        if (currChar == '\n')
            nextChar();
        currToken = new Token(TokenType.T_COMMENT, firstPos);
        return true;
    }

    /**
     * Tries building a text string that starts and ends with a " character.
     * Maximum possible length of text is MAX_LENGTH.
     * Next input characters are obtained differently as escape characters need to be handled
     * and characters as tab should be included in the resulting string.
     * @return                          If new Token with type T_STRING can be created
     * @throws IOException              on BufferedReader error
     * @throws InvalidTokenException    on too long token
     */
    public boolean tryBuildString() throws IOException, InvalidTokenException {
        if (!(currChar=='\"'))
            return false;
        int stringLen = 0;
        Position firstPos = new Position(br.currPos.rowNo, br.currPos.colNo);
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
            currToken = new TokenString(TokenType.T_STRING, firstPos, builder.toString());
            return true;
        }
        else
            throw new InvalidTokenException(firstPos, builder.toString());
    }

    /**
     * Depending on first character invokes functions that will start building
     * appropriate tokens.
     */
    public Token getToken() throws IOException, InvalidTokenException {
        if (!running)
            return new Token(TokenType.T_EOF, new Position(br.currPos.rowNo, br.currPos.colNo));
        if (tryBuildNumOrIdentifier() || tryBuildComment() || tryBuildString() || tryBuildSign())
            return currToken;
        else
            throw new InvalidTokenException(br.currPos, "Something went wrong");
    }

    public boolean isRunning(){
        return running;
    }
}
