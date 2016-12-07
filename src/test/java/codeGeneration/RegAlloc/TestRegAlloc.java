package codeGeneration.RegAlloc;

import codeGeneration.instructionSelection.InstructionSelector;
import org.junit.Test;
import parser.semantic.ir.LinearIr;

/**
 * Created by Brian on 12/6/2016.
 */
public class TestRegAlloc {

    @Test
    public void testNaiveRegisterAllocation() {
        RegAlloc regAlloc = new RegAlloc();

        LinearIr registerAllocatedIr = regAlloc.doNaiveRegisterAllocation();

        InstructionSelector instructionSelector = new InstructionSelector(registerAllocatedIr);

        System.out.println("<<<<<<<<<<<<final program>>>>>>>>>>>>");
        System.out.println(instructionSelector.run());
    }
}
