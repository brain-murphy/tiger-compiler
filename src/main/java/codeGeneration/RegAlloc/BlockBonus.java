package codeGeneration.RegAlloc;

import parser.semantic.ir.IrCode;
import parser.semantic.symboltable.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fishlinghu on 2016/12/6.
 */
public class BlockBonus {
    public List<IrCodeExtend> IrList = new ArrayList<>();
    public IrCodeExtend tailIR;
    public List<BlockBonus> next = new ArrayList<>();
    public List<BlockBonus> prev = new ArrayList<>();
    //public BlockBonus nextBlock;
}
