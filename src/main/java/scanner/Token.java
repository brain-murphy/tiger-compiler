package scanner;

public class Token {
    private TokenType tokenType;
    private String text;
    private int inputStringIndex;

    public Token(TokenType tokenType, String text, int inputStringIndex) {
        this.tokenType = tokenType;
        this.text = text;
        this.inputStringIndex = inputStringIndex;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getText() {
        return text;
    }
}
