package scanner;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestLexicalErrorFinder {

    @Test
    public void testFindingValueProblem() {
        String problemString = "100asdf000";

        LexicalErrorFinder lexicalErrorFinder = new LexicalErrorFinder(problemString);

        assertEquals(3, lexicalErrorFinder.findIndexOfFirstError(TokenType.INTLIT));
        assertEquals(3, lexicalErrorFinder.findIndexOfFirstError(TokenType.FLOATLIT));
        assertEquals(0, lexicalErrorFinder.findIndexOfFirstError(TokenType.ID));
        assertEquals(0, lexicalErrorFinder.findIndexOfFirstError(TokenType.LPAREN));
    }
}
