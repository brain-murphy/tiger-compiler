package parser.semantic.symboltable;

import parser.semantic.SemanticException;
import parser.semantic.ir.Label;

import java.util.HashMap;
import java.util.Map;

public class HashSymbolTable implements SymbolTable {

    private static int nextSymbolTableEntryId = 0;

    private static String makeTemporarySymbolId() {
        return "$$" + nextSymbolTableEntryId++;
    }
    private static String makeLabelId() {
        return "L" + nextSymbolTableEntryId++;
    }

    private Map<String, SymbolTableEntry> symbols;
    private Map<SymbolTableEntry, SymbolTable> children;

    private SymbolTable parent;
    private SymbolTableEntry symbolDefiningScope;

    public HashSymbolTable(SymbolTable parent, SymbolTableEntry symbolDefiningScope) {
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
    public SymbolTable getChildScope(SymbolTableEntry symbolDefiningChildScope) {
        return children.get(symbolDefiningChildScope);
    }

    @Override
    public SymbolTableEntry lookup(String name) {
        HashSymbolTable currentTable = this;

        do {
            if (currentTable.symbols.containsKey(name)) {
                return currentTable.symbols.get(name);
            }
        } while (currentTable.getParentScope() != null);

        throw new SemanticException("did not recognize symbol with name " + name);
    }

    @Override
    public void insert(SymbolTableEntry symbol) {
        if (symbols.containsKey(symbol.getName())) {
            throw new SemanticException("Symbol table already contains symbol with name " + symbol.getName());
        }

        symbols.put(symbol.getName(), symbol);
    }

    @Override
    public SymbolTable createChildScope(SymbolTableEntry symbolDefiningChildScope) {
        if (!symbols.containsKey(symbolDefiningChildScope.getName())) {
            throw new SemanticException("Symbol table does not contain a symbol with name "
                    + symbolDefiningChildScope.getName() + " and cannot create child scope.");
        }

        if (children.containsKey(symbolDefiningChildScope)) {
            throw new SemanticException("There is already a child scope for symbol " + symbolDefiningChildScope.getName());
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

    @Override
    public Label newLabel() {
        Label label = new Label(makeLabelId());

        insert(label);

        return label;
    }

}
