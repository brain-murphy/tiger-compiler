package codeGeneration.RegAlloc;

import com.sun.org.apache.xpath.internal.operations.Bool;
import parser.semantic.ir.IrCode;
import parser.semantic.symboltable.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fishlinghu on 2016/12/6.
 */
public class IrCodeExtend {
    public Map<Symbol, Boolean> in = new HashMap<>();
    public Map<Symbol, Boolean> out = new HashMap<>();
    //public List<Symbol> def = new ArrayList<>();
    public Symbol def;
    public List<Symbol> use = new ArrayList<>();
    public IrCode originalIR;
    public List<IrCodeExtend> prevIR = new ArrayList<>();
    public List<IrCodeExtend> nextIR = new ArrayList<>();

    public IrCodeExtend(IrCode a){
        def = null;
        originalIR = a;
    }
    public Symbol symbolStart(){
        if(def == null)
            return null;
        else{
            if(!in.containsKey(def) && out.containsKey(def))
                return def;
            else
                return null;
        }
    }
}
