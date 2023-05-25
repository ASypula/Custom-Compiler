package tkom;

import tkom.common.ExceptionHandler;
import tkom.components.Program;
import tkom.interpreter.Interpreter;
import tkom.lexer.Lexer;
import tkom.parser.Parser;


import java.io.*;

public class Main
{
    public static void main( String[] args ) throws Exception {
        if (args.length != 1)
            throw new Exception("Missing required parameter - path to the file");
        ExceptionHandler excHandler = new ExceptionHandler();
        FileReader fr=new FileReader(args[0]);
        BufferedReader br=new BufferedReader(fr);
        Program program = parseProgram(br, excHandler);
        runProgram(program);
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
