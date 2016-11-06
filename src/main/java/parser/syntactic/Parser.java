package parser.syntactic;

import scanner.*;

import static scanner.TokenType.NULL;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Parser {

    private boolean debug = true;
    private boolean errorCorrection = true;

    private boolean didThrowError = false;

    ParsingTable parsingTable;
    private Scanner scanner;
    private Token currentToken;
    private Stack<GrammarSymbol> symbol_stack;

    public Parser(Scanner scanner) {
        parsingTable = new ParsingTable(Rule.ALL_RULES);
        this.scanner = scanner;
    }

    Parser(Scanner scanner, boolean debug, boolean errorCorrection) {
        this(scanner);
        this.debug = debug;
        this.errorCorrection = errorCorrection;
    }

    public void parse() {
        symbol_stack = new Stack<>();
        // Conversion between TokenType and String?
        currentToken = getNextToken();
        // Push the start symbol to the stack
        symbol_stack.push(NonTerminal.TIGER_PROGRAM);
        GrammarSymbol focus = symbol_stack.peek();
        while( true )
        {
            if(symbol_stack.empty() && !scanner.hasNextToken() ) // No more input, parsing finish successfully
            {
                System.out.println("End of parse.");

                if (!didThrowError) {
                    System.out.println("successful parse");
                }

                break;
            }
            else if( focus instanceof TokenType)
            {
                TokenType terminal = (TokenType) focus;

                if (terminal.equals(currentToken.getTokenType())) {
                    acceptToken();

                } else {
                    didThrowError = true;
                    List<TokenType> expectedTokens = Collections.singletonList(terminal);

                    if (errorCorrection) {
                        correctError(expectedTokens);
                    } else {
                        throwError(scanner.getLexicalError(currentToken, expectedTokens));
                    }

                }
            }
            else // focus is NT
            {
                NonTerminal nonTerminal = (NonTerminal) focus;
                Rule ruleToExpandNonTerminal = parsingTable.getParsingRule(nonTerminal, currentToken.getTokenType());

                if (ruleToExpandNonTerminal == null) {
                    didThrowError = true;
                    List<TokenType> expectedTokens = parsingTable.getAugmentedFirstSet(nonTerminal);

                    if (errorCorrection) {
                        NonTerminal newFocus = correctError(nonTerminal);
                        ruleToExpandNonTerminal = parsingTable.getParsingRule(newFocus, currentToken.getTokenType());
                    } else {
                        throwError(scanner.getLexicalError(currentToken, expectedTokens));
                    }
                }

                symbol_stack.pop();
                pushExpansionToSymbolStack(ruleToExpandNonTerminal);


            }
            if (!symbol_stack.isEmpty()) {
                focus = symbol_stack.peek();
            }
        }

    }

    private void pushExpansionToSymbolStack(Rule ruleToExpandNonTerminal) {
        GrammarSymbol[] expansion = ruleToExpandNonTerminal.getExpansion();

        for (int expansionIndex = expansion.length - 1; expansionIndex >= 0; expansionIndex--) {
            if (expansion[expansionIndex] != NULL) {
                symbol_stack.push(expansion[expansionIndex]);
            }
        }
    }

    private void acceptToken() {
        if (debug) {
            System.out.println(currentToken.getTokenType());
        }
        if (!symbol_stack.empty()) {
            symbol_stack.pop();
        }
        if (scanner.hasNextToken()) {
            currentToken = getNextToken();
        }
    }

    private NonTerminal correctError(NonTerminal focus) {
        List<TokenType> expectedTokens = parsingTable.getAugmentedFirstSet(focus);
        Token problemToken = currentToken;

        while (scanner.hasNextToken() && !expectedTokens.contains(problemToken.getTokenType())) {

            printErrorRecoveryMessage(scanner.getLexicalError(problemToken, expectedTokens));

            if (problemToken.getTokenType() == TokenType.SEMI) {
                restartParsingAtStatement();
                return NonTerminal.STAT_SEQ_TAIL;
            }

            problemToken = getNextToken();
        }

        currentToken = problemToken;

        LexicalError lexicalError = scanner.getLexicalError(currentToken, expectedTokens);
        printErrorRecoveryMessage(lexicalError);

        return focus;
    }

    private void correctError(List<TokenType> expectedTokens) {
        Token problemToken = currentToken;

        while (scanner.hasNextToken() && !expectedTokens.contains(problemToken.getTokenType())) {

            printErrorRecoveryMessage(scanner.getLexicalError(problemToken, expectedTokens));

            if (problemToken.getTokenType() == TokenType.SEMI) {
                restartParsingAtStatement();
                return;
            }

            problemToken = getNextToken();
        }

        acceptToken();

        LexicalError lexicalError = scanner.getLexicalError(currentToken, expectedTokens);
        printErrorRecoveryMessage(lexicalError);
    }

    private void restartParsingAtStatement() {
        currentToken = getNextToken();

        while (!symbol_stack.empty() && symbol_stack.peek() != NonTerminal.STAT_SEQ_TAIL) {
            symbol_stack.pop();
        }
    }

    private Token getNextToken() {
        while (true) {
            try {
                return scanner.nextToken();
            } catch (LexicalException lexicalException) {
                didThrowError = true;
                System.out.println(lexicalException.getMessage());
            }
        }
    }

    private void throwError(LexicalError error) {
        throw new RuntimeException(error.toString());
    }

    private void printErrorRecoveryMessage(LexicalError error) {
        System.out.println(error.toString());
        System.out.println("Attempting error correction by skipping token.");
    }
}
