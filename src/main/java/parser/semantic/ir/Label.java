package parser.semantic.ir;

import parser.semantic.symboltable.SymbolTableEntry;

public class Label implements IrCode, SymbolTableEntry {

    private String labelName;

    public Label(String name) {
        labelName = name;
    }

    @Override
    public String getName() {
        return labelName;
    }

    @Override
    public String toString() {
        return labelName + ":";
    }
}
