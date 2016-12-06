package codeGeneration.instructionSelection

import codeGeneration.instructionSelection.mips.MipsFormatter
import codeGeneration.instructionSelection.mips.createAssemblerVariable
import parser.semantic.ir.*
import parser.semantic.symboltable.Attribute
import parser.semantic.symboltable.Symbol
import parser.semantic.symboltable.SymbolTable
import parser.semantic.symboltable.SymbolTableEntry
import java.lang.reflect.Type


class InstructionSelector(private val ir: LinearIr, private val symbolTable: SymbolTable) {

    private val formatter = MipsFormatter()

    private val globalVarGpOffsets = mutableMapOf<String, Int>()
    private val functionSymbols = mutableListOf<Symbol>()

    fun run() {
        makeDataSegment()

        makeTextSegment()
    }

    private fun makeTextSegment() {

        val functionSegments = getFunctionInstructionLists()

//        val stackInstructionSelector = StackInstructionSelector(globalVarGpOffsets)

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

        var globalPointerOffset = 0
        while (currentInstruction is ThreeAddressCode) {
            assertIsAssignmentInstruction(currentInstruction)

            val variableSymbol = currentInstruction.r1 as Symbol
            val variableType = variableSymbol.getAttribute(Attribute.TYPE) as ExpressionType

            val variableName = variableSymbol.name
            globalVarGpOffsets.put(variableName, globalPointerOffset)

            globalPointerOffset += variableType.size

            val value = (currentInstruction.r2 as Symbol).getAttribute(Attribute.LITERAL_VALUE)

            val assemblerVariable = createAssemblerVariable(variableName, variableType, value)
            formatter.addVariable(assemblerVariable)
        }

        val literals = getLiterals()

        literals.forEach {

            val literalType = it.getAttribute(Attribute.TYPE) as ExpressionType
            val literalName = it.name

            globalVarGpOffsets.put(literalName, globalPointerOffset)

            globalPointerOffset += literalType.size

            val value = it.getAttribute(Attribute.LITERAL_VALUE)

            formatter.addVariable(createAssemblerVariable(literalName, literalType, value))
        }
    }

    private fun getLiterals(): List<Symbol> {
        val allSymbols = mutableListOf<SymbolTableEntry>()
        ir.filterIsInstance<ThreeAddressCode>().forEach {
            allSymbols.add(it.r1)
            allSymbols.add(it.r2)
            allSymbols.add(it.r3)
        }
        ir.filterIsInstance<FunctionCallCode>().forEach {
            allSymbols.add(it.r1)
            allSymbols.addAll(it.args)
        }

        val literals = allSymbols
                .filterIsInstance<Symbol>()
                .filter { it.getAttribute(Attribute.IS_LITERAL) as Boolean }
                .distinct()
        return literals
    }

    fun assertIsAssignmentInstruction(instruction: ThreeAddressCode) {
        if (instruction.op != IrOperation.ASSIGN) {
            throw RuntimeException("expected assignment operation for top level variable declaration");
        }
    }
}
