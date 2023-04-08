package tkom.LexerTest;

import org.junit.Test;
import tkom.common.Position;
import tkom.common.Token;
import tkom.common.TokenType;
import tkom.exception.InvalidTokenException;
import tkom.lexer.Lexer;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.*;

public class LexerTest {

    private Lexer myLexer;

    private void initLexer(String input) throws IOException {
        InputStream initialStream = new ByteArrayInputStream(input.getBytes());
        Reader targetReader = new InputStreamReader(initialStream);
        BufferedReader br = new BufferedReader(targetReader);
        myLexer = new Lexer(br);
    }

    private void assertToken(Token expected, Token tested) {
        assertEquals(expected.getType(), tested.getType());
        assertEquals(expected.getValue(), tested.getValue());
        assertEquals(expected.getPosition(), tested.getPosition());
    }

    @Test
    public void test_T_AND() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_AND, "&&", new Position(0,0));
        String x = "&&";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_ASSIGN() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_ASSIGN, "=", new Position(0,0));
        String x = "=";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_COLON() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_COLON, ",", new Position(0,0));
        String x = ",";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_COMMENT() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_COMMENT, "# comment", new Position(0,0));
        String x = "# comment\n";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_CURLY_BRACKET_L() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_CURLY_BRACKET_L, "{", new Position(0,0));
        String x = "{";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_CURLY_BRACKET_R() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_CURLY_BRACKET_R, "}", new Position(0,0));
        String x = "}";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_DIV() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_DIV, "/", new Position(0,0));
        String x = "/";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_DOT() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_DOT, ".", new Position(0,0));
        String x = ".";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_DOUBLE() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_DOUBLE, "12.956", new Position(0,0));
        String x = "12.956";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_DOUBLE_with0() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_DOUBLE, "0.95", new Position(0,0));
        String x = "0.95";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_DOUBLE_withMultiple0() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_DOUBLE, "0.095", new Position(0,0));
        String x = "000.095";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_ELSE() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_ELSE, "else", new Position(0,0));
        String x = "else";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_EOF() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_EOF, "EOF", new Position(0,0));
        String x = "";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_EQUALS() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_EQUALS, "==", new Position(0,0));
        String x = "==";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_FIG_COLL() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_FIG_COLL, "FigCollection", new Position(0,0));
        String x = "FigCollection";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_FIGURE() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_FIGURE, "Figure", new Position(0,0));
        String x = "Figure";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_GREATER() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_GREATER, ">", new Position(0,0));
        String x = ">";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_GREATER_OR_EQ() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_GREATER_OR_EQ, ">=", new Position(0,0));
        String x = ">=";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_IDENT1() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_IDENT, "value1", new Position(0,0));
        String x = "value1";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_IDENT2() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_IDENT, "value_if2", new Position(0,0));
        String x = "value_if2";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_IF() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_IF, "if", new Position(0,0));
        String x = "if";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }
    @Test
    public void test_T_INT() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_INT, "123", new Position(0,0));
        String x = "123";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_LESS() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_LESS, "<", new Position(0,0));
        String x = "<";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_LESS_OR_EQ() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_LESS_OR_EQ, "<=", new Position(0,0));
        String x = "<=";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_LINE() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_LINE, "Line", new Position(0,0));
        String x = "Line";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_LIST() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_LIST, "List", new Position(0,0));
        String x = "List";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_MINUS() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_MINUS, "-", new Position(0,0));
        String x = "-";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_MULT() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_MULT, "*", new Position(0,0));
        String x = "*";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_NOT() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_NOT, "!", new Position(0,0));
        String x = "!";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_NOT_EQ() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_NOT_EQ, "!=", new Position(0,0));
        String x = "!=";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_OR() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_OR, "||", new Position(0,0));
        String x = "||";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_PLUS() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_PLUS, "+", new Position(0,0));
        String x = "+";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_POINT() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_POINT, "Point", new Position(0,0));
        String x = "Point";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_PRINT() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_PRINT, "print", new Position(0,0));
        String x = "print";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_REG_BRACKET_L() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_REG_BRACKET_L, "(", new Position(0,0));
        String x = "(";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_REG_BRACKET_R() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_REG_BRACKET_R, ")", new Position(0,0));
        String x = ")";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_RETURN() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_RETURN, "return", new Position(0,0));
        String x = "return";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_SEMICOLON() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_SEMICOLON, ";", new Position(0,0));
        String x = ";";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_STRING() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_STRING, "Hello", new Position(0,0));
        String x = "\"Hello\"";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_WHILE() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_WHILE, "while", new Position(0,0));
        String x = "while";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_STRING_withNewline() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_STRING, "Hello\nt", new Position(0,0));
        String x = "\"Hello\nt\"";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void test_T_STRING_withEscapeChar() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_STRING, "\"name\"", new Position(0,0));
        String x = "\"\\\"name\\\"\"";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }
    @Test
    public void test_T_STRING_withSlashChar() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_STRING, "\\name", new Position(0,0));
        String x = "\"\\name\"";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }

    @Test
    public void testException_unknownChar() throws IOException {
        Position pos = new Position(0,0);
        String x = "%";
        initLexer(x);
        Exception exception = assertThrows(InvalidTokenException.class, () -> myLexer.getToken());
        String expectedMessage = "Invalid token " + x + " at the position: " + pos;
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testException_tooLongComment() throws IOException {
        Position pos = new Position(0,0);
        int size = 210;
        char[] longArray = new char[size];
        for (int i=0; i<size; i++)
            longArray[i] = 'x';
        String x = "#" + Arrays.toString(longArray);
        initLexer(x);
        assertThrows(InvalidTokenException.class, () -> myLexer.getToken());
    }

    //TODO: tests for throwing exceptions
}