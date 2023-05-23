package tkom.ParserTest;

import org.junit.Test;
import tkom.common.ExceptionHandler;
import tkom.components.*;
import tkom.components.expressions.ArithmExpression;
import tkom.components.expressions.IExpression;
import tkom.components.expressions.MultExpression;
import tkom.components.expressions.PrimExpression;
import tkom.components.statements.AssignStatement;
import tkom.components.statements.IStatement;
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
        assertEquals(leftExpr.value.getIntValue(), 2);
        assertEquals(leftMultExpr.value.getIdentifierValue(), "y");
        assertEquals(rightMultExpr.value.getDoubleValue(), 5.1, 10^-6);
    }

    @Test
    public void test_ProgramWithPrint() throws Exception{
        String x = "function printHello() {x = \"Hello\"; print(x); }";
        initParser(x);
        Program program = myParser.parse();
        FunctionDef funcDef = program.getFunction("printHello");
        assertEquals(funcDef.getName(), "printHello");
        assertEquals(funcDef.getParams().size(), 0);
        Block block = funcDef.getBlock();
        assertEquals(block.getStmts().size(), 2);
        IStatement stmt0 = block.getStmt(0);
        IStatement stmt1 = block.getStmt(1);
        assertThat(stmt0, instanceOf(AssignStatement.class));
        assertThat(stmt1, instanceOf(FunctionCall.class));
    }
}
