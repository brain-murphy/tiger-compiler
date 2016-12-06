package parser.semantic.ir;

import parser.semantic.symboltable.Symbol;

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

    public Symbol getFunctionSymbol() {
        return function;
    }

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
