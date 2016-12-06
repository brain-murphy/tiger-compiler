package parser.semantic.ir;

import parser.semantic.symboltable.Symbol;
import parser.semantic.symboltable.SymbolTableEntry;

public class FunctionCallCode implements IrCode {
    private Symbol r1;
    private IrOperation op;
    private Symbol function;
    private Symbol[] args;

    public FunctionCallCode(Symbol r1, IrOperation op, Symbol function, Symbol... args) {
        this.r1 = r1;
        this.op = op;
        this.function = function;
        this.args = args;
    }

    public FunctionCallCode(IrOperation op, Symbol function, Symbol... args) {
        this(null, op, function, args);
    }

    public IrOperation getOp() {
        return op;
    }

    public SymbolTableEntry getR1() {
        return r1;
    }

    public Symbol[] getArgs() { return args; }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(op.name());

        if (op == IrOperation.CALLR) {
            stringBuilder
                    .append(", ")
                    .append(r1.getName());
        }

        stringBuilder
                .append(", ")
                .append(function.getName());

        for (Symbol arg : args) {
            stringBuilder
                    .append(", ")
                    .append(arg.getName());
        }
        return stringBuilder.toString();
    }
}
