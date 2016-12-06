package codeGeneration.instructionSelection

import codeGeneration.instructionSelection.mips.MipsFormatter
import parser.semantic.ir.FunctionCallCode
import parser.semantic.ir.LinearIr
import parser.semantic.ir.ThreeAddressCode
import parser.semantic.symboltable.SymbolTable


class InstructionSelector(private val ir: LinearIr, private val symbolTable: SymbolTable) {

    private val formatter = MipsFormatter()

    /*fun run() {
        val usedFunctionSymbols = ir.filterIsInstance<FunctionCallCode>()
                .map { it.functionSymbol }
                .distinctBy { it.name }

        symbolTable.getChildScope()

    }*/
}
