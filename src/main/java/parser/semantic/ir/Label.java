package parser.semantic.ir;

import parser.semantic.symboltable.SymbolTableEntry;

public class Label implements IrCode, SymbolTableEntry {

    private static int nextLabelId = 0;
    private static String makeLabelId() {
        return "L" + nextLabelId++;
    }


    private String labelName;

    public Label() {
        labelName = makeLabelId();
    }

    @Override
    public String getName() {
        return labelName;
    }
}
