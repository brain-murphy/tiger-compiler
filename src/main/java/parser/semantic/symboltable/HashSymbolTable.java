package parser.semantic.symboltable;

import parser.semantic.SemanticException;
import parser.semantic.ir.*;

import java.util.HashMap;
import java.util.Map;

public class HashSymbolTable implements SymbolTable {

    private static int nextSymbolTableEntryId = 0;

    public HashSymbolTable() {
        this(null, null);
    }

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

        initializeLibraryFunctions();
    }

    private void initializeLibraryFunctions() {
        Symbol print = new Symbol("print");
        print.putAttribute(Attribute.TYPE, new FunctionExpressionType(new ExpressionType[]{new StringExpressionType()}, new VoidExpressionType()));
        insert(print);

        Symbol printi = new Symbol("printi");
        printi.putAttribute(Attribute.TYPE, new FunctionExpressionType(new ExpressionType[]{ new IntegerExpressionType() }, new VoidExpressionType()));
        insert(printi);

        Symbol flush = new Symbol("flush");
        flush.putAttribute(Attribute.TYPE, new FunctionExpressionType(new ExpressionType[]{}, new VoidExpressionType()));
        insert(flush);

        Symbol getChar = new Symbol("getchar");
        getChar.putAttribute(Attribute.TYPE, new FunctionExpressionType(new ExpressionType[]{}, new StringExpressionType()));
        insert(getChar);

        Symbol ord = new Symbol("ord");
        ord.putAttribute(Attribute.TYPE, new FunctionExpressionType(new ExpressionType[] {new StringExpressionType()}, new IntegerExpressionType()));
        insert(ord);

        Symbol chr = new Symbol("chr");
        chr.putAttribute(Attribute.TYPE, new FunctionExpressionType(new ExpressionType[] { new IntegerExpressionType()}, new StringExpressionType()));
        insert(chr);

        Symbol size = new Symbol("size");
        size.putAttribute(Attribute.TYPE, new FunctionExpressionType(new ExpressionType[] {new StringExpressionType()}, new IntegerExpressionType()));
        insert(size);

        Symbol substring = new Symbol("substring");
        substring.putAttribute(Attribute.TYPE, new FunctionExpressionType(new ExpressionType[] {new StringExpressionType(), new IntegerExpressionType(), new IntegerExpressionType()}, new StringExpressionType()));
        insert(substring);

        Symbol concat = new Symbol("concat");
        concat.putAttribute(Attribute.TYPE, new FunctionExpressionType(new ExpressionType[] {new StringExpressionType(), new StringExpressionType()}, new StringExpressionType()));
        insert(concat);

        Symbol not = new Symbol("not");
        not.putAttribute(Attribute.TYPE, new FunctionExpressionType(new ExpressionType[]{ new IntegerExpressionType()}, new IntegerExpressionType()));
        insert(not);

        Symbol exit = new Symbol("exit");
        exit.putAttribute(Attribute.TYPE, new FunctionExpressionType(new ExpressionType[]{new IntegerExpressionType()}, new VoidExpressionType()));
        insert(exit);
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

        while(currentTable != null) {
            if (currentTable.symbols.containsKey(name)) {
                return currentTable.symbols.get(name);
            }

            currentTable = (HashSymbolTable) currentTable.getParentScope();
        }

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
        return newLabel(makeLabelId());
    }

    @Override
    public Label newLabel(String name) {
        Label label = new Label("_" + name);

        insert(label);

        return label;
    }

}
