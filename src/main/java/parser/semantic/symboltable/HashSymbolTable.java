package parser.semantic.symboltable;

import java.util.HashMap;
import java.util.Map;

public class HashSymbolTable implements SymbolTable {

    private static int nextTemporarySymbolId = 0;

    private static String makeTemporarySymbolId() {
        return "$$" + nextTemporarySymbolId++;
    }

    private Map<String, Symbol> symbols;
    private Map<Symbol, SymbolTable> children;

    private SymbolTable parent;
    private Symbol symbolDefiningScope;

    public HashSymbolTable(SymbolTable parent, Symbol symbolDefiningScope) {
        this.parent = parent;
        this.symbolDefiningScope = symbolDefiningScope;

        symbols = new HashMap<>();
        children = new HashMap<>();
    }

    @Override
    public SymbolTable getParentScope() {
        return parent;
    }

    @Override
    public SymbolTable getChildScope(Symbol symbolDefiningChildScope) {
        return children.get(symbolDefiningChildScope);
    }

    @Override
    public Symbol lookup(String name) {
        HashSymbolTable currentTable = this;

        do {
            if (currentTable.symbols.containsKey(name)) {
                return currentTable.symbols.get(name);
            }
        } while (currentTable.getParentScope() != null);
        
        throw new SymbolTableException("did not recognize symbol with name " + name);
    }

    @Override
    public void insert(Symbol symbol) {
        if (symbols.containsKey(symbol.getName())) {
            throw new SymbolTableException("Symbol table already contains symbol with name " + symbol.getName());
        }

        symbols.put(symbol.getName(), symbol);
    }

    @Override
    public SymbolTable createChildScope(Symbol symbolDefiningChildScope) {
        if (!symbols.containsKey(symbolDefiningChildScope.getName())) {
            throw new SymbolTableException("Symbol table does not contain a symbol with name "
                    + symbolDefiningChildScope.getName() + " and cannot create child scope.");
        }

        if (children.containsKey(symbolDefiningChildScope)) {
            throw new SymbolTableException("There is already a child scope for symbol " + symbolDefiningChildScope.getName());
        }

        SymbolTable newChild = new HashSymbolTable(this, symbolDefiningScope);

        children.put(symbolDefiningChildScope, newChild);

        return newChild;
    }

    @Override
    public Symbol newTemporary() {
        Symbol temporarySymbol = new Symbol(makeTemporarySymbolId());
        temporarySymbol.putAttribute(Attribute.IS_TEMPORARY, true);

        insert(temporarySymbol);

        return temporarySymbol;
    }


}
