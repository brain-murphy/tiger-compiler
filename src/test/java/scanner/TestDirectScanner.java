package scanner;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestDirectScanner {

    public static final String SAMPLE_PROGRAM_TEXT = "let\n" +
            " type ArrayInt = array [100] of int; \n" +
            " var X, Y : ArrayInt := 10; \n" +
            " var i, sum : int := 0; /* comment! */\n" +
            "in\n" +
            " for i := 1 to 100 do \n" +
            " sum := sum + X[i] * Y[i];\n" +
            " sum := myFunction();\n" +
            " enddo;\n" +
            " printi(sum); \n" +
            "end";

    Scanner scanner;

    @Before
    public void setUp() {
        scanner = new DirectScanner(SAMPLE_PROGRAM_TEXT);
    }

    @Test
    public void testScanningSampleProgram() {

        assertNextToken(TokenType.LET);

        assertNextToken(TokenType.TYPE);
        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.EQ);
        assertNextToken(TokenType.ARRAY);
        assertNextToken(TokenType.LBRACK);
        assertNextToken(TokenType.INTLIT);
        assertNextToken(TokenType.RBRACK);
        assertNextToken(TokenType.OF);
        assertNextToken(TokenType.INTTYPEID);
        assertNextToken(TokenType.SEMI);

        assertNextToken(TokenType.VAR);
        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.COMMA);
        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.COLON);
        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.ASSIGN);
        assertNextToken(TokenType.INTLIT);
        assertNextToken(TokenType.SEMI);

        assertNextToken(TokenType.VAR);
        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.COMMA);
        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.COLON);
        assertNextToken(TokenType.INTTYPEID);
        assertNextToken(TokenType.ASSIGN);
        assertNextToken(TokenType.INTLIT);
        assertNextToken(TokenType.SEMI);

        assertNextToken(TokenType.IN);
        assertNextToken(TokenType.FOR);
        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.ASSIGN);
        assertNextToken(TokenType.INTLIT);
        assertNextToken(TokenType.TO);
        assertNextToken(TokenType.INTLIT);
        assertNextToken(TokenType.DO);

        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.ASSIGN);
        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.PLUS);
        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.LBRACK);
        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.RBRACK);
        assertNextToken(TokenType.MULT);
        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.LBRACK);
        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.RBRACK);
        assertNextToken(TokenType.SEMI);

        assertNextToken(TokenType.ENDDO);
        assertNextToken(TokenType.SEMI);

        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.LPAREN);
        assertNextToken(TokenType.ID);
        assertNextToken(TokenType.RPAREN);
        assertNextToken(TokenType.SEMI);

        assertNextToken(TokenType.END);
    }

    private void assertNextToken(TokenType tokenType) {
        assertEquals(tokenType, scanner.nextToken().getTokenType());
    }
}
