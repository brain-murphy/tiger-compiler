package parser;

import scanner.LexicalError;
import scanner.Scanner;
import scanner.Token;
import scanner.TokenType;
import static scanner.TokenType.NULL;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Parser {

    private boolean debug;

    ParsingTable parsingTable;
    private Scanner scanner;
    private Token currentToken;
    private Stack<Symbol> symbol_stack;

    public Parser(Scanner scanner) {
        parsingTable = new ParsingTable(Rule.ALL_RULES);
        this.scanner = scanner;
    }

    Parser(Scanner scanner, boolean debug) {
        this(scanner);
        this.debug = debug;
    }

    public void parse() {
        symbol_stack = new Stack<>();
        // Conversion between TokenType and String?
        currentToken = scanner.nextToken();
        // Push the start symbol to the stack
        symbol_stack.push(NonTerminal.TIGER_PROGRAM);
        Symbol focus = symbol_stack.peek();
        while( true )
            {
            if(symbol_stack.empty() && !scanner.hasNextToken() ) // No more input, parsing finish successfully
                {
                System.out.println("successful parse");
                break;
                }
            else if( focus instanceof TokenType)
                {
                    TokenType terminal = (TokenType) focus;

                    if (terminal.equals(currentToken.getTokenType())) {
                        acceptToken();

                    } else {
                        List<TokenType> expectedTokens = Collections.singletonList(terminal);
                        handleError(expectedTokens);
                    }
                }
            else // focus is NT
                {
                NonTerminal nonTerminal = (NonTerminal) focus;
                Rule ruleToExpandNonTerminal = parsingTable.getParsingRule(nonTerminal, currentToken.getTokenType());

                    if (ruleToExpandNonTerminal == null) {
                        List<TokenType> expectedTokens = parsingTable.getAugmentedFirstSet(nonTerminal);
                        handleError(expectedTokens);
                    }

                symbol_stack.pop();
                    pushExpansionToSymbolStack(ruleToExpandNonTerminal);


                }
                focus = symbol_stack.peek();
            }

        }

    private void pushExpansionToSymbolStack(Rule ruleToExpandNonTerminal) {
        Symbol[] expansion = ruleToExpandNonTerminal.getExpansion();

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
        symbol_stack.pop();
        currentToken = scanner.nextToken();
    }

    private void handleError(List<TokenType> expectedTokens) {
        //TODO error correction
        throwLexicalError(scanner.getLexicalError(currentToken, expectedTokens));
    }

    private void throwLexicalError(LexicalError error) {
        throw new RuntimeException(error.toString());
    }
}
