package tkom;

import tkom.common.ExceptionHandler;
import tkom.components.Block;
import tkom.components.Program;
import tkom.components.expressions.Expression;
import tkom.components.expressions.IExpression;
import tkom.exception.MissingPartException;
import tkom.interpreter.Interpreter;
import tkom.lexer.Lexer;
import tkom.parser.Parser;
import tkom.visitor.Visitor;
import tkom.visitor.VisitorPrint;

import java.io.*;

public class Main
{
    public static void main( String[] args ) throws Exception {
        test2();
    }

    public static void test2() throws Exception{
        String filename = "src/main/java/tkom/test.txt";
        ExceptionHandler excHandler = new ExceptionHandler();
        FileReader fr=new FileReader(filename);
        BufferedReader br=new BufferedReader(fr);
        Program program = parseProgram(br, excHandler);
        runProgram(program);

    }

    public static void test() throws Exception{
        String filename = "src/main/java/tkom/test.txt";
        ExceptionHandler excHandler = new ExceptionHandler();
        FileReader fr=new FileReader(filename);
        BufferedReader br=new BufferedReader(fr);
        Lexer myLexer = new Lexer(br, excHandler);
        Parser myParser = new Parser(myLexer, excHandler);
        // Visitor visitor = new Interpreter(null);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        // block.accept(visitor);
        System.out.println(" ");
    }

    public static Program parseProgram(BufferedReader br, ExceptionHandler excHandler) throws Exception {
        Lexer myLexer = new Lexer(br, excHandler);
        Parser myParser = new Parser(myLexer, excHandler);
        return myParser.parse();
    }

    public static void runProgram(Program program) throws Exception {
        Interpreter interpreter = new Interpreter(program.functions);
        interpreter.runMain();
        System.out.println(" ");
    }

}
