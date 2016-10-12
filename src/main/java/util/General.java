package util;

import parser.Symbol;

public class General {
    public static <T> int arrayHash(T[] array) {
        int hash = 0;

        for (int i = 0; i < array.length; i++) {
            hash += Math.pow(array[i].hashCode(), i + 1);
        }

        return hash;
    }

    public static String expansionToString(Symbol[] expansion) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Symbol symbol : expansion) {
            stringBuilder.append(symbol.name())
                        .append(' ');
        }

        return stringBuilder.toString().trim();
    }
}
