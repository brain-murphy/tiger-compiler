package codeGeneration.RegAlloc;

import parser.ParseCoordinator;
import parser.semantic.ir.*;
import parser.semantic.symboltable.Attribute;
import parser.semantic.symboltable.Symbol;
import parser.semantic.symboltable.SymbolTableEntry;
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
    LinearIr naiveIR = new LinearIr();
    private List<Block> blockList = new ArrayList<Block>();

    public boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public LinearIr doNaiveRegisterAllocation() {
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

        addLoadStoreToIr(temp);
        System.out.print("\n========NAIVE========\n");
        System.out.print( naiveIR.toString() );
        System.out.print("\n=====================\n");

        return naiveIR;
    }

    private void addLoadStoreToIr(LinearIr ir) {

        IrCode currentIR;
        int i = 0;
        while(i < ir.getCodeSequence().size()){
            currentIR = ir.getCodeSequence().get(i);
            Map<Symbol, Symbol> varToReg = new HashMap<>();
            if (currentIR instanceof Label) {
                // re emit label
                naiveIR.emit(currentIR);
            } else if (currentIR instanceof FunctionCallCode) {
                // do something
                FunctionCallCode funcCode = (FunctionCallCode) currentIR;
                String regName = "$t";
                Object tempObj;
                Symbol destSymbol;
                int regCount = 0;
                if(funcCode.getR1() != null){
                    Symbol oldSymbol = (Symbol) funcCode.getR1();
                    destSymbol = new Symbol( regName+Integer.toString(regCount) );
                    regCount = regCount + 1;
                    // copy the attribute
                    for(Attribute att: Attribute.values()){
                        tempObj = oldSymbol.getAttribute( att );
                        if(tempObj != null){
                            destSymbol.putAttribute(att, tempObj);
                        }
                    }
                    IrCode loadInstruction = new ThreeAddressCode(funcCode.getR1(), IrOperation.LOAD, destSymbol, null);
                    naiveIR.emit(loadInstruction);
                    varToReg.put(oldSymbol, destSymbol);
                }
                // iterate through args
                Symbol[] args = funcCode.getArgs();
                int j = 0;
                while(j < args.length){
                    destSymbol = new Symbol( regName+Integer.toString(regCount) );
                    regCount = regCount + 1;
                    for(Attribute att: Attribute.values()){
                        tempObj = args[j].getAttribute( att );
                        if(tempObj != null){
                            destSymbol.putAttribute(att, tempObj);
                        }
                    }
                    IrCode loadInstruction = new ThreeAddressCode(funcCode.getR1(), IrOperation.LOAD, destSymbol, null);
                    naiveIR.emit(loadInstruction);
                    varToReg.put(args[j], destSymbol);
                    j = j + 1;
                }
                naiveIR.emit(currentIR);
            } else if (currentIR instanceof ThreeAddressCode) {
                ThreeAddressCode instructionWithVariables = (ThreeAddressCode) currentIR;
                Symbol exampleDestinationSymbol = null;
                Symbol destSymbol;
                String regName = "$t";
                Object tempObj;
                int regCount = 0;
                // do some register allocation actions
                if(instructionWithVariables.getR1() != null && (instructionWithVariables.getR1() instanceof Symbol)){
                    Symbol oldSymbol = (Symbol) instructionWithVariables.getR1();
                    destSymbol = new Symbol( regName+Integer.toString(regCount) );
                    regCount = regCount + 1;
                    // copy the attribute
                    for(Attribute att: Attribute.values()){
                        tempObj = oldSymbol.getAttribute( att );
                        if(tempObj != null){
                            destSymbol.putAttribute(att, tempObj);
                        }
                    }
                    IrCode loadInstruction = new ThreeAddressCode(instructionWithVariables.getR1(), IrOperation.LOAD, destSymbol, null);
                    naiveIR.emit(loadInstruction);
                    varToReg.put(oldSymbol, destSymbol);
                }
                if(instructionWithVariables.getR2() != null && (instructionWithVariables.getR2() instanceof Symbol)){
                    Symbol oldSymbol = (Symbol) instructionWithVariables.getR2();
                    destSymbol = new Symbol( regName+Integer.toString(regCount) );
                    regCount = regCount + 1;
                    // copy the attribute
                    for(Attribute att: Attribute.values()){
                        tempObj = oldSymbol.getAttribute( att );
                        if(tempObj != null){
                            destSymbol.putAttribute(att, tempObj);
                        }
                    }
                    IrCode loadInstruction = new ThreeAddressCode(instructionWithVariables.getR2(), IrOperation.LOAD, destSymbol, null);
                    naiveIR.emit(loadInstruction);
                    varToReg.put(oldSymbol, destSymbol);
                }
                if(instructionWithVariables.getR3() != null && (instructionWithVariables.getR3() instanceof Symbol)){
                    Symbol oldSymbol = (Symbol) instructionWithVariables.getR3();
                    destSymbol = new Symbol( regName+Integer.toString(regCount) );
                    regCount = regCount + 1;
                    // copy the attribute
                    for(Attribute att: Attribute.values()){
                        tempObj = oldSymbol.getAttribute( att );
                        if(tempObj != null){
                            destSymbol.putAttribute(att, tempObj);
                        }
                    }
                    IrCode loadInstruction = new ThreeAddressCode(instructionWithVariables.getR3(), IrOperation.LOAD, destSymbol, null);
                    naiveIR.emit(loadInstruction);
                    varToReg.put(oldSymbol, destSymbol);
                }
                naiveIR.emit( currentIR );
                // emit store
                for(Map.Entry<Symbol, Symbol> entry: varToReg.entrySet()){
                    IrCode storeInstruction = new ThreeAddressCode(entry.getKey(), IrOperation.STORE, entry.getValue(), null);
                    naiveIR.emit( storeInstruction );
                }
            }
            i = i + 1;
        }

    }

//    @org.junit.Test
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

        addLoadStoreToIr(temp);
        System.out.print("\n========NAIVE========\n");
        System.out.print( naiveIR.toString() );
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
        /*genRegAllocNaive();
        print_IRNaive();
        System.out.print("======================\n");
        System.out.print("CFG coloring: \n");
        System.out.print("======================\n");
        genRegAllocCFG();*/
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
