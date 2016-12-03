package codeGeneration.RegAlloc;

import parser.ParseCoordinator;
import parser.semantic.ir.LinearIr;
import scanner.DirectScanner;
import scanner.Scanner;
import util.Reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
        while(i < listOfIR.size()) {
            originalIR.add(new ArrayList<String>(Arrays.asList(listOfIR.get(i).split(", "))));
            //System.out.print(" ---");
            //System.out.print( originalIR.get(i).get(0) );
            //System.out.print("\n");
            i = i + 1;
        }
        genRegAllocNaive();
        print_IRNaive();
    }
    public void genRegAllocNaive(){
        // naive method for generating reg allocation code
        int i = 0;
        int j, k;
        int inst_size;
        boolean callFlag, callrFlag, branchFlag;
        String tempStr, opStr;

        while(i < originalIR.size()){
            // Count the total number of registers we need for this line
            List<String> originalVar = new ArrayList<String>();
            inst_size = originalIR.get(i).size();
            opStr = originalIR.get(i).get(0);
            callrFlag = false;
            callFlag = false;
            branchFlag = false;
            j = 1;// originalIR.get(0) is the operation string
            if(Objects.equals(opStr, "goto")) {
                // skip the while loop
                tempStr = originalIR.get(i).get(0) + ", " + originalIR.get(i).get(1);
                outputIRNaive.add(tempStr);
                continue;
            }
            else if(Objects.equals(opStr, "call")){
                callFlag = true;
                j = 2; // skip the first 2 strings
            }
            else if(Objects.equals(opStr, "callr")){
                // ignore the third string
                callrFlag = true;
            }
            else if(opStr.charAt(0) == 'b'){
                // branch instruction, ignore the last one
                branchFlag = true;
                inst_size = inst_size - 1;
            }
            while(j < inst_size) {
                if (callrFlag == true && j == 2)
                    j = j + 1;
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
                if(callFlag && j == 1){
                    tempStr = tempStr + ", " + originalIR.get(i).get(j);
                }
                else if(callrFlag && j == 2){
                    tempStr = tempStr + ", " + originalIR.get(i).get(j);
                }
                else if(branchFlag && j == originalIR.get(i).size()-1){
                    tempStr = tempStr + ", " + originalIR.get(i).get(j);
                }
                else if(!isNumeric( originalIR.get(i).get(j) )){
                    tempStr = tempStr + ", $t" + k;
                    k = k + 1;
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
    public void print_IRNaive(){
        int i = 0;
        System.out.print( outputIRNaive.size() );
        while(i < outputIRNaive.size()){
            System.out.print( outputIRNaive.get(i) );
            System.out.print("\n");
            i = i + 1;
        }
    }
    public void genRegAllocCFG(){

    }
}
