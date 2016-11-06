package parser.symantic.ir;

import java.util.*;
import java.util.function.Consumer;

public class LinearIr implements Iterable<ThreeAddressCode> {
    private List<ThreeAddressCode> codeSequence;

    public LinearIr() {
        codeSequence = new LinkedList<>();
    }

    public void append(ThreeAddressCode... instructions) {
        Collections.addAll(codeSequence, instructions);
    }

    @Override
    public Iterator<ThreeAddressCode> iterator() {
        return codeSequence.iterator();
    }

    @Override
    public void forEach(Consumer<? super ThreeAddressCode> action) {
        codeSequence.forEach(action);
    }

    @Override
    public Spliterator<ThreeAddressCode> spliterator() {
        return codeSequence.spliterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (ThreeAddressCode instruction : codeSequence) {
            builder.append(instruction)
                    .append("\n");
        }

        return builder.toString().trim();
    }
}
