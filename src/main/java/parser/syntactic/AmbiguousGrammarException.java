package parser.syntactic;

import scanner.TokenType;

public class AmbiguousGrammarException extends RuntimeException {
    private Rule[] ambiguousRules;
    private TokenType commonLookahead;

    public AmbiguousGrammarException(Rule[] ambiguousRules, TokenType commonLookahead) {
        this.ambiguousRules = ambiguousRules;
        this.commonLookahead = commonLookahead;
    }

    @Override
    public String getMessage() {
        StringBuilder stringBuilder = new StringBuilder("An ambiguity was detected in the grammar when computing the parse table. The following rules cannot" +
                " be distiguished given the lookahead token " + commonLookahead.name() +"\n");

        for (Rule ambiguousRule : ambiguousRules) {
            stringBuilder.append(ambiguousRule)
                        .append("\n");
        }

        return stringBuilder.toString();
    }
}
