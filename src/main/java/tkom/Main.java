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
        Visitor visitor = new Interpreter(null);
        myParser.nextToken();
        Block block = myParser.parseBlock();
        block.accept(visitor);
        System.out.println(" ");
    }

    public static Program parseProgram(BufferedReader br, ExceptionHandler excHandler) throws Exception {
        Lexer myLexer = new Lexer(br, excHandler);
        Parser myParser = new Parser(myLexer, excHandler);
        Program program = myParser.parse();
        return program;
    }

    public static void runProgram(Program program) throws Exception {
        Interpreter interpreter = new Interpreter(program.functions);
        interpreter.runMain();
    }

        // For displaying
//        JFrame fr = new JFrame();
//        fr.setBounds(10, 10, 500, 500);
//        fr.setDefaultCloseOperation(3);
//        JPanel pn = new JPanel(){
//            @Override
//            public void paint (Graphics g0) {
//                Graphics2D g = (Graphics2D)g0.create();
//                int[] xValues = {80, 20, 50};
//                int[] yValues = {10, 10, 5};
//                Polygon polygon0 = new Polygon( xValues, yValues, 3 );
//                g.setColor(Color.yellow);
//                g.drawPolygon( polygon0 );
//                int[] xValues1 = {400, 200, 300};
//                int[] yValues1 = {80, 10, 50};
//                Polygon polygon1 = new Polygon( xValues1, yValues1, 3 );
//                g.setColor(new Color(123));
//                g.drawPolygon( polygon1 );
//            }
//        };
//        JFrame fr1 = new JFrame();
//        fr1.setBounds(10, 10, 500, 500);
//        fr1.setDefaultCloseOperation(3);
//        JPanel pn1 = new JPanel(){
//            @Override
//            public void paint (Graphics g0) {
//                Graphics2D g = (Graphics2D)g0.create();
//                int[] xValues = {180, 20, 50};
//                int[] yValues = {110, 10, 5};
//                Polygon polygon0 = new Polygon( xValues, yValues, 3 );
//                g.setColor(Color.red);
//                g.drawPolygon( polygon0 );
//            }
//        };
//        fr.add(pn);
////        fr1.add(pn1);
//        fr.setVisible(true);
//        //fr1.setVisible(true);
//        System.out.println( "Hello World!" );
//        fr.add(pn1);
//        fr.setVisible(true);
}
