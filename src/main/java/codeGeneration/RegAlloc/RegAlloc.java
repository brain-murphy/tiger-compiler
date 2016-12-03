package codeGeneration.RegAlloc;

import parser.ParseCoordinator;
import parser.semantic.ir.LinearIr;
import scanner.DirectScanner;
import scanner.Scanner;
import util.Reader;

import java.util.ArrayList;
import java.util.Arrays;
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

    private List<List<String>> originalIR = new ArrayList<List<String>>();
    //private List<List<String>> outputIRNaive = new ArrayList<List<String>>();
    List<String> outputIRNaive = new ArrayList<String>();

    public boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    @org.junit.Test
    public void inputIR() {
        // parse in the input IR and store in the DS here
        Reader reader = new Reader();
        String programText = reader.readFromFile("./examples/test4.tiger");

        Scanner scanner = new DirectScanner(programText);

        ParseCoordinator parseCoordinator = new ParseCoordinator(scanner);

        LinearIr temp = parseCoordinator.getIr();
        String oldIR = temp.toString();

        System.out.print("=====================\n");
        System.out.print( oldIR );
        System.out.print("\n=====================\n");

        String[] tempArr = oldIR.split("\n");
        ArrayList<String> listOfIR = new ArrayList<String>(Arrays.asList(tempArr)); // Split IR line by line

        int i = 0;
        while(i < listOfIR.size()){
            originalIR.add( new ArrayList<String>( Arrays.asList(listOfIR.get(i).split(", ")) ) );
            System.out.print(" ---");
            System.out.print( originalIR.get(i).get(0) );
            System.out.print("\n");
            i = i + 1;
        }
    }
    public void genRegAllocNaive(){
        // naive method for generating reg allocation code
        int i = 0;
        int j, k;
        String tempStr;
        List<String> originalVar = new ArrayList<String>();
        while(i < originalIR.size()){
            // Count the total number of registers we need for this line
            j = 1; // originalIR.get(0) is the operation string
            while(j < originalIR.get(i).size()) {
                tempStr = originalIR.get(i).get(j);
                if(!isNumeric( tempStr )){
                    // should consider the case b__, call, callr, goto,
                    originalVar.add( tempStr );
                    }
                j = j + 1;
            }
            // Add corresponding number of load IR to outputIRNaive
            j = 0;
            while(j < originalVar.size()){
                tempStr = "LOAD, $t" + j + ", " + originalVar.get(j);
                outputIRNaive.add( tempStr );
                j = j + 1;
            }
            // Add the original line to outputIRNaive, remember to use the register name
            k = 0;
            j = 1;
            tempStr = originalIR.get(i).get(0);
            while(j < originalIR.get(i).size()){
                if(!isNumeric( originalIR.get(i).get(j) ){
                    tempStr = tempStr + ", $t" + k;
                    k = k + 1
                }
                else{
                    tempStr = tempStr + ", " + originalIR.get(i).get(j);
                }
                j = j + 1;
            }
            outputIRNaive.add( tempStr );
            // Add corresponding number of store IR to outputIRNaive
            j = 0;
            while(j < originalVar.size()){
                tempStr = "STORE, $t" + j + ", " + originalVar.get(j);
                outputIRNaive.add( tempStr );
                j = j + 1;
            }
            i = i + 1;
        }
    }
    public void genRegAllocCFG(){

    }
}
