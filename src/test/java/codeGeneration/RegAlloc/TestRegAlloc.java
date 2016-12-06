package codeGeneration.RegAlloc;

import org.junit.Test;

/**
 * Created by Brian on 12/6/2016.
 */
public class TestRegAlloc {

    @Test
    public void testNaiveRegisterAllocation() {
        RegAlloc regAlloc = new RegAlloc();

        regAlloc.doNaiveRegisterAllocation();
    }
}
