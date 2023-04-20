package tkom.lexer;

import org.apache.commons.lang3.tuple.ImmutablePair;
import tkom.common.BuffReader;
import tkom.common.ExceptionHandler;
import tkom.common.Position;
import tkom.common.tokens.*;
import tkom.exception.ExceededLimitsException;
import tkom.exception.InvalidTokenException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static tkom.common.tokens.TokenMap.*;

//escape characters handled correctly in the string
//overflow in numbers
//more tests for errors
//different error classes
//change of name buildIdentifier -> buildIdentifierOrKeyword

public class Lexer {
    boolean running;    // set to true as long as EOF is not encountered
    BuffReader br;
    public ExceptionHandler excHandler;

    Token currToken;
    char currChar;

    static final int MAX_LENGTH = 200; // maximum number of chars available in a string or a comment

    List<Character> newlineChars = Arrays.asList('\n', '\r');

    /**
     * Lexer constructor
     * Already the first character from the input is obtained
     * @param buffRead          BufferedReader for the input either String or a file
     * @param eh                ExceptionHandler for storing non-critical exceptions
     * @throws IOException      on BufferedReader error
     */
    public Lexer(BufferedReader buffRead, ExceptionHandler eh) throws IOException {
        br = new BuffReader(buffRead, new Position(-1, 0));
        excHandler = eh;
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
            if (newlineChars.contains((char)currInt)) {
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
        else if (newlineChars.contains((char)currInt)){
            br.currPos.rowNo = 0;
            br.currPos.colNo++;
        }
        currChar = (char)currInt;
    }

    /**
     * Tries building one of the single or double signs from input characters
     * Example tokens are: '<' T_LESS or '==' T_EQUALS
     * @throws IOException              on BufferedReader error
     * may throw InvalidTokenException  on invalid token
     */
    public boolean tryBuildSign() throws IOException {
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
                excHandler.add(new InvalidTokenException(firstPos, newString));
        }
        return true;
    }

    /**
     * Builds an identifier or a keyword: either user's custom defined variable
     * or a language keyword e.g. 'if'
     * @throws IOException              on BufferedReader error
     * @throws ExceededLimitsException    on too long token
     */
    public boolean tryBuildIdentifierOrKeyword() throws IOException, ExceededLimitsException {
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
            throw new ExceededLimitsException(firstPos, builder.toString(), MAX_LENGTH);
        String newString = builder.toString();
        if (T_KEYWORDS.containsKey(newString))
            currToken = new Token(T_KEYWORDS.get(newString), firstPos);
        else
            currToken = new TokenString(TokenType.T_IDENT, firstPos, newString);
        return true;
    }

    /**
     * Builds a number as a double. Counts the total number of digits to allow later
     * creation of floating point number by dividing double number by the digit count.
     * Separately takes care of the total and fractional part.
     * @param firstPos                  position of the first char of the token
     * @return                          pair: L - calculated number, R - count of digits
     * @throws IOException              on BufferedReader error
     * @throws ExceededLimitsException  on possible overflow
     */
    public ImmutablePair<Double, Integer> buildNumber(Position firstPos) throws IOException, ExceededLimitsException {
        int count = 0;
        double number =0;
        while (running && Character.isDigit(currChar)) {
            count++;
            if (number>(Double.MAX_VALUE - currChar)/10)
                throw new ExceededLimitsException(firstPos, Double.toString(number));
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
     * @throws ExceededLimitsException  on overflow
     */
    public boolean tryBuildIntOrDouble() throws IOException, ExceededLimitsException {
        if (!Character.isDigit(currChar))
            return false;
        Position firstPos = new Position(br.currPos.rowNo, br.currPos.colNo);
        double number = 0;
        if (currChar=='0') {
            number+=Character.getNumericValue(currChar);
            // omit any additional leading zero's
            while (currChar == '0')
                nextChar();
        }
        ImmutablePair<Double, Integer> pair = buildNumber(firstPos);
        number+=pair.left;
        if (currChar == '.'){
            nextChar();
            ImmutablePair<Double, Integer> pair1 = buildNumber(firstPos);
            double numberD = number + pair1.left/Math.pow(10, pair1.right);
            currToken = new TokenDouble(TokenType.T_DOUBLE, firstPos, numberD);
        }
        // check against max_int, if number > MAX_VALUE, return a double, else return int
        else if (number > Integer.MAX_VALUE)
            currToken =  new TokenDouble(TokenType.T_DOUBLE, firstPos, number);
        else
            currToken =  new TokenInt(TokenType.T_INT, firstPos, (int)number);
        return true;
    }

    /**
     * Depending on first character tries building either a number or an identifier
     * @return                          new Token of numerical or string type
     * @throws IOException              on BufferedReader error
     * @throws ExceededLimitsException    on too long token
     */
    public boolean tryBuildNumOrIdentifier() throws IOException, ExceededLimitsException {
        if (!Character.isLetterOrDigit(currChar))
            return false;
        return tryBuildIntOrDouble() || tryBuildIdentifierOrKeyword();
    }

    /**
     * Builds a comment that starts with '#' character and ends after a newline character.
     * Maximum possible length of comment is MAX_LENGTH.
     * Next input characters are obtained differently as newline character should not be omitted
     * as it ends the comment.
     * @return                          If Token with type T_COMMENT can be created
     * @throws IOException              on BufferedReader error
     * @throws ExceededLimitsException    on too long token
     */
    public boolean tryBuildComment() throws IOException, ExceededLimitsException {
        if (!(currChar =='#'))
            return false;
        int commentLen = 0;
        Position firstPos = new Position(br.currPos.rowNo, br.currPos.colNo);
        StringBuilder builder = new StringBuilder();
        while (running && !newlineChars.contains(currChar) && commentLen<=MAX_LENGTH){
            commentLen++;
            builder.append(currChar);
            nextCharCommText();
        }
        if (commentLen > MAX_LENGTH)
            throw new ExceededLimitsException(firstPos, builder.toString(), MAX_LENGTH);
        if (newlineChars.contains(currChar))
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
     * @throws ExceededLimitsException  on too long token
     * @throws InvalidTokenException    on invalid token
     */
    public boolean tryBuildString() throws IOException, ExceededLimitsException, InvalidTokenException {
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
                //TODO: done
                if (T_ESCAPECHARS.containsKey(currChar))
                    builder.append(T_ESCAPECHARS.get(currChar));
                else{
                    builder.append('\\');
                    builder.append(currChar);
                }
            }
            else
                builder.append(currChar);
            nextCharCommText();
        }
        if (stringLen > MAX_LENGTH)
            throw new ExceededLimitsException(firstPos, builder.toString(), MAX_LENGTH);
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
    public Token getToken() throws IOException, InvalidTokenException, ExceededLimitsException {
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
