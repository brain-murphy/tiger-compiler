package symantic.symboltable;

/**
 * Created by Brian on 11/2/2016.
 */
public class SymbolInsertionException extends RuntimeException {
    private Symbol symbol;

    public SymbolInsertionException(Symbol symbol) {
        super(makeMessageString(symbol.getName()));
        this.symbol = symbol;
    }

    private static String makeMessageString(String symbolName) {
        return "There is already a symbol in the table that conflicts with new entry \"" + symbolName + "\"";
    }
}
