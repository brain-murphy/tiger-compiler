package parser.semantic.ir;

import parser.semantic.symboltable.Symbol;
import parser.semantic.symboltable.SymbolTableEntry;

public class ThreeAddressCode implements IrCode {
    private SymbolTableEntry r1;
    private SymbolTableEntry r2;
    private SymbolTableEntry r3;

    private IrOperation op;

    public ThreeAddressCode(SymbolTableEntry r1, IrOperation op, SymbolTableEntry r2, SymbolTableEntry r3) {
        this.r1 = r1;
        this.op = op;
        this.r2 = r2;
        this.r3 = r3;
    }

    public IrOperation getOp() {
        return op;
    }

    public SymbolTableEntry getR3() {
        return r3;
    }

    public SymbolTableEntry getR2() {
        return r2;
    }

    public SymbolTableEntry getR1() {
        return r1;
    }

    @Override
    public String toString() {
        return op.name() + ", " + r1.getName() + ", " + r2.getName() + ", " + r3.getName();
    }
}
