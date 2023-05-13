package tkom.ParserTest;

import org.junit.Test;
import tkom.common.ExceptionHandler;
import tkom.common.ParserComponentTypes.ExpressionType;
import tkom.common.ParserComponentTypes.LiteralType;
import tkom.common.tokens.TokenType;
import tkom.components.*;
import tkom.components.expressions.*;
import tkom.components.statements.*;
import tkom.exception.ExceededLimitsException;
import tkom.exception.InvalidMethodException;
import tkom.exception.InvalidTokenException;
import tkom.exception.MissingPartException;
import tkom.lexer.Lexer;
import tkom.parser.Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

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
    public void test_IntLiteral() throws IOException, InvalidTokenException, ExceededLimitsException, InvalidMethodException {
        String x = "5";
        initParser(x);
        myParser.nextToken();
        Literal lit = myParser.parseLiteral();
        assertEquals(lit.getIntValue(), 5);
    }

    @Test
    public void test_IntLiteralException() throws IOException, InvalidTokenException, ExceededLimitsException, InvalidMethodException {
        String x = "hello";
        initParser(x);
        myParser.nextToken();
        Literal lit = myParser.parseLiteral();
        assertThrows(InvalidMethodException.class, () -> lit.getIntValue());
    }

    @Test
    public void test_DoubleLiteral() throws IOException, InvalidTokenException, ExceededLimitsException, InvalidMethodException {
        String x = "5.1";
        initParser(x);
        myParser.nextToken();
        Literal lit = myParser.parseLiteral();
        assertEquals(lit.getDoubleValue(), 5.1, 10^-6);
    }

    @Test
    public void test_StringLiteral() throws IOException, InvalidTokenException, ExceededLimitsException, InvalidMethodException {
        String x = "\"hello\"";
        initParser(x);
        myParser.nextToken();
        Literal lit = myParser.parseLiteral();
        assertEquals(lit.getStringValue(), "hello");
        assertEquals(lit.getType(), LiteralType.L_STRING);
    }

    @Test
    public void test_IdentLiteral() throws IOException, InvalidTokenException, ExceededLimitsException, InvalidMethodException {
        String x = "hello";
        initParser(x);
        myParser.nextToken();
        Literal lit = myParser.parseLiteral();
        assertEquals(lit.getIdentifierValue(), x);
        assertEquals(lit.getType(), LiteralType.L_IDENT);
    }

    @Test
    public void test_ClassLiteral() throws IOException, InvalidTokenException, ExceededLimitsException, InvalidMethodException {
        String x = "Point";
        initParser(x);
        myParser.nextToken();
        Literal lit = myParser.parseLiteral();
        assertEquals(lit.getType(), LiteralType.L_CLASS);
        assertEquals(lit.getTokenType(), TokenType.T_POINT);
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
    public void test_ArithmeticExpressionAdditionOpMore() throws Exception {
        String x = "2+5+2";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(ArithmExpression.class));
        assertEquals(((ArithmExpression)expr).isSubtraction(), false);
        assertThat(((ArithmExpression)expr).left, instanceOf(ArithmExpression.class));
        assertThat(((ArithmExpression)expr).right, instanceOf(PrimExpression.class));
        ArithmExpression leftExpr = (ArithmExpression)((ArithmExpression)expr).left;
        PrimExpression rightExpr = (PrimExpression)((ArithmExpression)expr).right;
        assertThat(leftExpr.right, instanceOf(PrimExpression.class));
        assertEquals(rightExpr.type, ExpressionType.E_LITERAL);
        assertEquals(rightExpr.literal.getIntValue(), 2);
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

    @Test
    public void test_ArithmeticExpressionSubtractionOpWithIdent() throws Exception {
        String x = "x-5.4";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(ArithmExpression.class));
        assertEquals(((ArithmExpression)expr).isSubtraction(), true);
        assertThat(((ArithmExpression)expr).left, instanceOf(PrimExpression.class));
        PrimExpression leftExpr = (PrimExpression)((ArithmExpression)expr).left;
        PrimExpression rightExpr = (PrimExpression)((ArithmExpression)expr).right;
        assertEquals(leftExpr.type, ExpressionType.E_LITERAL);
        assertEquals(leftExpr.literal.getIdentifierValue(), "x");
        assertEquals(leftExpr.literal.getType(), LiteralType.L_IDENT);
        assertEquals(rightExpr.literal.getDoubleValue(), 5.4, 10^-6);
    }

    @Test
    public void test_ArithmeticExpressionSubtractionOpWithIdentInverse() throws Exception {
        String x = "5.4-x";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(ArithmExpression.class));
        assertEquals(((ArithmExpression)expr).isSubtraction(), true);
        assertThat(((ArithmExpression)expr).left, instanceOf(PrimExpression.class));
        PrimExpression leftExpr = (PrimExpression)((ArithmExpression)expr).left;
        PrimExpression rightExpr = (PrimExpression)((ArithmExpression)expr).right;
        assertEquals(rightExpr.type, ExpressionType.E_LITERAL);
        assertEquals(rightExpr.literal.getIdentifierValue(), "x");
        assertEquals(rightExpr.literal.getType(), LiteralType.L_IDENT);
        assertEquals(leftExpr.literal.getDoubleValue(), 5.4, 10^-6);
    }

    @Test
    public void test_RelationalSmallerExpression() throws Exception {
        String x = "x < y";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(RelExpression.class));
    }

    @Test
    public void test_RelationalEqualExpression() throws Exception {
        String x = "x == y";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(RelExpression.class));
    }

    @Test
    public void test_AndExpression() throws Exception {
        String x = "x && y";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(AndExpression.class));
        PrimExpression leftExpr = (PrimExpression)((AndExpression)expr).left;
        PrimExpression rightExpr = (PrimExpression)((AndExpression)expr).right;
        assertEquals(rightExpr.type, ExpressionType.E_LITERAL);
        assertEquals(rightExpr.literal.getIdentifierValue(), "y");
        assertEquals(rightExpr.literal.getType(), LiteralType.L_IDENT);
        assertEquals(leftExpr.literal.getIdentifierValue(), "x");
    }

    @Test
    public void test_AndExpressionWithInt() throws Exception {
        String x = "2 && y";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(AndExpression.class));
    }

    @Test
    public void test_OrExpression() throws Exception {
        String x = "x || y";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(Expression.class));
    }
    @Test
    public void test_OrExpressionWithBool() throws Exception {
        String x = "x || False";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(Expression.class));
    }

    @Test
    public void test_ClassCall() throws Exception {
        String x = "Point(x)";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(FunctionCall.class));
        assertEquals(((FunctionCall)expr).getName(), "Point");
    }


    @Test
    public void test_IfStatementNoElse() throws Exception {
        String x = "if(x > 4) { x=1; }";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(IfStatement.class));
        IExpression cond = ((IfStatement)stmt).getCondition();
        assertThat(cond, instanceOf(RelExpression.class));
        Block bTrue = ((IfStatement)stmt).getBlockTrue();
        Block bElse = ((IfStatement)stmt).getBlockElse();
        assertThat(bTrue, instanceOf(Block.class));
        assertNull(bElse);
    }

    @Test
    public void test_IfStatementWithElse() throws Exception {
        String x = "if(x > 4) { x=1; } else {x=2;}";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(IfStatement.class));
        IExpression cond = ((IfStatement)stmt).getCondition();
        assertThat(cond, instanceOf(RelExpression.class));
        Block bTrue = ((IfStatement)stmt).getBlockTrue();
        Block bElse = ((IfStatement)stmt).getBlockElse();
        assertThat(bTrue, instanceOf(Block.class));
        assertThat(bElse, instanceOf(Block.class));
    }

    @Test
    public void test_WhileStatement() throws Exception {
        String x = "while(x > 4) { x=1; }";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(WhileStatement.class));
        IExpression cond = ((WhileStatement)stmt).getCondition();
        assertThat(cond, instanceOf(RelExpression.class));
        Block block = ((WhileStatement)stmt).getBlock();
        assertThat(block, instanceOf(Block.class));
    }

    @Test
    public void test_ReturnStatementIdentifier() throws Exception {
        String x = "return w;";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(ReturnStatement.class));
    }

    @Test
    public void test_ReturnStatementAddition() throws Exception {
        String x = "return x+8;";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(ReturnStatement.class));
    }

    @Test
    public void test_AssignStatementInt() throws Exception {
        String x = "w = 5;";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(AssignStatement.class));
        String identifier = ((AssignStatement)stmt).getIdentifier();
        IExpression expr = ((AssignStatement)stmt).getExpression();
        assertEquals(identifier, "w");
        assertThat(expr, instanceOf(PrimExpression.class));
        assertEquals(((PrimExpression)expr).type, ExpressionType.E_LITERAL);
        Literal lit = ((PrimExpression)expr).literal;
        assertEquals(lit.getIntValue(), 5);
    }

    @Test
    public void test_PrintStatementString() throws Exception {
        String x = "print(\"Hello\");";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(PrintStatement.class));
    }

    @Test
    public void test_PrintStatementIdentifier() throws Exception {
        String x = "print(x);";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(PrintStatement.class));
    }
    @Test
    public void test_StmtFunctionCall() throws Exception {
        String x = "hello(x, 3);";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(FunctionCall.class));
    }
    @Test
    public void test_StmtFunctionCallNoParams() throws Exception {
        String x = "hello();";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(FunctionCall.class));
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
    public void test_ParametersExceptionDuplicatedIdentifier() throws Exception {
        String x = "x, w, x";
        initParser(x);
        myParser.nextToken();
        assertThrows(MissingPartException.class, () -> myParser.parseParameters());
    }

    @Test
    public void test_ParametersExceptionNotIdentifier() throws Exception {
        String x = "x, 2, x";
        initParser(x);
        myParser.nextToken();
        assertThrows(InvalidMethodException.class, () -> myParser.parseParameters());
    }

    @Test
    public void test_ObjectAccess() throws Exception {
        String x = "x.length";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseIdentStartStmt();
        assertThat(stmt, instanceOf(ObjectAccess.class));
    }

    @Test
    public void test_ObjectAccessMore() throws Exception {
        String x = "x.y.length";
        initParser(x);
        myParser.nextToken();
        IStatement stmt = myParser.parseIdentStartStmt();
        assertThat(stmt, instanceOf(ObjectAccess.class));
        String identifier = ((ObjectAccess)stmt).getName();
        IExpression expr = ((ObjectAccess)stmt).getExpression();
        assertEquals(identifier, "x");
        assertThat(expr, instanceOf(ObjectAccess.class));
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

    @Test
    public void test_FunctionDefinitionExceptionNoName() throws Exception {
        String x = "() {print(\"Hello\");}";
        initParser(x);
        HashMap<String, FunctionDef> functions = new HashMap<>();
        myParser.nextToken();
        assertThrows(InvalidTokenException.class, () -> myParser.parseFuncDef(functions));
    }

    @Test
    public void test_FunctionDefinitionExceptionNoLBracket() throws Exception {
        String x = "function hej) {print(\"Hello\");}";
        initParser(x);
        HashMap<String, FunctionDef> functions = new HashMap<>();
        myParser.nextToken();
        assertThrows(MissingPartException.class, () -> myParser.parseFuncDef(functions));
    }
    @Test
    public void test_FunctionDefinitionExceptionNoRBracket() throws Exception {
        String x = "function name( {print(\"Hello\");}";
        initParser(x);
        HashMap<String, FunctionDef> functions = new HashMap<>();
        myParser.nextToken();
        assertThrows(MissingPartException.class, () -> myParser.parseFuncDef(functions));
    }

    @Test
    public void test_Block() throws Exception {
        String x = "{x = 2+2; print(\"Hi\");}";
        initParser(x);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        IStatement stmt0 = block.getStmt(0);
        IStatement stmt1 = block.getStmt(1);
        assertThat(stmt0, instanceOf(AssignStatement.class));
        assertThat(stmt1, instanceOf(PrintStatement.class));
    }

}
