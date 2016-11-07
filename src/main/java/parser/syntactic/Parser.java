package parser.syntactic;

import parser.semantic.ParseStream;
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
    private Token currentToken;
    private Stack<GrammarSymbol> symbolStack;

    private Scanner scanner;

    private ParseStream parseStream;

    public Parser(Scanner scanner, ParseStream parseStream) {
        parsingTable = new ParsingTable(Rule.ALL_RULES);
        this.scanner = scanner;
        this.parseStream = parseStream;
    }

    Parser(Scanner scanner, ParseStream parseStream, boolean debug, boolean errorCorrection) {
        this(scanner, parseStream);
        this.debug = debug;
        this.errorCorrection = errorCorrection;
    }

    public void parse() {
        symbolStack = new Stack<>();
        // Conversion between TokenType and String?
        currentToken = getNextToken();
        // Push the start symbol to the stack
        symbolStack.push(NonTerminal.TIGER_PROGRAM);
        GrammarSymbol focus = symbolStack.peek();
        while( true )
        {
            if(symbolStack.empty() && !scanner.hasNextToken() ) // No more input, parsing finish successfully
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

                symbolStack.pop();
                pushExpansionToSymbolStack(ruleToExpandNonTerminal);


            }
            if (!symbolStack.isEmpty()) {
                focus = symbolStack.peek();
            }
        }

    }

    private void pushExpansionToSymbolStack(Rule ruleToExpandNonTerminal) {
        GrammarSymbol[] expansion = ruleToExpandNonTerminal.getExpansion();

        for (int expansionIndex = expansion.length - 1; expansionIndex >= 0; expansionIndex--) {
            if (expansion[expansionIndex] != NULL) {
                symbolStack.push(expansion[expansionIndex]);
            }
        }
    }

    private void acceptToken() {
        if (debug) {
            System.out.println(currentToken.getTokenType());
        }
        if (!symbolStack.empty()) {
            symbolStack.pop();
        }

        parseStream.put(currentToken.getTokenType(), currentToken.getText());

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

        while (!symbolStack.empty() && symbolStack.peek() != NonTerminal.STAT_SEQ_TAIL) {
            symbolStack.pop();
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
