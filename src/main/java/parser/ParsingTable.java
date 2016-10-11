package parser;

import org.jetbrains.annotations.NotNull;
import scanner.TokenType;
import util.Csv;

import static scanner.TokenType.NULL;

import java.util.*;

class ParsingTable {

    public static final int NO_RULE_FOR_TERMINAL = -1;

    private Rule[] rules;
    private Map<Symbol, Map<TokenType, Integer>> firstSets;
    private Map<Symbol, Map<TokenType, Integer>> followSets;
    private Map<Symbol, Map<TokenType, Integer>> augmentedFirstSets;

    ParsingTable(Rule[] rules) {
        this.rules = rules;

        computeFirstSets();
        computeFollowSets();
        computeAugmentedFirstSets();
    }

    private void computeFirstSets() {
        firstSets = new HashMap<>();
        addEmptySetsForNonTerminals(firstSets);
        computeFirstSetsForTerminals(firstSets);

        boolean firstSetsAreChanging = true;
        while (firstSetsAreChanging) {
            firstSetsAreChanging = false;

            for (int ruleIndex = 0; ruleIndex < rules.length; ruleIndex++) {
                Rule rule = rules[ruleIndex];

                Map<TokenType, Integer> newFirstSet = computeNewFirstSet(ruleIndex);

                Set<TokenType> oldFirstSet = getCurrentFirstSet(rule.getNonTerminalExpanded());

                for (TokenType terminal : newFirstSet.keySet()) {
                    if (!oldFirstSet.contains(terminal)) {
                        firstSetsAreChanging = true;
                    }

                    firstSets.get(rule.getNonTerminalExpanded()).put(terminal, ruleIndex);
                }

            }
        }
    }

    private void addEmptySetsForNonTerminals(Map<Symbol, Map<TokenType, Integer>> set) {
        for (NonTerminal nonTerminal : NonTerminal.values()) {
            set.put(nonTerminal, new HashMap<>());
        }
    }

    private void computeFirstSetsForTerminals(Map<Symbol, Map<TokenType, Integer>> firstSets) {
        for (TokenType terminal : TokenType.values()) {

            Map<TokenType, Integer> firstSetForTerminal = new HashMap<>();
            firstSetForTerminal.put(terminal, NO_RULE_FOR_TERMINAL);

            firstSets.put(terminal, firstSetForTerminal);
        }
    }

    @NotNull
    private Map<TokenType, Integer> computeNewFirstSet(int ruleIndex) {
        Rule rule = rules[ruleIndex];

        Map<TokenType, Integer> newFirstSet = new HashMap<>();

        Symbol[] expansion = rule.getExpansion();

        int expansionSymbolIndex = 0;
        Set<TokenType> firstSetOfExpansionSymbol = null;

        do {
            firstSetOfExpansionSymbol = getCurrentFirstSet(expansion[expansionSymbolIndex]);

            for (TokenType terminal : firstSetOfExpansionSymbol) {
                newFirstSet.put(terminal, ruleIndex);
            }

            expansionSymbolIndex += 1;
        } while (firstSetOfExpansionSymbol.contains(NULL) && expansionSymbolIndex < expansion.length);

        boolean entireRuleIsNullable = expansionSymbolIndex == expansion.length && firstSetOfExpansionSymbol.contains(NULL);
        if (!entireRuleIsNullable) {
            newFirstSet.remove(NULL);
        }

        return newFirstSet;
    }

    private Set<TokenType> getCurrentFirstSet(Symbol symbol) {
        return firstSets.get(symbol).keySet();
    }

    private void computeFollowSets() {
        followSets = new HashMap<>();
        addEmptySetsForNonTerminals(followSets);

        boolean followSetsAreChanging = true;
        while (followSetsAreChanging) {
            followSetsAreChanging = false;

            for (Rule rule : rules) {

                Map<TokenType, Integer> trailer = new HashMap<>(getCurrentFollowSet(rule.getNonTerminalExpanded()));

                Symbol[] expansion = rule.getExpansion();
                for (int expansionSymbolIndex = expansion.length - 1; expansionSymbolIndex >= 0; expansionSymbolIndex--) {

                    if (expansion[expansionSymbolIndex] instanceof NonTerminal) {
                        NonTerminal expansionSymbol = (NonTerminal) expansion[expansionSymbolIndex];

                        //no follow set needed if the symbol is not nullable
                        boolean followSetsChanged = addToFollowSet(trailer, expansionSymbol);

                        if (followSetsChanged) {
                            followSetsAreChanging = true;
                        }

                        trailer = computeNextTrailer(trailer, expansionSymbol);

                    } else {
                        trailer = new HashMap<>(firstSets.get(expansion[expansionSymbolIndex]));
                    }
                }
            }
        }
    }

    @NotNull
    private Map<TokenType, Integer> computeNextTrailer(Map<TokenType, Integer> trailer, NonTerminal expansionSymbol) {
        if (getCurrentFirstSet(expansionSymbol).contains(NULL)) {
            trailer.putAll(firstSets.get(expansionSymbol));
        } else {
            trailer = new HashMap<>(firstSets.get(expansionSymbol));
        }
        return trailer;
    }

    private boolean addToFollowSet(Map<TokenType, Integer> trailer, NonTerminal expansionSymbol) {
        boolean followSetsAreChanging = false;
        Map<TokenType, Integer> followSet = followSets.get(expansionSymbol);

        for (TokenType terminal : trailer.keySet()) {
            if (!followSet.containsKey(terminal)) {
                followSetsAreChanging = true;
            }

            Integer ruleIndexWhereSymbolIsNullable = firstSets.get(expansionSymbol).get(NULL);

            followSet.put(terminal, ruleIndexWhereSymbolIsNullable);
        }
        return followSetsAreChanging;
    }

    private Map<TokenType, Integer> getCurrentFollowSet(NonTerminal nonTerminal) {
        return followSets.get(nonTerminal);
    }

    private void computeAugmentedFirstSets() {
        augmentedFirstSets = new HashMap<>();
        addEmptySetsForNonTerminals(augmentedFirstSets);

        for (NonTerminal nonTerminal : NonTerminal.values()) {
            Map<TokenType, Integer> firstSet = firstSets.get(nonTerminal);

            augmentedFirstSets.get(nonTerminal).putAll(firstSet);

            if (firstSet.containsKey(NULL)) {
                augmentedFirstSets.get(nonTerminal).putAll(followSets.get(nonTerminal));
            }
        }

        firstSets = null;
        followSets = null;
    }

    public Rule getParsingRule(NonTerminal focus, TokenType lookahead) {
        Integer ruleIndex = augmentedFirstSets.get(focus).get(lookahead);

        if (ruleIndex == null) {
            return null;
        } else {
            return rules[ruleIndex];
        }
    }

    public List<TokenType> getAugmentedFirstSet(NonTerminal nonTerminal) {
        return new ArrayList<>(augmentedFirstSets.get(nonTerminal).keySet());
    }

    @Override
    public String toString() {
        Csv csv = new Csv("focus", "lookahead", "ruleToUse");

        for (Symbol focus : augmentedFirstSets.keySet()) {

            Set<TokenType> possibleLookaheads = augmentedFirstSets.get(focus).keySet();
            for (TokenType lookahead : possibleLookaheads) {

                Rule ruleToUse = rules[augmentedFirstSets.get(focus).get(lookahead)];
                csv.addRow(focus.name(), lookahead.name(), expansionToString(ruleToUse.getExpansion()));
            }
        }

        return csv.toString();
    }

    private String expansionToString(Symbol[] expansion) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Symbol symbol : expansion) {
            stringBuilder.append(symbol.name())
                        .append(' ');
        }

        return stringBuilder.toString().trim();
    }
}
