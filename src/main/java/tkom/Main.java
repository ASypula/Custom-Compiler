package tkom;

import tkom.lexer.Lexer;

import java.awt.*;
import java.io.*;
import javax.swing.JPanel;
import javax.swing.JFrame;


public class Main
{
    public static void main( String[] args ) throws IOException {
        File file = new File("src/main/java/tkom");
        for(String fileNames : file.list()) System.out.println(fileNames);
        FileReader fr=new FileReader("src/main/java/tkom/test.txt");
        BufferedReader br=new BufferedReader(fr);
        Lexer myLexer = new Lexer(br);
        char c = myLexer.nextChar();
        while (c != 'o'){
            c = myLexer.nextChar();
            System.out.println(c);
        };
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
}
