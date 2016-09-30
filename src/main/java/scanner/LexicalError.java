package scanner;

import java.util.List;

public class LexicalError {
    private String problemString;
    private int lineNumber;
    private List<TokenType> expectations;

    private LexicalErrorFinder lexicalErrorFinder;

    public LexicalError(String problemString, int lineNumber, List<TokenType> expectations) {
        this.problemString = problemString;
        this.lineNumber = lineNumber;
        this.expectations = expectations;

        lexicalErrorFinder = new LexicalErrorFinder(problemString);
    }

    public String getProblemString() {
        return problemString;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getReason(TokenType tokenType) {
        int indexOfError = lexicalErrorFinder.findIndexOfFirstError(tokenType);
        return "Expected " + tokenType.name() + " but could not match.\n" +
                problemString.substring(0, indexOfError) + " ^ " + problemString.substring(indexOfError);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Lexical Error: could not match any expected token on line:" + getLineNumber() + "\n");
        for (TokenType tokenType : expectations) {
            stringBuilder
                    .append(getReason(tokenType))
                    .append("\n");
        }
        return stringBuilder.toString();
    }

    public List<TokenType> getExpectations() {
        return expectations;
    }
}
