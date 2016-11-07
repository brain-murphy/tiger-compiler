package parser.semantic.ir;

import java.util.*;
import java.util.function.Consumer;

public class LinearIr implements Iterable<IrCode> {
    private List<IrCode> codeSequence;

    public LinearIr() {
        codeSequence = new LinkedList<>();
    }

    public void append(IrCode... instructions) {
        Collections.addAll(codeSequence, instructions);
    }

    @Override
    public Iterator<IrCode> iterator() {
        return codeSequence.iterator();
    }

    @Override
    public void forEach(Consumer<? super IrCode> action) {
        codeSequence.forEach(action);
    }

    @Override
    public Spliterator<IrCode> spliterator() {
        return codeSequence.spliterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (IrCode codeLine : codeSequence) {
            builder.append(codeLine)
                    .append("\n");
        }

        return builder.toString().trim();
    }
}
