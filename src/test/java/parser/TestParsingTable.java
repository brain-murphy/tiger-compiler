package parser;

import org.junit.Test;
import scanner.DirectScanner;
import scanner.Scanner;
import scanner.TestDirectScanner;

public class TestParsingTable {

    @Test
    public void testPrintParsingTable() {
        Scanner scanner = new DirectScanner(TestDirectScanner.SAMPLE_PROGRAM_TEXT);

        Parser parser = new Parser(scanner);

        System.out.println(parser.parsingTable.toString());
    }
}
