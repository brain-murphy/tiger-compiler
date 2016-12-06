package codeGeneration.RegAlloc;

import parser.ParseCoordinator;
import parser.semantic.ir.LinearIr;
import scanner.DirectScanner;
import scanner.Scanner;
import util.Reader;

import java.util.*;

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
    private List<Block> blockList = new ArrayList<Block>();

    public boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    @org.junit.Test
    public void inputIR() {
        // parse in the input IR and store in the DS here
        Reader reader = new Reader();
        String programText = reader.readFromFile("./examples/test5.tiger");

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
        System.out.print("======================\n");
        System.out.print("CFG coloring: \n");
        System.out.print("======================\n");
        genRegAllocCFG();
    }

    public void genRegAllocNaive(){
        int i = 0, j;
        int regCount;
        String opStr, tempStr;
        boolean callFlag, callrFlag, branchFlag;
        while(i < originalIR.size()){
            Map<String, String> regMap = new HashMap<String, String>();
            opStr = originalIR.get(i).get(0);
            j = 1;
            regCount = 0;
            callrFlag = false;
            if(Objects.equals(opStr, "GOTO")) {
                // skip the while loop
                tempStr = originalIR.get(i).get(0) + ", " + originalIR.get(i).get(1);
                outputIRNaive.add(tempStr);
                i = i + 1;
                continue;
            }
            else if(Objects.equals(opStr, "CALL")){
                callFlag = true;
                j = 2; // skip the first 2 strings
            }
            else if(Objects.equals(opStr, "CALLR")){
                // ignore the third string
                callrFlag = true;
            }
            else if(opStr.charAt(0) == 'B'){
                // branch instruction, ignore the last one
                branchFlag = true;
                j = 2;
            }
            while(j < originalIR.get(i).size()){
                if(callrFlag && j == 2)
                    j = j + 1;
                tempStr = originalIR.get(i).get(j);
                if(!isNumeric(tempStr)){
                    if(!regMap.containsKey( tempStr )){
                        regMap.put(tempStr, "$t"+regCount);
                        regCount = regCount + 1;
                    }
                }
                j = j + 1;
            }
            // output load instruction
            for(Map.Entry<String, String> entry: regMap.entrySet()){
                tempStr = "LOAD, " + entry.getValue() + ", " + entry.getKey();
                outputIRNaive.add( tempStr );
            }
            // output original IR
            j = 1;
            String symbol;
            tempStr = originalIR.get(i).get(0);
            while(j < originalIR.get(i).size()){
                symbol = originalIR.get(i).get(j);
                if(!regMap.containsKey( symbol )){
                    tempStr = tempStr + ", " + symbol;
                }
                else{
                    tempStr = tempStr + ", " + regMap.get( symbol );
                }
                j = j + 1;
            }
            outputIRNaive.add( tempStr );
            // output store inst
            for(Map.Entry<String, String> entry: regMap.entrySet()){
                tempStr = "STORE, " + entry.getValue() + ", " + entry.getKey();
                outputIRNaive.add( tempStr );
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
    public void buildBlocks(){
        int i = 0;
        Block newBlock = new Block();
        boolean afterBranch = false;
        while(i < originalIR.size()){
            if(i == 0 || afterBranch){
                // new block
                afterBranch = false;
                newBlock = new Block();
                blockList.add(newBlock);
            }
            else if(originalIR.get(i).size() == 1 && !Objects.equals(originalIR.get(i).get(0), "_main:")){
                // next line MIGHT be the beginning of a new block
                // if it is the target of any branch
                newBlock = new Block();
                blockList.add(newBlock);
            }
            else if(originalIR.get(i).get(0).charAt(0) == 'B'){
                afterBranch = true;
            }
            // populate the line into block
            List<String> tempArr = new ArrayList<String>( originalIR.get(i) );
            newBlock.oldIR.add( tempArr );
            i = i + 1;
        }
        i = 0;
        while(i < blockList.size()){
            blockList.get(i).generateNode();
            blockList.get(i).buildCFG();
            blockList.get(i).doColoring();
            blockList.get(i).printNode();
            i = i + 1;
        }
    }

    public void genRegAllocCFG(){
        buildBlocks();
    }
}
