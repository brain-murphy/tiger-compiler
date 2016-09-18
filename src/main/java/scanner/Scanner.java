package scanner;

public interface Scanner {
    Token nextToken();
    Token previousToken();
    Token getTokenAtIndex(int tokenIndex);
}
