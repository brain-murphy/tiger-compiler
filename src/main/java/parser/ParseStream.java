package parser;


import scanner.TokenType;
import parser.symantic.symboltable.Attribute;
import parser.symantic.symboltable.Symbol;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;

public class ParseStream {
    private static Set<GrammarSymbol> usefulSymbols = new HashSet<>(Arrays.asList(NonTerminal.TYPE_DECLARATION, TokenType.ID, NonTerminal.TYPE, NonTerminal.VAR_DECLARATION, ))

    private SynchronousQueue<GrammarSymbol> filteredExpansions;

    public ParseStream() {
        filteredExpansions = new SynchronousQueue<>();
    }

    protected void put(GrammarSymbol grammarSymbol, String text) {
        if (isUsefulForSemanticParse(grammarSymbol)) {
            Symbol newSymbol = new Symbol(text);
            newSymbol.putAttribute(Attribute.GRAMMAR_SYMBOL, grammarSymbol);

            filteredExpansions.put(newSymbol);
        }
    }

    public

    private boolean isUsefulForSemanticParse(GrammarSymbol grammarSymbol) {
        if (grammarSymbol instanceof NonTerminal) {
            NonTerminal nonTerminal = (NonTerminal) grammarSymbol;

            switch (nonTerminal) {
                case NonTerminal.TYPE_DECLARATION:

            }
        } else { //terminal

        }
    }
}
