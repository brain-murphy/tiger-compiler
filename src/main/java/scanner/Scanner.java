package scanner;

import java.util.List;

public interface Scanner {
    Token nextToken();
    boolean hasNextToken();
    Token previousToken();
    Token getTokenAtIndex(int tokenIndex);
    LexicalError getLexicalError(Token problemToken, List<TokenType> expectedTokenTypes);
}
