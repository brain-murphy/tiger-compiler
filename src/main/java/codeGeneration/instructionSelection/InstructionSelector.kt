package codeGeneration.instructionSelection

import codeGeneration.instructionSelection.mips.MipsFormatter
import codeGeneration.instructionSelection.mips.createAssemblerVariable
import parser.semantic.ir.*
import parser.semantic.symboltable.Attribute
import parser.semantic.symboltable.Symbol
import parser.semantic.symboltable.SymbolTable


class InstructionSelector(private val ir: LinearIr, private val symbolTable: SymbolTable) {

    private val formatter = MipsFormatter()

    fun run() {
        makeDataSegment()

        makeTextSegment()
    }

    private fun makeTextSegment() {

        val functionSegments = getFunctionInstructionLists()

        for (instructionList in functionSegments) {

        }
    }

    private fun getFunctionInstructionLists(): MutableList<List<IrCode>> {
        val functionSegments = mutableListOf<List<IrCode>>()

        var currentFunctionCodes = mutableListOf<IrCode>()

        ir.forEach {
            if (it is Label && it.name.startsWith("_")) {
                functionSegments.add(currentFunctionCodes)
                currentFunctionCodes = mutableListOf()
            }

            currentFunctionCodes.add(it)
        }

        functionSegments.removeAt(0)
        return functionSegments
    }

    private fun makeDataSegment() {
        val irIterator = ir.iterator()

        var currentInstruction = irIterator.next()

        while (currentInstruction is ThreeAddressCode) {
            assertIsAssignmentInstruction(currentInstruction)

            val variableSymbol = currentInstruction.r1 as Symbol
            val variableType = variableSymbol.getAttribute(Attribute.TYPE) as ExpressionType
            val variableName = variableSymbol.name

            val value = (currentInstruction.r2 as Symbol).getAttribute(Attribute.LITERAL_VALUE)

            val assemblerVariable = createAssemblerVariable(variableName, variableType, value)
            formatter.addVariable(assemblerVariable)
        }
    }

    fun assertIsAssignmentInstruction(instruction: ThreeAddressCode) {
        if (instruction.op != IrOperation.ASSIGN) {
            throw RuntimeException("expected assignment operation for top level variable declaration");
        }
    }
}
