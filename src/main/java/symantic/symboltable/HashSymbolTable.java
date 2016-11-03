package symantic.symboltable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashSymbolTable implements SymbolTable {

    private Map<String, Symbol> symbols;

    private Map<Symbol, SymbolTable> childTables;
    private SymbolTable parentTable;

    public HashSymbolTable(SymbolTable parentTable) {
        symbols = new HashMap<>();
        childTables = new HashMap<>();
        this.parentTable = parentTable;
    }

    @Override
    public SymbolTable getParentScope() {
        return parentTable;
    }

    @Override
    public SymbolTable getChildScope(Symbol symbolDefiningChildScope) {
        return childTables.get(symbolDefiningChildScope);
    }

    @Override
    public Symbol lookup(String name) {
        return symbols.get(name);
    }

    @Override
    public void insert(Symbol symbol) {
        if (symbols.containsKey(symbol.getName())) {
            throw new SymbolInsertionException(symbol);
        }

        symbols.put(symbol.getName(), symbol);
    }

    @Override
    public SymbolTable createChildScope(Symbol symbolDefiningChildScope) {
        SymbolTable childTable = new HashSymbolTable(this);

        childTables.put(symbolDefiningChildScope, childTable);

        return childTable;
    }
}
