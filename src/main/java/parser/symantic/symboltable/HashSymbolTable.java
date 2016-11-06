package parser.symantic.symboltable;

import java.util.HashMap;
import java.util.Map;

public class HashSymbolTable implements SymbolTable {
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
        return symbols.get(name);
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
}
