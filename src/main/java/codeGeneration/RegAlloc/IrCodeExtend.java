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
    public List<Symbol> def = new ArrayList<>();
    public List<Symbol> use = new ArrayList<>();
    public IrCode originalIR;

    public IrCodeExtend(IrCode a){
        originalIR = a;
    }
}
