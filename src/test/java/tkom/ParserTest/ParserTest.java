package tkom.ParserTest;

import org.junit.Test;
import tkom.common.ExceptionHandler;
import tkom.common.ParserComponentTypes.ExpressionType;
import tkom.components.FunctionDef;
import tkom.components.Parameter;
import tkom.components.Program;
import tkom.components.expressions.*;
import tkom.components.statements.*;
import tkom.exception.ExceededLimitsException;
import tkom.exception.InvalidTokenException;
import tkom.exception.MissingPartException;
import tkom.lexer.Lexer;
import tkom.parser.Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserTest {

    private Parser myParser;
    private void initParser(String input) throws IOException {
        InputStream initialStream = new ByteArrayInputStream(input.getBytes());
        Reader targetReader = new InputStreamReader(initialStream);
        BufferedReader br = new BufferedReader(targetReader);
        ExceptionHandler excHandler = new ExceptionHandler();
        Lexer myLexer = new Lexer(br, excHandler);
        myParser = new Parser(myLexer, excHandler);
    }

    @Test
    public void test_empty() throws Exception {
        String x = "";
        initParser(x);
        Program program = myParser.parse();
        assertThat(program, instanceOf(Program.class));
        assertEquals(program.functions.size(), 0);
    }

    @Test
    public void test_multiplicationExpressionMultiplicationOp() throws Exception {
        String x = "2*5";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(MultExpression.class));
        assertEquals(((MultExpression)expr).isDivision(), false);
        assertThat(((MultExpression)expr).left, instanceOf(PrimExpression.class));
        PrimExpression leftExpr = (PrimExpression)((MultExpression)expr).left;
        PrimExpression rightExpr = (PrimExpression)((MultExpression)expr).right;
        assertEquals(leftExpr.type, ExpressionType.E_LITERAL);
        assertEquals(leftExpr.literal.getIntValue(), 2);
        assertEquals(rightExpr.literal.getIntValue(), 5);
    }

    @Test
    public void test_multiplicationExpressionDivisionOp() throws Exception {
        String x = "2/5";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(MultExpression.class));
        assertEquals(((MultExpression)expr).isDivision(), true);
        assertThat(((MultExpression)expr).left, instanceOf(PrimExpression.class));
        PrimExpression leftExpr = (PrimExpression)((MultExpression)expr).left;
        PrimExpression rightExpr = (PrimExpression)((MultExpression)expr).right;
        assertEquals(leftExpr.type, ExpressionType.E_LITERAL);
        assertEquals(leftExpr.literal.getIntValue(), 2);
        assertEquals(rightExpr.literal.getIntValue(), 5);
    }

    @Test
    public void test_ArithmeticExpressionAdditionOp() throws Exception {
        String x = "2+5";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(ArithmExpression.class));
        assertEquals(((ArithmExpression)expr).isSubtraction(), false);
        assertThat(((ArithmExpression)expr).left, instanceOf(PrimExpression.class));
        PrimExpression leftExpr = (PrimExpression)((ArithmExpression)expr).left;
        PrimExpression rightExpr = (PrimExpression)((ArithmExpression)expr).right;
        assertEquals(leftExpr.type, ExpressionType.E_LITERAL);
        assertEquals(leftExpr.literal.getIntValue(), 2);
        assertEquals(rightExpr.literal.getIntValue(), 5);
    }

    @Test
    public void test_ArithmeticExpressionSubtractionOp() throws Exception {
        String x = "2-5.4";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(ArithmExpression.class));
        assertEquals(((ArithmExpression)expr).isSubtraction(), true);
        assertThat(((ArithmExpression)expr).left, instanceOf(PrimExpression.class));
        PrimExpression leftExpr = (PrimExpression)((ArithmExpression)expr).left;
        PrimExpression rightExpr = (PrimExpression)((ArithmExpression)expr).right;
        assertEquals(leftExpr.type, ExpressionType.E_LITERAL);
        assertEquals(leftExpr.literal.getIntValue(), 2);
        assertEquals(rightExpr.literal.getDoubleValue(), 5.4, 10^-6);
    }

//    @Test
//    public void test_RelationalExpression() throws Exception {
//        String x = "x || y";
//        initParser(x);
//        myParser.nextToken();
//        IExpression expr = myParser.parseExpression();
//        assertThat(expr, instanceOf(RelExpression.class));
//    }

//    @Test
//    public void test_AndExpression() throws Exception {
//        String x = "(true && x)";
//        initParser(x);
//        myParser.nextToken();
//        IExpression expr = myParser.parseExpression();
//        assertThat(expr, instanceOf(AndExpression.class));
//    }

    @Test
    public void test_IfStatement() throws Exception {
        String x = "if(x > 4) { x=1; }";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(IfStatement.class));
    }

    @Test
    public void test_WhileStatement() throws Exception {
        String x = "while(x > 4) { x=1; }";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(WhileStatement.class));
    }

    @Test
    public void test_ReturnStatement1() throws Exception {
        String x = "return w;";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(ReturnStatement.class));
    }

    @Test
    public void test_ReturnStatement2() throws Exception {
        String x = "return 8+x;";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(ReturnStatement.class));
    }

    @Test
    public void test_AssignStatement1() throws Exception {
        String x = "w = 5;";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(AssignStatement.class));
    }

    @Test
    public void test_PrintStatement1() throws Exception {
        String x = "print(\"Hello\");";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(PrintStatement.class));
    }

    @Test
    public void test_PrintStatement2() throws Exception {
        String x = "print(x);";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(PrintStatement.class));
    }

    @Test
    public void test_Parameters() throws Exception {
        String x = "x, y, hello";
        initParser(x);
        myParser.nextToken();
        ArrayList<Parameter> params = myParser.parseParameters();
        assertEquals(params.size(), 3);
        assertThat(params.get(0), instanceOf(Parameter.class));
        assertEquals(params.get(0).name, "x");
        assertEquals(params.get(1).name, "y");
        assertEquals(params.get(2).name, "hello");
    }

    @Test
    public void test_ParametersException() throws Exception {
        String x = "x, 2, hello";
        initParser(x);
        myParser.nextToken();
        ArrayList<Parameter> params = myParser.parseParameters();
        assertEquals(params.size(), 3);

    }

    @Test
    public void test_FunctionDefinitionTrue() throws Exception {
        String x = "function hello() {print(\"Hello\");}";
        initParser(x);
        HashMap<String, FunctionDef> functions = new HashMap<>();
        myParser.nextToken();
        boolean success = myParser.parseFuncDef(functions);
        assertTrue(success);
    }

//    @Test
//    public void test_FunctionDefinitionException() throws Exception {
//        String x = "hello() {print(\"Hello\");}";
//        initParser(x);
//        HashMap<String, FunctionDef> functions = new HashMap<>();
//        myParser.nextToken();
//        boolean success = myParser.parseFuncDef(functions);
//        assertTrue(success);
//    }

}
