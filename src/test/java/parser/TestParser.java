package parser;

import org.junit.Test;
import scanner.DirectScanner;
import scanner.Scanner;
import scanner.TestDirectScanner;

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
    public void testParsingSampleProgram() {
        Scanner scanner = new DirectScanner(TestDirectScanner.SAMPLE_PROGRAM_TEXT);

        boolean debug = true;
        boolean errorCorrection = false;
        Parser parser = new Parser(scanner, debug, errorCorrection);

        parser.parse();
    }

    @Test
    public void testErrorCorrection() {
        Scanner scanner = new DirectScanner(ERRONEOUS_SAMPLE_PROGRAM_TEXT);

        boolean debug = true;
        boolean errorCorrection = true;
        Parser parser = new Parser(scanner, debug, errorCorrection);

        parser.parse();
    }
}
