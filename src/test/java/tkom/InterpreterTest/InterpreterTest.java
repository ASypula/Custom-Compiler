package tkom.InterpreterTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tkom.common.ExceptionHandler;
import tkom.components.Block;
import tkom.exception.InvalidMethodException;
import tkom.exception.UnknownVariableException;
import tkom.exception.ZeroDivisionException;
import tkom.interpreter.Interpreter;
import tkom.lexer.Lexer;
import tkom.parser.Parser;

import java.io.*;

import static org.junit.Assert.*;

public class InterpreterTest {
    private Interpreter myInterpreter;
    private Parser myParser;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    private void initInterpreter(String input) throws Exception {
        InputStream initialStream = new ByteArrayInputStream(input.getBytes());
        Reader targetReader = new InputStreamReader(initialStream);
        BufferedReader br = new BufferedReader(targetReader);
        ExceptionHandler excHandler = new ExceptionHandler();
        Lexer myLexer = new Lexer(br, excHandler);
        myParser = new Parser(myLexer, excHandler);
        myInterpreter = new Interpreter(null);
    }

    private void assertEqualOutput(String expected, String actual){
        String str = actual.substring(0, actual.length() - 2);
        assertEquals(expected, str);
    }

    @Test
    public void test_Print() throws Exception {
        String x = "{ print(\"Hello\"); }";
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        block.accept(myInterpreter);
        assertEqualOutput("Hello", outContent.toString());
    }

    @Test
    public void test_ArithmExprAdd() throws Exception {
        String x = """
            { 
                x = 2+2;
                print(x);
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        block.accept(myInterpreter);
        assertEqualOutput("4", outContent.toString());
    }

    @Test
    public void test_ArithmExprSubtract() throws Exception {
        String x = """
            { 
                x = 4-2;
                print(x);
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        block.accept(myInterpreter);
        assertEqualOutput("2", outContent.toString());
    }

    @Test
    public void test_MultExpr() throws Exception {
        String x = """
            { 
                x = 4*2;
                print(x);
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        block.accept(myInterpreter);
        assertEqualOutput("8", outContent.toString());
    }

    @Test
    public void test_OrderAdditionAndMultiplication() throws Exception {
        String x = """
            { 
                z = 2+3*2;
                print(z);
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        block.accept(myInterpreter);
        assertEqualOutput("8", outContent.toString());
    }

    @Test
    public void test_OrderAdditionAndMultiplicationBrackets() throws Exception {
        String x = """
            { 
                z = (2+3)*2;
                print(z);
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        block.accept(myInterpreter);
        assertEqualOutput("10", outContent.toString());
    }

    @Test
    public void test_ExceptionZeroDivision() throws Exception {
        String x = """
            { 
                x = 4/0;
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        assertThrows(ZeroDivisionException.class, () -> block.accept(myInterpreter));
    }

    @Test
    public void test_ExceptionStringAddition() throws Exception {
        String x = """
            { 
                x = 4 + "Hello";
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        assertThrows(InvalidMethodException.class, () -> block.accept(myInterpreter));
    }

    @Test
    public void test_IfWithAndTrue() throws Exception {
        String x = """
            { if (2+2==4 && true){
                print("Yes");
              }
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        block.accept(myInterpreter);
        assertEqualOutput("Yes", outContent.toString());
    }

    @Test
    public void test_IfWithAndFalse() throws Exception {
        String x = """
            { if (2+2==2 && true){
                print("Yes");
              }
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        block.accept(myInterpreter);
        assertEquals("", outContent.toString());
    }

    @Test
    public void test_IfWithElse() throws Exception {
        String x = """
            { if (false){
                print("Yes");
              }
              else {
                print("Else");
              }
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        block.accept(myInterpreter);
        assertEqualOutput("Else", outContent.toString());
    }

    @Test
    public void test_While() throws Exception {
        String x = """
            { 
                i = 0;
                while (i<3){
                    i = i+1;
                }
                print(i);
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        block.accept(myInterpreter);
        assertEqualOutput("3", outContent.toString());
    }

    @Test
    public void test_ExceptionBlockScope() throws Exception {
        String x = """
            { 
                i = 0;
                if (true) {
                    z = 0;
                }
                print(z);
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        assertThrows(UnknownVariableException.class, () -> block.accept(myInterpreter));
    }

}
