package tkom.InterpreterTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tkom.common.ExceptionHandler;
import tkom.components.Block;
import tkom.components.FunctionDef;
import tkom.components.Program;
import tkom.exception.*;
import tkom.interpreter.Interpreter;
import tkom.lexer.Lexer;
import tkom.parser.Parser;

import java.io.*;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

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
        HashMap<String, FunctionDef> functions = new HashMap<>();
        functions.put("main", null);
        myInterpreter = new Interpreter(functions);
    }

    private void initFullInterpreter(String input) throws Exception {
        InputStream initialStream = new ByteArrayInputStream(input.getBytes());
        Reader targetReader = new InputStreamReader(initialStream);
        BufferedReader br = new BufferedReader(targetReader);
        ExceptionHandler excHandler = new ExceptionHandler();
        Lexer myLexer = new Lexer(br, excHandler);
        myParser = new Parser(myLexer, excHandler);
        Program program = myParser.parse();
        myInterpreter = new Interpreter(program.functions);
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
    public void test_AdditionIntDouble() throws Exception {
        String x = """
            {
                x = 4 + 2.5;
                print(x);
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        block.accept(myInterpreter);
        assertEqualOutput("6.5", outContent.toString());
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
    public void test_ExceptionDifferentTypeAssignment() throws Exception {
        String x = """
            {
                z = 2;
                z = "String";
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        assertThrows(IncorrectTypeException.class, () -> block.accept(myInterpreter));
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
    public void test_NestedOperations() throws Exception {
        String x = """
            {
                z = 2*((2+3)*2-(3-1)*2);
                print(z);
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        block.accept(myInterpreter);
        assertEqualOutput("12", outContent.toString());
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
    public void test_ExceptionZeroDivision1() throws Exception {
        String x = """
            {
                x = 4/(2-2);
            }
                """;
        initInterpreter(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        assertThrows(ZeroDivisionException.class, () -> block.accept(myInterpreter));
    }

    @Test
    public void test_StringConcatenation() throws Exception {
        String x = """
            function main() {
                x = "Hello ";
                y = "world";
                z = x+y;
                print(z);
            }
                """;
        initFullInterpreter(x);
        myInterpreter.runMain();
        assertEqualOutput("Hello world", outContent.toString());
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

    @Test
    public void test_ExceptionMissingFuncDef() throws Exception {
        String x = """
            function main() {
                hello();
            }
                """;
        initFullInterpreter(x);
        assertThrows(MissingPartException.class, () -> myInterpreter.runMain());
    }

    @Test
    public void test_ExceptionMissingMainFuncDef() throws Exception {
        String x = """
            function dummy() {
                i = 0;
            }
                """;
        initFullInterpreter(x);
        assertThrows(MissingPartException.class, () -> myInterpreter.runMain());
    }

    @Test
    public void test_ExceptionDifferentParamsAndArgsCount() throws Exception {
        String x = """
            function test(x, y){
                print("Here");
            }
            function main() {
                x = 1;
                test(x);
            }
                """;
        initFullInterpreter(x);
        assertThrows(InvalidMethodException.class, () -> myInterpreter.runMain());
    }

    @Test
    public void test_AssignmentToFunctionName() throws Exception {
        String x = """
            function test(x, y){
                print("Here");
            }
            function main() {
                test = 2;
            }
                """;
        initFullInterpreter(x);
        assertThrows(IncorrectValueException.class, () -> myInterpreter.runMain());
    }

    @Test
    public void test_FunctionCallWithParams() throws Exception {
        String x = """
            function test(x, y){
                z = x+y;
                print(z);
            }
            function main() {
                test(2, 3);
            }
                """;
        initFullInterpreter(x);
        myInterpreter.runMain();
        assertEqualOutput("5", outContent.toString());
    }

    @Test
    public void test_FunctionCallWithParams2() throws Exception {
        String x = """
            function test(x, y, z){
                x = y*z;
                w = x+z;
                print(w);
            }
            function main() {
                x = 2;
                test(x, 4, 6);
            }
                """;
        initFullInterpreter(x);
        myInterpreter.runMain();
        assertEqualOutput("30", outContent.toString());
    }

    @Test
    public void test_NestedFunctionCall() throws Exception {
        String x = """
            function test1(x, y) {
                z = x-y;
                print(z);
            }
            
            function test(x, y, z){
                test1(y, x);
            }
            function main() {
                x = 2;
                y = 4;
                test(x, y, 6);
            }
                """;
        initFullInterpreter(x);
        myInterpreter.runMain();
        assertEqualOutput("2", outContent.toString());
    }

    @Test
    public void test_EmptyReturn() throws Exception {
        String x = """
            function test(){
                print("One");
                return;
                print("Two");
            }
            function main() {
                test();
            }
                """;
        initFullInterpreter(x);
        myInterpreter.runMain();
        assertEqualOutput("One", outContent.toString());
    }

    //TODO: rekurencja testy, nadpisywanie zmiennej, nieskonczona rekurencja

    @Test
    public void test_Return() throws Exception {
        String x = """
            function test(){
                return "success";
            }
            function main() {
                x = test();
                print(x);
            }
                """;
        initFullInterpreter(x);
        myInterpreter.runMain();
        assertEqualOutput("success", outContent.toString());
    }

    @Test
    public void test_ClassList() throws Exception {
        String x = """
            function main() {
                x = List();
                x.add(2);
                x.add(3);
                w = x.remove();
                print(w);
            }
                """;
        initFullInterpreter(x);
        myInterpreter.runMain();
        assertEqualOutput("3", outContent.toString());
    }

    @Test
    public void test_ListTypeException() throws Exception {
        String x = """
            function main() {
                x = List();
                x.add(2);
                x.add(2.2);
            }
                """;
        initFullInterpreter(x);
        assertThrows(IncorrectTypeException.class, () -> myInterpreter.runMain());
    }

    @Test
    public void test_ListLimitsException() throws Exception {
        String x = """
            function main() {
                x = List();
                y = x.remove();
            }
                """;
        initFullInterpreter(x);
        assertThrows(ExceededLimitsException.class, () -> myInterpreter.runMain());
    }

    @Test
    public void test_ClassPoint() throws Exception {
        String x = """
            function main() {
                point1 = Point(2, 3);
                point1_x = point1.x;
                print(point1_x);
            }
                """;
        initFullInterpreter(x);
        myInterpreter.runMain();
        assertEqualOutput("2", outContent.toString());
    }

    @Test
    public void test_ClassPointAttributes() throws Exception {
        String x = """
            function main() {
                point1 = Point(2, 3);
                point1_x = point1.x;
                point1_y = point1.y;
                w = point1_x + point1_y;
                print(w);
            }
                """;
        initFullInterpreter(x);
        myInterpreter.runMain();
        assertEqualOutput("5", outContent.toString());
    }

    @Test
    public void test_PointTypeException() throws Exception {
        String x = """
            function main() {
                x = Point(2, "hello");
            }
                """;
        initFullInterpreter(x);
        assertThrows(IncorrectTypeException.class, () -> myInterpreter.runMain());
    }

    @Test
    public void test_LineTypeException() throws Exception {
        String x = """
            function main() {
                p1 = Point(1, 2);
                w = 3;
                x = Line(p1, w);
            }
                """;
        initFullInterpreter(x);
        assertThrows(IncorrectTypeException.class, () -> myInterpreter.runMain());
    }

    @Test
    public void test_assigningParamVariable() throws Exception {
        String x = """
            function test1(x){
                x = 5;
            }
            
            function main() {
                x = 4;
                test1(x);
                print(x);
            }
                """;
        initFullInterpreter(x);
        myInterpreter.runMain();
        assertEqualOutput("4", outContent.toString());
    }

    @Test
    public void test_recursion() throws Exception {
        String x = """
            function fibonacci(x){
                if(x==0){
                    return 1;
                }
                if (x==1){
                    return 1;
                }
                else {
                    return (fibonacci(x-1)+fibonacci(x-2));
                }
            }
            
            function main() {
                print(fibonacci(7));
            }
                """;
        initFullInterpreter(x);
        myInterpreter.runMain();
        assertEqualOutput("21", outContent.toString());
    }

    @Test
    public void test_ExceededRecursion() throws Exception {
        String x = """
            function fibonacci(x){
                if(x==0){
                    return 1;
                }
                if (x==1){
                    return 1;
                }
                else {
                    return (fibonacci(x-1)+fibonacci(x-2));
                }
            }
            
            function main() {
                print(fibonacci(50));
            }
                """;
        initFullInterpreter(x);
        assertThrows(ExceededLimitsException.class, () -> myInterpreter.runMain());
    }
}


