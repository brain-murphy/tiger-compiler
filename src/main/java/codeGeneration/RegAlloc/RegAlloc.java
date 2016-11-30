package codeGeneration.RegAlloc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fishlinghu on 2016/11/30.
 */
public class RegAlloc {
    private class regSet{
        public String dst = "";
        public String op1 = "";
        public String op2 = "";
    }

    private List<List<String>> orignalIR = new ArrayList<List<String>>();
    private List<List<String>> outputIRNaive = new ArrayList<List<String>>();

    public void inputIR() {
        // parse in the input IR and store in the DS here
    }
    public void genRegAllocNaive(){
        // naive method for generating reg allocation code
    }
}
