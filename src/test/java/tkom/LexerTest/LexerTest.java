package tkom.LexerTest;

import org.junit.Test;
import tkom.common.Position;
import tkom.common.Token;
import tkom.common.TokenType;
import tkom.exception.InvalidTokenException;
import tkom.lexer.Lexer;

import java.io.*;

import static org.junit.Assert.assertEquals;

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
    public void test_T_INT() throws IOException, InvalidTokenException {
        Token tokenExp=new Token(TokenType.T_INT, "123", new Position(0,0));
        String x = "123 ";
        initLexer(x);
        Token t = myLexer.getToken();
        assertToken(tokenExp, t);
    }
}