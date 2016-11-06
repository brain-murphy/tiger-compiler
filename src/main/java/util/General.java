package util;

import parser.syntactic.GrammarSymbol;

public class General {
    public static <T> int arrayHash(T[] array) {
        int hash = 0;

        for (int i = 0; i < array.length; i++) {
            hash += Math.pow(array[i].hashCode(), i + 1);
        }

        return hash;
    }

    public static String expansionToString(GrammarSymbol[] expansion) {
        StringBuilder stringBuilder = new StringBuilder();
        for (GrammarSymbol grammarSymbol : expansion) {
            stringBuilder.append(grammarSymbol.name())
                        .append(' ');
        }

        return stringBuilder.toString().trim();
    }
}
