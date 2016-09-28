package scanner;


import java.util.ArrayList;
import java.util.regex.Matcher;

public class TigerScanner implements Scanner {

    //TODO doesn't match ids or constants. There are no TokenTypes to represent them rn.

    private String fileText;
    private int cursorPosition;

    private ArrayList<Token> scannedTokens;


    public TigerScanner(String fileText) {
        this.fileText = fileText.trim();
    }

    public Token nextToken() {
        return nextLongestToken();
    }

    private Token nextLongestToken() {
        if (cursorPosition >= fileText.length()) {
            throw new RuntimeException("end of input");
        }

        String notScannedProgramText = fileText.substring(cursorPosition);

        Token bestMatch = new Token(TokenType.AND, "");
        boolean didMatchToken = false;

        Matcher matcher = TokenType.AND.getRegex().matcher(notScannedProgramText);
        for (TokenType tokenType : TokenType.values()) {
            matcher.usePattern(tokenType.getRegex());

            if (isMatch(matcher)) {
                int matchLength = matcher.end() - matcher.start();

                if (isBetterMatch(bestMatch, tokenType, matchLength)) {
                    bestMatch = new Token(tokenType, notScannedProgramText.substring(matcher.start(), matcher.end()));
                    didMatchToken = true;
                }
            }
        }

        if (didMatchToken) {
            scannedTokens.add(bestMatch);
            cursorPosition = cursorPosition + bestMatch.getText().length();
            advanceCursorPastWhitespace();
        } else {
            throw new RuntimeException("could not match token");
        }

        return bestMatch;
    }

    private void advanceCursorPastWhitespace() {
        while (Character.isWhitespace(fileText.charAt(cursorPosition))) {
            cursorPosition++;
        }
    }

    private boolean isMatch(Matcher matcher) {
        return matcher.find() && matcher.start() == cursorPosition;
    }

    private boolean isBetterMatch(Token bestMatch, TokenType tokenType, int matchLength) {
        return matchLength > bestMatch.getText().length()
                || (matchLength == bestMatch.getText().length()
                    && tokenType.getPrecedence() > bestMatch.getTokenType().getPrecedence());
    }

    public Token previousToken() {
        return scannedTokens.get(scannedTokens.size() - 1);
    }

    public Token getTokenAtIndex(int tokenIndex) {
        if (tokenIndex >= scannedTokens.size()) {
            while (scannedTokens.size() < tokenIndex) {
                nextLongestToken();
            }
        }

        return scannedTokens.get(tokenIndex);
    }
}
