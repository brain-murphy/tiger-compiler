package codeGeneration.RegAlloc;

import parser.semantic.ir.IrCode;
import parser.semantic.symboltable.Symbol;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fishlinghu on 2016/12/7.
 */
public class Web {
    public Symbol originalSymbol;
    public Map<IrCodeExtend, Boolean> irIncluded = new HashMap<>();
    public Web(Symbol s){
        //irIncluded.put(startIR, true);
        originalSymbol = s;
    }
}
