package parser;


class Token {
    private TokenType tokenType;
    private String text;

    public Token(TokenType tokenType, String text) {
        this.tokenType = tokenType;
        this.text = text;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getText() {
        return text;
    }
}
