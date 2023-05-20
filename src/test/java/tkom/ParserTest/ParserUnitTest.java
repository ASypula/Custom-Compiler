package tkom.ParserTest;

import org.junit.Test;
import tkom.common.ExceptionHandler;
import tkom.common.ParserComponentTypes.ExpressionType;
import tkom.common.ParserComponentTypes.ValueType;
import tkom.common.Position;
import tkom.common.tokens.*;
import tkom.components.Block;
import tkom.components.FunctionDef;
import tkom.components.Value;
import tkom.components.expressions.*;
import tkom.components.statements.*;
import tkom.exception.*;
import tkom.parser.Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

public class ParserUnitTest {
    private Parser myParser;
    private MockLexer myLexer;
    private void initParser(ArrayList<Token> tList) {
        ExceptionHandler excHandler = new ExceptionHandler();
        myLexer = new MockLexer(tList);
        myParser = new Parser(myLexer, excHandler);
    }

    @Test
    public void test_IntLiteral() throws IOException, InvalidTokenException, ExceededLimitsException, InvalidMethodException {
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 0), 5));
        initParser(tList);
        myParser.nextToken();
        Value lit = myParser.parseValue();
        assertEquals(lit.getIntValue(), 5);
    }

    @Test
    public void test_IntLiteralException() throws IOException, InvalidTokenException, ExceededLimitsException, InvalidMethodException {
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new TokenString(TokenType.T_STRING, new Position(0, 0), "hello"));
        initParser(tList);
        myParser.nextToken();
        Value lit = myParser.parseValue();
        assertThrows(InvalidMethodException.class, () -> lit.getIntValue());
    }

    @Test
    public void test_StringLiteral() throws IOException, InvalidTokenException, ExceededLimitsException, InvalidMethodException {
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new TokenString(TokenType.T_STRING, new Position(0, 0), "hello"));
        initParser(tList);
        myParser.nextToken();
        Value lit = myParser.parseValue();
        assertEquals(lit.getStringValue(), "hello");
        assertEquals(lit.getType(), ValueType.V_STRING);
    }

    @Test
    public void test_multiplicationExpressionMultiplicationOp() throws Exception {
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 0), 2));
        tList.add(new Token(TokenType.T_MULT, new Position(0, 1)));
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 2), 5));
        initParser(tList);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(MultExpression.class));
        assertEquals(((MultExpression)expr).isDivision(), false);
        assertThat(((MultExpression)expr).left, instanceOf(PrimExpression.class));
        PrimExpression leftExpr = (PrimExpression)((MultExpression)expr).left;
        PrimExpression rightExpr = (PrimExpression)((MultExpression)expr).right;
        assertEquals(leftExpr.type, ExpressionType.E_LITERAL);
        assertEquals(leftExpr.value.getIntValue(), 2);
        assertEquals(rightExpr.value.getIntValue(), 5);
    }

    @Test
    public void test_ArithmeticExpressionAdditionOp() throws Exception {
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 0), 2));
        tList.add(new Token(TokenType.T_PLUS, new Position(0, 1)));
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 2), 5));
        initParser(tList);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(ArithmExpression.class));
        assertEquals(((ArithmExpression)expr).isSubtraction(), false);
        assertThat(((ArithmExpression)expr).left, instanceOf(PrimExpression.class));
        PrimExpression leftExpr = (PrimExpression)((ArithmExpression)expr).left;
        PrimExpression rightExpr = (PrimExpression)((ArithmExpression)expr).right;
        assertEquals(leftExpr.type, ExpressionType.E_LITERAL);
        assertEquals(leftExpr.value.getIntValue(), 2);
        assertEquals(rightExpr.value.getIntValue(), 5);
    }

    @Test
    public void test_ArithmeticExpressionSubtractionOpWithIdentInverse() throws Exception {
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new TokenDouble(TokenType.T_DOUBLE, new Position(0, 0), 5.4));
        tList.add(new Token(TokenType.T_MINUS, new Position(0, 1)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 2), "x"));
        initParser(tList);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(ArithmExpression.class));
        assertEquals(((ArithmExpression)expr).isSubtraction(), true);
        assertThat(((ArithmExpression)expr).left, instanceOf(PrimExpression.class));
        PrimExpression leftExpr = (PrimExpression)((ArithmExpression)expr).left;
        PrimExpression rightExpr = (PrimExpression)((ArithmExpression)expr).right;
        assertEquals(rightExpr.type, ExpressionType.E_LITERAL);
        assertEquals(rightExpr.value.getIdentifierValue(), "x");
        assertEquals(rightExpr.value.getType(), ValueType.V_IDENT);
        assertEquals(leftExpr.value.getDoubleValue(), 5.4, 10^-6);
    }

    @Test
    public void test_RelationalSmallerExpression() throws Exception {
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "x"));
        tList.add(new Token(TokenType.T_LESS, new Position(0, 1)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 2), "y"));
        initParser(tList);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(RelExpression.class));
    }

    @Test
    public void test_AndExpression() throws Exception {
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "x"));
        tList.add(new Token(TokenType.T_AND, new Position(0, 1)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 2), "y"));
        initParser(tList);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(AndExpression.class));
        PrimExpression leftExpr = (PrimExpression)((AndExpression)expr).left;
        PrimExpression rightExpr = (PrimExpression)((AndExpression)expr).right;
        assertEquals(rightExpr.type, ExpressionType.E_LITERAL);
        assertEquals(rightExpr.value.getIdentifierValue(), "y");
        assertEquals(rightExpr.value.getType(), ValueType.V_IDENT);
        assertEquals(leftExpr.value.getIdentifierValue(), "x");
    }

    @Test
    public void test_OrExpression() throws Exception {
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "x"));
        tList.add(new Token(TokenType.T_OR, new Position(0, 1)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 2), "y"));
        initParser(tList);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(Expression.class));
    }

    @Test
    public void test_IfStatementNoElse() throws Exception {
        // "if(x > 4) { x=1; }";
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new Token(TokenType.T_IF, new Position(0, 0)));
        tList.add(new Token(TokenType.T_REG_BRACKET_L, new Position(0, 1)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 1), "x"));
        tList.add(new Token(TokenType.T_LESS, new Position(0, 2)));
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 3), 4));
        tList.add(new Token(TokenType.T_REG_BRACKET_R, new Position(0, 5)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_L, new Position(0, 6)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 7), "x"));
        tList.add(new Token(TokenType.T_ASSIGN, new Position(0, 8)));
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 3), 1));
        tList.add(new Token(TokenType.T_SEMICOLON, new Position(0, 1)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_R, new Position(0, 6)));
        initParser(tList);
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
        // "if(x > 4) { x=1; } else {x=2;}";
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new Token(TokenType.T_IF, new Position(0, 0)));
        tList.add(new Token(TokenType.T_REG_BRACKET_L, new Position(0, 1)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 1), "x"));
        tList.add(new Token(TokenType.T_LESS, new Position(0, 2)));
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 3), 4));
        tList.add(new Token(TokenType.T_REG_BRACKET_R, new Position(0, 5)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_L, new Position(0, 6)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 7), "x"));
        tList.add(new Token(TokenType.T_ASSIGN, new Position(0, 8)));
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 3), 1));
        tList.add(new Token(TokenType.T_SEMICOLON, new Position(0, 1)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_R, new Position(0, 6)));
        tList.add(new Token(TokenType.T_ELSE, new Position(0, 1)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_L, new Position(0, 6)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 7), "x"));
        tList.add(new Token(TokenType.T_ASSIGN, new Position(0, 8)));
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 3), 2));
        tList.add(new Token(TokenType.T_SEMICOLON, new Position(0, 1)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_R, new Position(0, 6)));
        initParser(tList);
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
        // "while(x > 4) { x=1; }";
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new Token(TokenType.T_WHILE, new Position(0, 0)));
        tList.add(new Token(TokenType.T_REG_BRACKET_L, new Position(0, 1)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 1), "x"));
        tList.add(new Token(TokenType.T_LESS, new Position(0, 2)));
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 3), 4));
        tList.add(new Token(TokenType.T_REG_BRACKET_R, new Position(0, 5)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_L, new Position(0, 6)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 7), "x"));
        tList.add(new Token(TokenType.T_ASSIGN, new Position(0, 8)));
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 3), 1));
        tList.add(new Token(TokenType.T_SEMICOLON, new Position(0, 1)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_R, new Position(0, 6)));
        initParser(tList);
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
        // "return w;";
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new Token(TokenType.T_RETURN, new Position(0, 0)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 1), "w"));
        tList.add(new Token(TokenType.T_SEMICOLON, new Position(0, 2)));
        initParser(tList);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(ReturnStatement.class));
    }

    @Test
    public void test_AssignStatementInt() throws Exception {
        // "w = 5;";
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "w"));
        tList.add(new Token(TokenType.T_ASSIGN, new Position(0, 0)));
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 0), 5));
        tList.add(new Token(TokenType.T_SEMICOLON, new Position(0, 2)));
        initParser(tList);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(AssignStatement.class));
        String identifier = ((AssignStatement)stmt).getIdentifier();
        IExpression expr = ((AssignStatement)stmt).getExpression();
        assertEquals(identifier, "w");
        assertThat(expr, instanceOf(PrimExpression.class));
        assertEquals(((PrimExpression)expr).type, ExpressionType.E_LITERAL);
        Value lit = ((PrimExpression)expr).value;
        assertEquals(lit.getIntValue(), 5);
    }

    @Test
    public void test_PrintStatementIdentifier() throws Exception {
        // "print(x);";
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new Token(TokenType.T_PRINT, new Position(0, 0)));
        tList.add(new Token(TokenType.T_REG_BRACKET_L, new Position(0, 0)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "x"));
        tList.add(new Token(TokenType.T_REG_BRACKET_R, new Position(0, 0)));
        tList.add(new Token(TokenType.T_SEMICOLON, new Position(0, 2)));
        initParser(tList);
        myParser.nextToken();
        IStatement stmt = myParser.parseStatement();
        assertThat(stmt, instanceOf(PrintStatement.class));
    }

    @Test
    public void test_ParametersExceptionDuplicatedIdentifier() throws Exception {
        // "x, w, x";
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "x"));
        tList.add(new Token(TokenType.T_COLON, new Position(0, 0)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "w"));
        tList.add(new Token(TokenType.T_COLON, new Position(0, 2)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "x"));
        initParser(tList);
        myParser.nextToken();
        assertThrows(DuplicatedElementException.class, () -> myParser.parseParameters());
    }

    @Test
    public void test_FunctionDefinitionTrue() throws Exception {
        // "function hello() {print(x);}";
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new Token(TokenType.T_FUNCTION, new Position(0, 0)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "hello"));
        tList.add(new Token(TokenType.T_REG_BRACKET_L, new Position(0, 0)));
        tList.add(new Token(TokenType.T_REG_BRACKET_R, new Position(0, 0)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_L, new Position(0, 0)));
        tList.add(new Token(TokenType.T_PRINT, new Position(0, 0)));
        tList.add(new Token(TokenType.T_REG_BRACKET_L, new Position(0, 0)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "x"));
        tList.add(new Token(TokenType.T_REG_BRACKET_R, new Position(0, 0)));
        tList.add(new Token(TokenType.T_SEMICOLON, new Position(0, 2)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_R, new Position(0, 0)));
        initParser(tList);
        HashMap<String, FunctionDef> functions = new HashMap<>();
        myParser.nextToken();
        boolean success = myParser.parseFuncDef(functions);
        assertTrue(success);
    }

    @Test
    public void test_FunctionDefinitionExceptionNoName() throws Exception {
        // "hello() {print(x);}";
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "hello"));
        tList.add(new Token(TokenType.T_REG_BRACKET_L, new Position(0, 0)));
        tList.add(new Token(TokenType.T_REG_BRACKET_R, new Position(0, 0)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_L, new Position(0, 0)));
        tList.add(new Token(TokenType.T_PRINT, new Position(0, 0)));
        tList.add(new Token(TokenType.T_REG_BRACKET_L, new Position(0, 0)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "x"));
        tList.add(new Token(TokenType.T_REG_BRACKET_R, new Position(0, 0)));
        tList.add(new Token(TokenType.T_SEMICOLON, new Position(0, 2)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_R, new Position(0, 0)));
        initParser(tList);
        HashMap<String, FunctionDef> functions = new HashMap<>();
        myParser.nextToken();
        assertThrows(InvalidTokenException.class, () -> myParser.parseFuncDef(functions));
    }

    @Test
    public void test_FunctionDefinitionExceptionNoRBracket() throws Exception {
        // "function hello() {print(x);}";
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new Token(TokenType.T_FUNCTION, new Position(0, 0)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "hello"));
        tList.add(new Token(TokenType.T_REG_BRACKET_L, new Position(0, 0)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_L, new Position(0, 0)));
        tList.add(new Token(TokenType.T_PRINT, new Position(0, 0)));
        tList.add(new Token(TokenType.T_REG_BRACKET_L, new Position(0, 0)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "x"));
        tList.add(new Token(TokenType.T_REG_BRACKET_R, new Position(0, 0)));
        tList.add(new Token(TokenType.T_SEMICOLON, new Position(0, 2)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_R, new Position(0, 0)));
        initParser(tList);
        HashMap<String, FunctionDef> functions = new HashMap<>();
        myParser.nextToken();
        assertThrows(MissingPartException.class, () -> myParser.parseFuncDef(functions));
    }

    @Test
    public void test_Block() throws Exception {
        // {x = 2+2; print("Hi");}
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new Token(TokenType.T_CURLY_BRACKET_L, new Position(0, 0)));
        tList.add(new TokenString(TokenType.T_IDENT, new Position(0, 0), "x"));
        tList.add(new Token(TokenType.T_ASSIGN, new Position(0, 0)));
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 0), 2));
        tList.add(new Token(TokenType.T_PLUS, new Position(0, 0)));
        tList.add(new TokenInt(TokenType.T_INT, new Position(0, 0), 2));
        tList.add(new Token(TokenType.T_SEMICOLON, new Position(0, 0)));
        tList.add(new Token(TokenType.T_PRINT, new Position(0, 0)));
        tList.add(new Token(TokenType.T_REG_BRACKET_L, new Position(0, 0)));
        tList.add(new TokenString(TokenType.T_STRING, new Position(0, 0), "Hi"));
        tList.add(new Token(TokenType.T_REG_BRACKET_R, new Position(0, 0)));
        tList.add(new Token(TokenType.T_SEMICOLON, new Position(0, 0)));
        tList.add(new Token(TokenType.T_CURLY_BRACKET_R, new Position(0, 0)));
        initParser(tList);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        IStatement stmt0 = block.getStmt(0);
        IStatement stmt1 = block.getStmt(1);
        assertThat(stmt0, instanceOf(AssignStatement.class));
        assertThat(stmt1, instanceOf(PrintStatement.class));
    }

}
