package codeGeneration.RegAlloc;

import parser.semantic.ir.IrCode;
import parser.semantic.symboltable.Symbol;

import java.util.*;

/**
 * Created by fishlinghu on 2016/12/7.
 */
public class Web {
    public Symbol originalSymbol;
    public Symbol regSymbol;
    public Map<IrCodeExtend, Boolean> irIncluded = new HashMap<>(); // spill cost = irIncluded.size()
    public Map<IrCode, Boolean> originalIRIncluded = new HashMap<>();
    public List<IrCodeExtend> startIRList = new ArrayList<>();
    public List<IrCodeExtend> endIRList = new ArrayList<>();
    public List<Web> neighbor = new ArrayList<>();
    public Web(Symbol s){
        //irIncluded.put(startIR, true);
        regSymbol = null;
        originalSymbol = s;
    }
    public boolean contain(Symbol s, IrCode tempIR){
        if(Objects.equals( s.getName(), originalSymbol.getName() ) && originalIRIncluded.containsKey( tempIR ))
            return true;
        else
            return false;
    }
}
