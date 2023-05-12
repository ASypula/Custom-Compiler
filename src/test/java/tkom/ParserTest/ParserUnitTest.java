package tkom.ParserTest;

import org.junit.Test;
import tkom.common.ExceptionHandler;
import tkom.common.ParserComponentTypes.ExpressionType;
import tkom.common.ParserComponentTypes.LiteralType;
import tkom.common.Position;
import tkom.common.tokens.Token;
import tkom.common.tokens.TokenInt;
import tkom.common.tokens.TokenString;
import tkom.common.tokens.TokenType;
import tkom.components.Literal;
import tkom.components.expressions.ArithmExpression;
import tkom.components.expressions.IExpression;
import tkom.components.expressions.PrimExpression;
import tkom.exception.ExceededLimitsException;
import tkom.exception.InvalidMethodException;
import tkom.exception.InvalidTokenException;
import tkom.exception.MissingPartException;
import tkom.lexer.ILexer;
import tkom.lexer.Lexer;
import tkom.parser.Parser;

import java.io.*;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

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
        Literal lit = myParser.parseLiteral();
        assertEquals(lit.getIntValue(), 5);
    }

    @Test
    public void test_IntLiteralException() throws IOException, InvalidTokenException, ExceededLimitsException, InvalidMethodException {
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new TokenString(TokenType.T_STRING, new Position(0, 0), "hello"));
        initParser(tList);
        myParser.nextToken();
        Literal lit = myParser.parseLiteral();
        assertThrows(InvalidMethodException.class, () -> lit.getIntValue());
    }

    @Test
    public void test_StringLiteral() throws IOException, InvalidTokenException, ExceededLimitsException, InvalidMethodException {
        ArrayList<Token> tList = new ArrayList<>();
        tList.add(new TokenString(TokenType.T_STRING, new Position(0, 0), "hello"));
        initParser(tList);
        myParser.nextToken();
        Literal lit = myParser.parseLiteral();
        assertEquals(lit.getStringValue(), "hello");
        assertEquals(lit.getType(), LiteralType.L_STRING);
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
        assertEquals(leftExpr.literal.getIntValue(), 2);
        assertEquals(rightExpr.literal.getIntValue(), 5);
    }

}
