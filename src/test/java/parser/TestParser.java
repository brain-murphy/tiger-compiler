package parser;

import org.junit.Test;
import parser.Parser;
import scanner.DirectScanner;
import scanner.Scanner;
import scanner.TestDirectScanner;

public class TestParser {
    @Test
    public void testParsingSampleProgram() {
        Scanner scanner = new DirectScanner(TestDirectScanner.SAMPLE_PROGRAM_TEXT);

        boolean debug = true;
        Parser parser = new Parser(scanner, debug);

        parser.parse();
    }
}
