package codeGeneration.RegAlloc;

import parser.semantic.ir.IrCode;
import parser.semantic.symboltable.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fishlinghu on 2016/12/7.
 */
public class Web {
    public Symbol originalSymbol;
    public Symbol regSymbol;
    public Map<IrCodeExtend, Boolean> irIncluded = new HashMap<>(); // spill cost = irIncluded.size()
    public List<IrCodeExtend> startIRList = new ArrayList<>();
    public List<IrCodeExtend> endIRList = new ArrayList<>();
    public List<Web> neighbor = new ArrayList<>();
    public Web(Symbol s){
        //irIncluded.put(startIR, true);
        regSymbol = null;
        originalSymbol = s;
    }
}
