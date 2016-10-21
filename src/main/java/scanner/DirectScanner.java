package scanner;

import java.util.ArrayList;
import java.util.List;

public class DirectScanner implements Scanner {

    public static final int COMMENT_OPEN_LENGTH = "/*".length();
    public static final int COMMENT_CLOSE_LENGTH = "*/".length();

    private int cursorPosition;
    private String fileText;

    private List<Token> scannedTokens;

    public DirectScanner(String fileText) {
        this.fileText = fileText;
        scannedTokens = new ArrayList<>();
    }

    @Override
    public Token nextToken() {
        if (!hasNextToken()) {
            throw new EofException();
        }

        token();
        return scannedTokens.get(scannedTokens.size() - 1);
    }

    @Override
    public boolean hasNextToken() {
        return cursorPosition < fileText.length();
    }

    private void token() {
        char nextChar = charAt(cursorPosition);

        switch (nextChar) {

            case 'a':case 'b':case 'c':case 'd':case 'e':case 'f':case 'g':case 'h':case 'i':case 'j':
            case 'k':case 'l':case 'm':case 'n':case 'o':case 'p':case 'q':case 'r':case 's':case 't':
            case 'u':case 'v':case 'w':case 'x':case 'y':case 'z':case 'A':case 'B':case 'C':case 'D':
            case 'E':case 'F':case 'G':case 'H':case 'I':case 'J':case 'K':case 'L':case 'M':case 'N':
            case 'O':case 'P':case 'Q':case 'R':case 'S':case 'T':case 'U':case 'V':case 'W':case 'X':
            case 'Y':case 'Z':
                idOrKeyword();
                break;

            case '0':case '1':case '2':case '3':case '4':case '5':case '6':case '7':case '8':case '9':
                integerOrFloatLiteral();
                break;

            case '<':
                openAngleBracketOp();
                break;

            case '>':
                closeAngleBracketOp();
                break;

            case ':':
                colonOp();
                break;

            case '/':
                forwardSlash();
                break;

            case ',':
                acceptToken(cursorPosition + 1, TokenType.COMMA);
                break;
            case ';':
                acceptToken(cursorPosition + 1, TokenType.SEMI);
                break;
            case '(':
                acceptToken(cursorPosition + 1, TokenType.LPAREN);
                break;
            case ')':
                acceptToken(cursorPosition + 1, TokenType.RPAREN);
                break;
            case '[':
                acceptToken(cursorPosition + 1, TokenType.LBRACK);
                break;
            case ']':
                acceptToken(cursorPosition + 1, TokenType.RBRACK);
                break;
            case '{':
                acceptToken(cursorPosition + 1, TokenType.LBRACE);
                break;
            case '}':
                acceptToken(cursorPosition + 1, TokenType.RBRACE);
                break;
            case '.':
                acceptToken(cursorPosition + 1, TokenType.PERIOD);
                break;
            case '+':
                acceptToken(cursorPosition + 1, TokenType.PLUS);
                break;
            case '-':
                acceptToken(cursorPosition + 1, TokenType.MINUS);
                break;
            case '*':
                acceptToken(cursorPosition + 1, TokenType.MULT);
                break;
            case '=':
                acceptToken(cursorPosition + 1, TokenType.EQ);
                break;
            case '&':
                acceptToken(cursorPosition + 1, TokenType.AND);
                break;
            case '|':
                acceptToken(cursorPosition + 1, TokenType.OR);
                break;
            default:
                int problemIndex = cursorPosition;
                advanceCursor(cursorPosition + 1);

                throw new LexicalException(nextChar + " is not a valid character in the Tiger language.\n" +
                        "See line " + getCursorLineNumber() + "\n" +
                        fileText.substring(problemIndex - 10, problemIndex + 1) + "<--");
        }
    }

    private void idOrKeyword() {
        int scanningIndex = cursorPosition + 1;

        while (hasNextChar(scanningIndex)) {
            char nextChar = charAt(scanningIndex);

            if (!Character.isLetterOrDigit(nextChar)) {
                break;
            }

            scanningIndex += 1;
        }

        boolean isKeyword = keyword(scanningIndex);

        if (!isKeyword) {
            acceptToken(scanningIndex, TokenType.ID);
        }
    }

    private boolean hasNextChar(int scanningIndex) {
        return scanningIndex < fileText.length();
    }

    private boolean keyword(int scanningIndex) {
        String scannedString = fileText.substring(cursorPosition, scanningIndex);

        boolean isKeyword = false;

        for (TokenType tokenType : TokenType.KEYWORDS) {
            if (tokenType.getPattern().equals(scannedString)) {
                acceptToken(scanningIndex, tokenType);
                isKeyword = true;
                break;
            }
        }
        return isKeyword;
    }

    private void integerOrFloatLiteral() {
        int scanningIndex = cursorPosition + 1;

        if (charAt(cursorPosition) == '0') {
            if (Character.isDigit(charAt(scanningIndex))) {
                throw new LexicalException("Lexical Error: int literal cannot start with a zero.");
            }
        }

        while (hasNextChar(scanningIndex)) {
            char nextChar = charAt(scanningIndex);

            if (!Character.isDigit(nextChar)) {
                break;
            }

            scanningIndex += 1;
        }

        if (charAt(scanningIndex) == '.') {
            floatLiteral(scanningIndex);
        } else {
            acceptToken(scanningIndex, TokenType.INTLIT);
        }
    }

    /**
     *   Starts scanning where a period was discovered.
     */
    private void floatLiteral(int periodIndex) {
        int scanningIndex = periodIndex + 1;

        while (hasNextChar(scanningIndex)) {
            char nextChar = charAt(scanningIndex);

            if (!Character.isDigit(nextChar)) {
                break;
            }

            scanningIndex += 1;
        }

        acceptToken(scanningIndex, TokenType.FLOATLIT);
    }

    private void openAngleBracketOp() {
        int scanningIndex = cursorPosition + 1;

        if (hasNextChar(scanningIndex)) {
            char nextChar = charAt(scanningIndex);

            switch (nextChar) {
                case '=':
                    acceptToken(cursorPosition + 2, TokenType.LESSEREQ);
                    break;
                case '>':
                    acceptToken(cursorPosition + 2, TokenType.NEQ);
                    break;
                default:
                    acceptToken(scanningIndex, TokenType.LESSER);
                    break;
            }

        } else {
            acceptToken(scanningIndex, TokenType.LESSER);
        }
    }

    private void closeAngleBracketOp() {
        int scanningIndex = cursorPosition + 1;
        if (hasNextChar(scanningIndex)) {
            char nextChar = charAt(scanningIndex);

            switch (nextChar) {
                case '=':
                    acceptToken(cursorPosition + 2, TokenType.GREATEREQ);
                    break;
                default:
                    acceptToken(scanningIndex, TokenType.GREATER);
                    break;
            }
        } else {
            acceptToken(scanningIndex, TokenType.GREATER);
        }
    }

    private void colonOp() {
        int scanningIndex = cursorPosition + 1;
        if (hasNextChar(scanningIndex)) {
            char nextChar = charAt(scanningIndex);

            if (nextChar == '=') {
                acceptToken(cursorPosition + 2, TokenType.ASSIGN);
            } else {
                acceptToken(scanningIndex, TokenType.COLON);
            }
        } else {
            acceptToken(scanningIndex, TokenType.COLON);
        }
    }

    private char charAt(int scanningIndex) {
        return fileText.charAt(scanningIndex);
    }

    private void acceptToken(int delimitingCharacter, TokenType tokenType) {
        String tokenText = fileText.substring(cursorPosition, delimitingCharacter);
        scannedTokens.add(new Token(tokenType, tokenText, cursorPosition));

        advanceCursor(delimitingCharacter);
    }

    private void advanceCursor(int characterIndex) {
        cursorPosition = characterIndex;

        advanceCursorPastWhitespace();
    }

    private void advanceCursorPastWhitespace() {
        while (hasNextChar(cursorPosition) && Character.isWhitespace(charAt(cursorPosition))) {
            cursorPosition++;
        }
    }

    @Override
    public Token previousToken() {
        return scannedTokens.get(scannedTokens.size() - 1);
    }

    @Override
    public Token getTokenAtIndex(int tokenIndex) {
        while (scannedTokens.size() <= tokenIndex) {
            nextToken();
        }

        return scannedTokens.get(tokenIndex);
    }

    @Override
    public LexicalError getLexicalError(Token problemToken, List<TokenType> expectedTokenTypes) {
        return new LexicalError(problemToken.getText(), getCursorLineNumber(), expectedTokenTypes);
    }

    private int getCursorLineNumber() {
        int numNewlines = 0;
        for (int i = 0; i < cursorPosition; i++) {
            if (isNewline(charAt(i))) {
                numNewlines += 1;
            }
        }

        return numNewlines;
    }

    private boolean isNewline(char character) {
        return !String.valueOf(character).matches("."); // "." matches all characters except newlines.
    }

    private void forwardSlash() {
        int scanningIndex = cursorPosition + 1;

        if (hasNextChar(scanningIndex)) {
            char nextChar = charAt(scanningIndex);

            if (nextChar == '*') {
                comment();
            } else {
                acceptToken(scanningIndex, TokenType.DIV);
            }

        } else {
            acceptToken(scanningIndex, TokenType.DIV);
        }
    }

    private void comment() {
        int scanningIndex = cursorPosition + COMMENT_OPEN_LENGTH;

        while (hasNextChar(scanningIndex + 1)) {

            if (charAt(scanningIndex) == '*' && charAt(scanningIndex + 1) == '/') {
                advanceCursor(scanningIndex + COMMENT_CLOSE_LENGTH);
                token();

                return;
            }
            scanningIndex = scanningIndex + 1;
        }

        throw new LexicalException("File ended while comment was not closed.");
    }
}
