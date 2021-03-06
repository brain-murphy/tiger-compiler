package parser.syntactic;

import org.junit.Test;
import parser.ParseCoordinator;
import parser.semantic.ParseStream;
import parser.syntactic.Parser;
import scanner.DirectScanner;
import scanner.Scanner;
import scanner.TestDirectScanner;
import util.Reader;

public class TestParser {
    public static final String ERRONEOUS_SAMPLE_PROGRAM_TEXT = "let\n" +
            " type ArrayInt = array [100] of int; \n" +
            " var X, Y : ArrayInt := 010; \n" +
            " var i, sum : int := 0; /* comment! */\n" +
            "in\n" +
            " for i := 1 to 100 do \n" +
            " sum = sum + X[i] * Y[i];\n" +
            " enddo;\n" +
            " printi(sum); \n" +
            "end";


    @Test
    public void testSemanticParse() {
        Reader reader = new Reader();
        String programText = reader.readFromFile("./examples/myTest.tiger");

        Scanner scanner = new DirectScanner(programText);

        ParseCoordinator parseCoordinator = new ParseCoordinator(scanner);

        parseCoordinator.runParse();
    }

//    @Test
//    public void testParsingSampleProgram() {
//        Scanner scanner = new DirectScanner(TestDirectScanner.SAMPLE_PROGRAM_TEXT);
//
//        boolean debug = true;
//        boolean errorCorrection = true;
//        Parser parser = new Parser(scanner, debug, errorCorrection);
//
//        parser.parse();
//    }
//
//    @Test
//    public void testParsingLongerProgram() {
//        Reader reader = new Reader();
//        String programText = reader.readFromFile("./examples/taTest.tiger");
//
//        Scanner scanner = new DirectScanner(programText);
//
//        boolean debug = true;
//        boolean errorCorrection = true;
//        Parser parser = new Parser(scanner, debug, errorCorrection);
//
//        parser.parse();
//    }
//
//    @Test
//    public void testErrorCorrection() {
//        Scanner scanner = new DirectScanner(ERRONEOUS_SAMPLE_PROGRAM_TEXT);
//
//        boolean debug = true;
//        boolean errorCorrection = true;
//        Parser parser = new Parser(scanner, debug, errorCorrection);
//
//        parser.parse();
//    }
}
