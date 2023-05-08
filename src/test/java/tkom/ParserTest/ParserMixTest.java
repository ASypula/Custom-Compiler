package tkom.ParserTest;

import org.junit.Test;
import tkom.common.ExceptionHandler;
import tkom.common.ParserComponentTypes.ExpressionType;
import tkom.components.Literal;
import tkom.components.expressions.ArithmExpression;
import tkom.components.expressions.IExpression;
import tkom.components.expressions.MultExpression;
import tkom.components.expressions.PrimExpression;
import tkom.exception.ExceededLimitsException;
import tkom.exception.InvalidMethodException;
import tkom.exception.InvalidTokenException;
import tkom.exception.MissingPartException;
import tkom.lexer.Lexer;
import tkom.parser.Parser;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;

public class ParserMixTest {
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
    public void test_ArithmMixMultiExpression() throws IOException, InvalidTokenException, ExceededLimitsException, MissingPartException, InvalidMethodException {
        String x = "2+y*5.1";
        initParser(x);
        myParser.nextToken();
        IExpression expr = myParser.parseExpression();
        assertThat(expr, instanceOf(ArithmExpression.class));
        assertEquals(((ArithmExpression)expr).isSubtraction(), false);
        assertThat(((ArithmExpression)expr).left, instanceOf(PrimExpression.class));
        assertThat(((ArithmExpression)expr).right, instanceOf(MultExpression.class));
        PrimExpression leftExpr = (PrimExpression)((ArithmExpression)expr).left;
        MultExpression rightExpr = (MultExpression)((ArithmExpression)expr).right;
        assertEquals(rightExpr.isDivision(), false);
        PrimExpression leftMultExpr = (PrimExpression)rightExpr.left;
        PrimExpression  rightMultExpr = (PrimExpression)rightExpr.right;
        assertEquals(leftExpr.literal.getIntValue(), 2);
        assertEquals(leftMultExpr.literal.getIdentifierValue(), "y");
        assertEquals(rightMultExpr.literal.getDoubleValue(), 5.1, 10^-6);
    }
}
