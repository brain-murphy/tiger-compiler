package codeGeneration.RegAlloc;

import com.sun.org.apache.xpath.internal.operations.Bool;
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
    private List<BlockBonus> blockBonusList = new ArrayList<BlockBonus>();
    // need a map for bonus block
    private Map<IrCode, BlockBonus> blockBonusMap = new HashMap<>();
    private Map<String, ArrayList<Web>> varToWeb = new HashMap<String, ArrayList<Web>>();

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
                FunctionCallCode funcCode = (FunctionCallCode) currentIR;
                String regName = "$t";
                Object tempObj;
                Symbol destSymbol;
                int regCount = 0;
                if(funcCode.getR1() != null){
                    Symbol oldSymbol = (Symbol) funcCode.getR1();
                    if(!varToReg.containsKey(oldSymbol)){
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
                }
                // iterate through args
                Symbol[] args = funcCode.getArgs();
                int j = 0;
                while(j < args.length){
                    if(varToReg.containsKey(args[j])){
                        j = j + 1;
                        continue;
                    }
                    destSymbol = new Symbol( regName+Integer.toString(regCount) );
                    regCount = regCount + 1;
                    for(Attribute att: Attribute.values()){
                        tempObj = args[j].getAttribute( att );
                        if(tempObj != null){
                            destSymbol.putAttribute(att, tempObj);
                        }
                    }
                    IrCode loadInstruction = new ThreeAddressCode(args[j], IrOperation.LOAD, destSymbol, null);
                    naiveIR.emit(loadInstruction);
                    varToReg.put(args[j], destSymbol);
                    j = j + 1;
                }
                naiveIR.emit(currentIR);
                for(Map.Entry<Symbol, Symbol> entry: varToReg.entrySet()) {
                    IrCode storeInstruction = new ThreeAddressCode(entry.getKey(), IrOperation.STORE, entry.getValue(), null);
                    naiveIR.emit(storeInstruction);
                }
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
                    if(!varToReg.containsKey(oldSymbol)){
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
                }
                if(instructionWithVariables.getR2() != null && (instructionWithVariables.getR2() instanceof Symbol)){
                    Symbol oldSymbol = (Symbol) instructionWithVariables.getR2();
                    if(!varToReg.containsKey(oldSymbol)){
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
                }
                if(instructionWithVariables.getR3() != null && (instructionWithVariables.getR3() instanceof Symbol)){
                    Symbol oldSymbol = (Symbol) instructionWithVariables.getR3();
                    if(!varToReg.containsKey(oldSymbol)){
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

        addLoadStoreToIr(temp);
        System.out.print("\n========NAIVE========\n");
        System.out.print( naiveIR.toString() );
        System.out.print("\n=====================\n");

        genRegAllocBonus(temp);
        debugPrintLiveAnalysis();

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
        print_IRNaive();*/
        /*System.out.print("======================\n");
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
            else if(originalIR.get(i).get(0).charAt(0) == 'B' || Objects.equals(originalIR.get(i).get(0), "RETURN")){
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
    public void genRegAllocBonus(LinearIr ir){
        buildBlocksBonus(ir);
        buildBlocksBonusCFG();
        linkIRinBlocks();
        liveAnalysis();
        constructWeb();
    }
    public void buildBlocksBonus(LinearIr ir){
        Boolean branchNext = false;
        IrCode currentIR;
        BlockBonus newBlock = new BlockBonus();
        int i = 0;
        while(i < ir.getCodeSequence().size()){
            currentIR = ir.getCodeSequence().get(i);
            if(i == 0 || currentIR instanceof Label || branchNext){
                branchNext = false;
                newBlock = new BlockBonus();
                blockBonusList.add(newBlock);
                blockBonusMap.put( currentIR, newBlock );
            }
            if(currentIR instanceof Label){
                IrCodeExtend newIr = new IrCodeExtend(currentIR);
                newBlock.IrList.add( newIr );
                newBlock.tailIR = newIr;
            }
            else if(currentIR instanceof FunctionCallCode){
                // function call
                IrCodeExtend newIr = new IrCodeExtend(currentIR);
                newBlock.IrList.add( newIr );
                newBlock.tailIR = newIr;
                FunctionCallCode funcCall = (FunctionCallCode) currentIR;
                if(funcCall.getR1() != null){
                    // caller
                    newIr.def = (Symbol) funcCall.getR1();
                }
                int j = 0;
                Symbol[] args = funcCall.getArgs();
                while(j < args.length){
                    newIr.use.add( args[j] );
                    newIr.in.put( args[j], true );
                    j = j + 1;
                }
            }
            else{
                // three addr code
                ThreeAddressCode threeAddr = (ThreeAddressCode) currentIR;
                // create newIr
                IrCodeExtend newIr = new IrCodeExtend(currentIR);
                newBlock.IrList.add( newIr );
                newBlock.tailIR = newIr;
                if(threeAddr.getOp() == IrOperation.BREQ
                        || threeAddr.getOp() == IrOperation.BRGEQ
                        || threeAddr.getOp() == IrOperation.BRGT
                        || threeAddr.getOp() == IrOperation.BRLEQ
                        || threeAddr.getOp() == IrOperation.BRLT
                        || threeAddr.getOp() == IrOperation.BRNEQ
                        || threeAddr.getOp() == IrOperation.GOTO
                        || threeAddr.getOp() == IrOperation.RETURN){
                    branchNext = true;
                }
                // r1, r2, r3 could be symbol and label, ignore label
                if(threeAddr.getR1() != null && !(threeAddr.getR1() instanceof Label)){
                    newIr.def = (Symbol)threeAddr.getR1();
                }
                if(threeAddr.getR2() != null){
                    Symbol tempSymbol = (Symbol) threeAddr.getR2();
                    if( tempSymbol.getAttribute(Attribute.IS_LITERAL) == null ) {
                        newIr.use.add((Symbol) threeAddr.getR2());
                        newIr.in.put((Symbol) threeAddr.getR2(), true);
                    }
                }
                if(threeAddr.getR3() != null){
                    Symbol tempSymbol = (Symbol) threeAddr.getR3();
                    if( tempSymbol.getAttribute(Attribute.IS_LITERAL) == null ) {
                        newIr.use.add((Symbol) threeAddr.getR3());
                        newIr.in.put((Symbol) threeAddr.getR3(), true);
                    }
                }
            }
            i = i + 1;
        }
    }
    public void buildBlocksBonusCFG(){
        // populated the prev and next list in blockBonus obj
        int i = 0;
        IrCode tailIR;
        BlockBonus currentBlock;
        BlockBonus targetBlock;
        while(i < blockBonusList.size()){
            currentBlock = blockBonusList.get(i);
            tailIR = currentBlock.tailIR.originalIR;
            if(tailIR instanceof Label || tailIR instanceof FunctionCallCode){
                if(i != blockBonusList.size()-1) {
                    blockBonusList.get(i + 1).prev.add(currentBlock);
                    currentBlock.next.add(blockBonusList.get(i + 1));
                }
            }
            else if(tailIR instanceof ThreeAddressCode){
                ThreeAddressCode threeAddr = (ThreeAddressCode) tailIR;
                if(threeAddr.getOp() == IrOperation.BREQ
                        || threeAddr.getOp() == IrOperation.BRGEQ
                        || threeAddr.getOp() == IrOperation.BRGT
                        || threeAddr.getOp() == IrOperation.BRLEQ
                        || threeAddr.getOp() == IrOperation.BRLT
                        || threeAddr.getOp() == IrOperation.BRNEQ){
                    // need to determine it's next block
                    targetBlock = blockBonusMap.get( threeAddr.getR1() );
                    targetBlock.prev.add( currentBlock );
                    currentBlock.next.add( targetBlock );
                    // flow down
                    if(i != blockBonusList.size()-1){
                        blockBonusList.get(i+1).prev.add( currentBlock );
                        currentBlock.next.add( blockBonusList.get(i+1) );
                    }
                }
                /*else if(threeAddr.getOp() == IrOperation.GOTO || threeAddr.getOp() == IrOperation.RETURN){
                    targetBlock = blockBonusMap.get( threeAddr.getR1() );
                    targetBlock.prev.add( currentBlock );
                    currentBlock.next.add( targetBlock );
                }*/
                else{
                    // normal IR, just flow down
                    if(i != blockBonusList.size()-1){
                        blockBonusList.get(i+1).prev.add( currentBlock );
                        currentBlock.next.add( blockBonusList.get(i+1) );
                    }
                }
            }
            else {
                System.out.print("What is the type of IR??\n");
            }
            i = i + 1;
        }
    }
    public void linkIRinBlocks(){
        int i = 0;
        int j, k;
        BlockBonus currentBlk, prevBlk, nextBlk;
        while(i < blockBonusList.size()){
            currentBlk = blockBonusList.get(i);
            j = 0;
            while(j < currentBlk.IrList.size()){
                if(j != 0){
                    currentBlk.IrList.get(j).prevIR.add( currentBlk.IrList.get(j-1) );
                }
                else{
                    // look for prevBlock
                    k = 0;
                    while(k < currentBlk.prev.size()){
                        prevBlk = currentBlk.prev.get(k);
                        currentBlk.IrList.get(j).prevIR.add( prevBlk.tailIR );
                        k = k + 1;
                    }
                }
                if(j != currentBlk.IrList.size()-1){
                    currentBlk.IrList.get(j).nextIR.add( currentBlk.IrList.get(j+1) );
                }
                else{
                    // look for next block
                    k = 0;
                    while(k < currentBlk.next.size()){
                        nextBlk = currentBlk.next.get(k);
                        currentBlk.IrList.get(j).prevIR.add( nextBlk.IrList.get(0) );
                        k = k + 1;
                    }
                }
                j = j + 1;
            }
            i = i + 1;
        }
    }

    public void liveAnalysis(){
        Boolean changeFlag = true;
        BlockBonus currentBlk;
        IrCodeExtend currentIR, nextIR, prevIR;
        int i, j, k, sizeBefore;
        while(changeFlag){
            changeFlag = false;
            // update block first, using CFG and traversed map, and a queue
            // put blockBonusList.get(0) in the queue
            Map<BlockBonus, Boolean> traversedBlk = new HashMap<>();
            Queue<BlockBonus> processQ = new LinkedList<>();
            processQ.add( blockBonusList.get(0) );
            traversedBlk.put( blockBonusList.get(0), true );
            while(!processQ.isEmpty()){
                currentBlk = processQ.remove();
                // add successor's in to currentBlk.tailIR.out
                i = 0;
                sizeBefore = currentBlk.tailIR.out.size();
                currentBlk.tailIR.out.clear();
                while(i < currentBlk.next.size()){
                    nextIR = currentBlk.next.get(i).IrList.get(0);
                    for(Map.Entry<Symbol, Boolean> entry: nextIR.in.entrySet()){
                        currentBlk.tailIR.out.put(entry.getKey(), true);
                    }
                    if(!traversedBlk.containsKey( currentBlk.next.get(i) )){
                        // put untraversed blocks into the processing qqqqqqqqqqq
                        processQ.add( currentBlk.next.get(i) );
                        // the case when a block cannot be reached by any other blocks is not considered here
                    }
                    i = i + 1;
                }
                if( currentBlk.tailIR.out.size() != sizeBefore )
                    changeFlag = true;
            }
            // update instruction in each block, from the bottom of block
            // need to be careful about checking whether the block is unchanged
            i = 0;
            while(i < blockBonusList.size()){
                j = blockBonusList.get(i).IrList.size()-1;
                while( j >= 0 ){
                    currentIR = blockBonusList.get(i).IrList.get(j);
                    sizeBefore = currentIR.in.size();
                    currentIR.in.clear();
                    for(Map.Entry<Symbol, Boolean> entry: currentIR.out.entrySet()){
                        currentIR.in.put( entry.getKey(), true );
                    }
                    currentIR.in.remove( currentIR.def );
                    k = 0;
                    while (k < currentIR.use.size()){
                        currentIR.in.put( currentIR.use.get(k), true );
                        k = k + 1;
                    }
                    if(currentIR.in.size() != sizeBefore)
                        changeFlag = true;
                    if(j != 0){
                        // out[i-1] = in[i]
                        prevIR = blockBonusList.get(i).IrList.get(j-1);
                        sizeBefore = prevIR.out.size();
                        for(Map.Entry<Symbol, Boolean> entry: currentIR.in.entrySet()){
                            prevIR.out.put( entry.getKey(), true );
                        }
                        if(prevIR.out.size() != sizeBefore)
                            changeFlag = true;
                    }
                    j = j - 1;
                }
                i = i + 1;
            }
        }
    }
    public void debugPrintLiveAnalysis(){
        int i = 0, j;
        IrCodeExtend currentIR;
        while(i < blockBonusList.size()){
            j = 0;
            while(j < blockBonusList.get(i).IrList.size()){
                currentIR = blockBonusList.get(i).IrList.get(j);
                System.out.print( "\n\n" + currentIR.originalIR.toString() + "\n" );
                System.out.print("IN: ");
                for(Map.Entry<Symbol, Boolean> entry: currentIR.in.entrySet()){
                    System.out.print( entry.getKey().getName() + ", ");
                }
                System.out.print("\nOUT: ");
                for(Map.Entry<Symbol, Boolean> entry: currentIR.out.entrySet()){
                    System.out.print( entry.getKey().getName() + ", ");
                }
                j = j + 1;
            }
            i = i + 1;
        }
    }
    public boolean IRinWeb(String varName, IrCodeExtend tempIR){
        if(varToWeb.containsKey( varName )){
            int i = 0;
            ArrayList<Web> webList = varToWeb.get( varName );
            while(i < webList.size()){
                if( webList.get(i).irIncluded.containsKey(tempIR) )
                    return true;
                i = i + 1;
            }
            return false;
        }
        else{
            return false;
        }
    }
    public void spanWeb(Web newWeb, IrCodeExtend startIR){
        IrCodeExtend currentIR, prevIR, nextIR;
        int i;
        Queue<IrCodeExtend> processQ = new LinkedList<>();
        processQ.add(startIR);
        newWeb.irIncluded.put(startIR, true);
        while(!processQ.isEmpty()){
            currentIR = processQ.remove();
            // traverse prev IR
            i = 0;
            while (i < currentIR.prevIR.size()){
                prevIR = currentIR.prevIR.get(i);
                if(prevIR.out.containsKey( newWeb.originalSymbol ) && !newWeb.irIncluded.containsKey( prevIR )){
                    newWeb.irIncluded.put( prevIR,true );
                    processQ.add( prevIR );
                }
                i = i + 1;
            }
            // traverse next IR
            i = 0;
            while(i < currentIR.nextIR.size()){
                nextIR = currentIR.nextIR.get(i);
                if(nextIR.in.containsKey( newWeb.originalSymbol ) && !newWeb.irIncluded.containsKey( nextIR )){
                    newWeb.irIncluded.put( nextIR,true );
                    processQ.add( nextIR );
                }
                i = i + 1;
            }
        }
    }
    public void constructWeb(){
        int i = 0;
        int j;
        BlockBonus currentBlk;
        IrCodeExtend currentIR;
        Web newWeb;
        Symbol tempSymbol;
        while(i < blockBonusList.size()){
            currentBlk = blockBonusList.get(i);
            j = 0;
            while(j < currentBlk.IrList.size()){
                currentIR = currentBlk.IrList.get(j);
                tempSymbol = currentIR.symbolStart();
                if(tempSymbol != null){
                    // tempSymbol starts here
                    if( IRinWeb( tempSymbol.getName(), currentIR )){
                        j = j + 1;
                        continue;
                    }
                    else{
                        newWeb = new Web( tempSymbol );
                        // populate that web here
                        spanWeb( newWeb, currentIR );
                        if(varToWeb.containsKey( tempSymbol.getName() ))
                            varToWeb.get( tempSymbol.getName() ).add( newWeb );
                        else {
                            ArrayList<Web> webList = new ArrayList<>();
                            webList.add( newWeb );
                            varToWeb.put( tempSymbol.getName(), webList );
                        }
                    }
                }
                j = j + 1;
            }
            i = i + 1;
        }
    }
}
