package util;

import junit.framework.Assert;
import org.junit.Test;
import parser.Parser;
import scanner.DirectScanner;
import scanner.Scanner;

public class TestReader {

    public static final String BRIANS_TEST_PROGRAM_PATH = "./test.tiger";
    public static final String ACTUAL_FILE_CONTENTS = "let\n" +
            " type ArrayInt = array [100] of int;\n" +
            " var X, Y : ArrayInt := 10;\n" +
            " var i, sum : int := 0;\n" +
            "in\n" +
            " for i := 1 to 100 do\n" +
            " sum := sum + X[i] * Y[i];\n" +
            " enddo\n" +
            " printi(sum);\n" +
            "end";

    @Test
    public void testReadingFromFile() {
        Reader reader = new Reader();

        String programText = reader.readFromFile(BRIANS_TEST_PROGRAM_PATH);

        Assert.assertEquals("program not read correctly", ACTUAL_FILE_CONTENTS, programText);
    }
}
