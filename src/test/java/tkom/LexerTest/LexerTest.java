package tkom.LexerTest;

import org.junit.Test;
import tkom.common.ExceptionHandler;
import tkom.common.Position;
import tkom.common.tokens.Token;
import tkom.common.tokens.TokenType;
import tkom.exception.ExceededLimitsException;
import tkom.exception.InvalidTokenException;
import tkom.lexer.Lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class LexerTest {

    private Lexer myLexer;
    double epsilon = 0.000001;

    private void initLexer(String input) throws IOException {
        InputStream initialStream = new ByteArrayInputStream(input.getBytes());
        Reader targetReader = new InputStreamReader(initialStream);
        BufferedReader br = new BufferedReader(targetReader);
        ExceptionHandler excHandler = new ExceptionHandler();
        myLexer = new Lexer(br, excHandler);
    }

    private void assertToken(Token expected, Token tested) {
        assertEquals(expected.getType(), tested.getType());
        assertEquals(expected.getPosition(), tested.getPosition());
    }

    @Test
    public void test_T_AND() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_AND, new Position(0,0));
        String x = "&&";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_ASSIGN() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_ASSIGN, new Position(0,0));
        String x = "=";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_COLON() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_COLON, new Position(0,0));
        String x = ",";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_COMMENT() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_COMMENT, new Position(0,0));
        String x = "# comment\n";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_CURLY_BRACKET_L() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_CURLY_BRACKET_L, new Position(0,0));
        String x = "{";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_CURLY_BRACKET_R() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_CURLY_BRACKET_R, new Position(0,0));
        String x = "}";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_DIV() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_DIV, new Position(0,0));
        String x = "/";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_DOT() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_DOT, new Position(0,0));
        String x = ".";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_DOUBLE() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_DOUBLE, new Position(0,0));
        String x = "12.956";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
        assertTrue(Math.abs(t.getDoubleValue() - Double.parseDouble(x)) < epsilon);
    }

    @Test
    public void test_T_DOUBLE_with0() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_DOUBLE, new Position(0,0));
        String x = "0.95";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
        assertTrue(Math.abs(t.getDoubleValue() - Double.parseDouble(x)) < epsilon);
    }

    @Test
    public void test_T_DOUBLE_withMultiple0() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_DOUBLE, new Position(0,0));
        String x = "000.095";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
        assertTrue(Math.abs(t.getDoubleValue() - Double.parseDouble(x)) < epsilon);
    }

    @Test
    public void test_T_DOUBLE_withDot() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_DOUBLE, new Position(0,0));
        String x = "12.";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
        assertTrue(Math.abs(t.getDoubleValue() - Double.parseDouble(x)) < epsilon);
    }

    @Test
    public void test_T_ELSE() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_ELSE, new Position(0,0));
        String x = "else";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_FALSE() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_FALSE, new Position(0,0));
        String x = "false";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_TRUE() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_TRUE, new Position(0,0));
        String x = "true";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_EOF() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_EOF, new Position(0,0));
        String x = "";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_EQUALS() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_EQUALS, new Position(0,0));
        String x = "==";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_FIG_COLL() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_IDENT, new Position(0,0));
        String x = "FigCollection";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_FIGURE() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_IDENT, new Position(0,0));
        String x = "Figure";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_GREATER() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_GREATER, new Position(0,0));
        String x = ">";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_GREATER_OR_EQ() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_GREATER_OR_EQ, new Position(0,0));
        String x = ">=";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_IDENT1() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_IDENT, new Position(0,0));
        String x = "value1";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
        assertEquals(t.getStringValue(), x);
    }

    @Test
    public void test_T_IDENT2() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_IDENT, new Position(0,0));
        String x = "value_if2";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
        assertEquals(t.getStringValue(), x);
    }

    @Test
    public void test_T_IF() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_IF, new Position(0,0));
        String x = "if";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }
    @Test
    public void test_T_INT() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_INT, new Position(0,0));
        String x = "123";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
        assertEquals(Integer.parseInt(x), t.getIntValue());
    }

    @Test
    public void test_T_LESS() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_LESS, new Position(0,0));
        String x = "<";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_LESS_OR_EQ() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_LESS_OR_EQ, new Position(0,0));
        String x = "<=";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_LINE() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_IDENT, new Position(0,0));
        String x = "Line";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_LIST() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_IDENT, new Position(0,0));
        String x = "List";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_MINUS() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_MINUS, new Position(0,0));
        String x = "-";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_MULT() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_MULT, new Position(0,0));
        String x = "*";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_NOT() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_NOT, new Position(0,0));
        String x = "!";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_NOT_EQ() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_NOT_EQ, new Position(0,0));
        String x = "!=";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_OR() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_OR, new Position(0,0));
        String x = "||";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_PLUS() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_PLUS, new Position(0,0));
        String x = "+";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_POINT() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_IDENT, new Position(0,0));
        String x = "Point";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_PRINT() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_IDENT, new Position(0,0));
        String x = "print";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_REG_BRACKET_L() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_REG_BRACKET_L, new Position(0,0));
        String x = "(";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_REG_BRACKET_R() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_REG_BRACKET_R, new Position(0,0));
        String x = ")";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_RETURN() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_RETURN, new Position(0,0));
        String x = "return";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_SEMICOLON() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_SEMICOLON, new Position(0,0));
        String x = ";";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_STRING() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_STRING, new Position(0,0));
        String x = "\"Hello\"";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_WHILE() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_WHILE, new Position(0,0));
        String x = "while";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_IDENT_while() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_IDENT, new Position(0,0));
        String x = "whileident";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
        assertEquals(x, t.getStringValue());
    }

    //poniedzialek 12:30
    @Test
    public void test_T_STRING_withNewline() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_STRING, new Position(0,0));
        String x = "\"Hello\nt\"";
        String w = "Hello\nt";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
        assertEquals(w, t.getStringValue());
    }

    @Test
    public void test_T_STRING_withEscapeChar() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_STRING, new Position(0,0));
        String x = "\"\\\"name\\\"\"";
        String w = "\"name\"";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
        assertEquals(w, t.getStringValue());
    }
    @Test
    public void test_T_STRING_withSlashChar() throws IOException, InvalidTokenException, ExceededLimitsException {
        Token tokenExp=new Token(TokenType.T_STRING, new Position(0,0));
        String x = "\"\\help\"";
        String w = "\\help";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
        assertEquals(w, t.getStringValue());
    }

    @Test
    public void testException_unknownChar() throws Exception {
        Position pos = new Position(0,0);
        String x = "%";
        initLexer(x);
        myLexer.getToken();
        Exception exception = myLexer.excHandler.get(0);
        String expectedMessage = "Invalid token " + x + " at the position: " + pos;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testException_tooLongComment() throws IOException {
        int size = 210;
        char[] longArray = new char[size];
        for (int i=0; i<size; i++)
            longArray[i] = 'x';
        String x = "#" + Arrays.toString(longArray);
        initLexer(x);
        assertThrows(ExceededLimitsException.class, () -> myLexer.getToken());
    }

    @Test
    public void testException_overflow() throws IOException {
        String x = "9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999";
        initLexer(x);
        assertThrows(ExceededLimitsException.class, () -> myLexer.getToken());
    }

    @Test
    public void testException_invalidString() throws IOException {
        String x = "\"No end";
        initLexer(x);
        assertThrows(InvalidTokenException.class, () -> myLexer.getToken());
    }

    @Test
    public void test_Sequence1() throws IOException, InvalidTokenException, ExceededLimitsException {
        ArrayList<Token> expectedTokens = new ArrayList<>();
        expectedTokens.add(new Token(TokenType.T_WHILE, new Position(0,0)));
        expectedTokens.add(new Token(TokenType.T_REG_BRACKET_L, new Position(6,0)));
        expectedTokens.add(new Token(TokenType.T_IDENT, new Position(7,0)));
        expectedTokens.add(new Token(TokenType.T_LESS, new Position(9,0)));
        expectedTokens.add(new Token(TokenType.T_INT, new Position(10,0)));
        expectedTokens.add(new Token(TokenType.T_REG_BRACKET_R, new Position(12,0)));
        expectedTokens.add(new Token(TokenType.T_IDENT, new Position(0,1)));
        expectedTokens.add(new Token(TokenType.T_REG_BRACKET_L, new Position(5,1)));
        expectedTokens.add(new Token(TokenType.T_STRING, new Position(6,1)));
        expectedTokens.add(new Token(TokenType.T_REG_BRACKET_R, new Position(13,1)));
        expectedTokens.add(new Token(TokenType.T_SEMICOLON, new Position(14,1)));
        String x = "while   (i < 20)\n print(\"Hello\");";
        ArrayList<Token> returnedTokens = new ArrayList<>();
        initLexer(x);
        while (myLexer.isRunning()) {
            Token newToken = myLexer.getToken();
            returnedTokens.add(newToken);
        }
        for (int i = 0; i<returnedTokens.size(); i++)
            assertToken(expectedTokens.get(i), returnedTokens.get(i));
        Token newToken = myLexer.getToken();
        assertToken(newToken, new Token(TokenType.T_EOF, new Position(15,1)));
    }


}