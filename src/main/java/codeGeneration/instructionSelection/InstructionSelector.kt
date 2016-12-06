package codeGeneration.instructionSelection

import codeGeneration.instructionSelection.mips.MipsFormatter
import codeGeneration.instructionSelection.mips.createAssemblerVariable
import parser.semantic.ir.*
import parser.semantic.symboltable.Attribute
import parser.semantic.symboltable.Symbol
import parser.semantic.symboltable.SymbolTable


class InstructionSelector(private val ir: LinearIr, private val symbolTable: SymbolTable) {

    private val formatter = MipsFormatter()

    private val globalVariableNames = mutableListOf<String>()
    private val functionSymbols = mutableListOf<Symbol>()

    fun run() {
        makeDataSegment()

        makeTextSegment()
    }

    private fun makeTextSegment() {

        val functionSegments = getFunctionInstructionLists()

        val stackInstructionSelector = StackInstructionSelector(globalVariableNames)

        for (instructionList in functionSegments) {

        }
    }

    private fun getFunctionInstructionLists(): Map<Symbol, List<IrCode>> {
        findAllFunctionLabels()

        val functionSegments = mutableMapOf<Symbol, List<IrCode>>()

        val fakeStartingSymbol = Symbol("not a real Symbol")

        var currentFunctionSymbol = fakeStartingSymbol
        var currentFunctionCodes = mutableListOf<IrCode>()

        ir.forEach {
            if (isFunctionStart(it)) {
                functionSegments.put(currentFunctionSymbol, currentFunctionCodes)

                currentFunctionCodes = mutableListOf()
                currentFunctionSymbol = functionSymbols.first { it.getAttribute(Attribute.FUNCTION_START_LABEL) == it }
            }

            currentFunctionCodes.add(it)
        }

        functionSegments.remove(fakeStartingSymbol)
        return functionSegments
    }

    private fun isFunctionStart(code: IrCode): Boolean {
        return code is Label && functionSymbols.any { it.getAttribute(Attribute.FUNCTION_START_LABEL) == code }
    }

    private fun findAllFunctionLabels() {
        val functionLabelsUsed = ir.filterIsInstance<FunctionCallCode>()
                .map { it.functionSymbol }
                .distinct()

        functionSymbols.addAll(functionLabelsUsed)
    }

    private fun makeDataSegment() {
        val irIterator = ir.iterator()

        var currentInstruction = irIterator.next()

        while (currentInstruction is ThreeAddressCode) {
            assertIsAssignmentInstruction(currentInstruction)

            val variableSymbol = currentInstruction.r1 as Symbol
            val variableType = variableSymbol.getAttribute(Attribute.TYPE) as ExpressionType

            val variableName = variableSymbol.name
            globalVariableNames.add(variableName)

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
