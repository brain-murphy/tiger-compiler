package parser.semantic.symboltable;

import parser.semantic.ir.Label;

/**
 * Created by Brian on 10/20/2016.
 */
public interface SymbolTable {

    SymbolTable getParentScope();

    SymbolTable getChildScope(SymbolTableEntry symbolDefiningChildScope);

    SymbolTableEntry lookup(String name);

    void insert(SymbolTableEntry symbol);

    SymbolTable createChildScope(SymbolTableEntry symbolDefiningChildScope);

    Symbol newTemporary();

    Label newLabel();
    Label newLabel(String name);
}
