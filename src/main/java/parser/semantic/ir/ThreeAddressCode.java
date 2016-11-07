package parser.semantic.ir;

import parser.semantic.symboltable.Symbol;

public class ThreeAddressCode implements IrCode {
    private Symbol r1;
    private Symbol r2;
    private Symbol r3;

    private IrOperation op;

    public ThreeAddressCode(Symbol r1, IrOperation op, Symbol r2, Symbol r3) {
        this.r1 = r1;
        this.op = op;
        this.r2 = r2;
        this.r3 = r3;
    }

    public IrOperation getOp() {
        return op;
    }

    public Symbol getR3() {
        return r3;
    }

    public Symbol getR2() {
        return r2;
    }

    public Symbol getR1() {
        return r1;
    }
}
