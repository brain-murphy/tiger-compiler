package codeGeneration.instructionSelection

import codeGeneration.instructionSelection.mips.MipsInstruction
import codeGeneration.instructionSelection.mips.MipsOpcode
import codeGeneration.instructionSelection.mips.makeMipsInstruction
import parser.semantic.ir.*
import parser.semantic.symboltable.Attribute
import parser.semantic.symboltable.Symbol
import parser.semantic.symboltable.SymbolTableEntry
import java.lang.reflect.Type


class StackInstructionSelector(private val functionSymbol: Symbol, private val globalVarGpOffsets: Map<String, Int>) {
    val paramFpOffsets = mutableMapOf<String, Int>()
    val variableFpOffsets = mutableMapOf<String, Int>()
    val stackCreationInstructions = mutableListOf<MipsInstruction>()


    fun makeStackFrame(code: List<IrCode>) {

        storePastFp(stackCreationInstructions)
        setFramePointer(stackCreationInstructions)

        calculateParamAddresses(functionSymbol)

        allocateVariables(code, stackCreationInstructions)
    }

    fun generateCallingCode(callCode: FunctionCallCode): List<MipsInstruction> {
        val functionType = functionSymbol.getAttribute(Attribute.TYPE) as FunctionExpressionType
        val paramSize = functionType.params
                .map{ it.size }
                .sum()

        val instructions = mutableListOf<MipsInstruction>()

        val setSpInstruction = MipsInstruction(MipsOpcode.addi, "\$sp", "\$sp", "${ -paramSize}")
        instructions.add(setSpInstruction)

        var spOffset = 1
        for (arg in callCode.args) {
            val argType = arg.getAttribute(Attribute.TYPE)
            if (argType is IntegerExpressionType || argType is FloatExpressionType) {
                val storeInstruction = MipsInstruction(MipsOpcode.sw, arg.name, "$spOffset", "\$sp")

                instructions.add(storeInstruction)

                spOffset += 1
            } else {
                throw RuntimeException("cannot do array arguments at the moment");
            }
        }
        return instructions
    }

    fun generateReturnCode(returnInstruction: ThreeAddressCode): List<MipsInstruction> {
        val instructions = mutableListOf<MipsInstruction>()

        val popStackInstruction = MipsInstruction(MipsOpcode.addi, "\$sp", "\$fp", "2")
        instructions.add(popStackInstruction)

        if (returnInstruction.hasReturnValue()) {
            val storeReturnValueCode = MipsInstruction(MipsOpcode.sw, returnInstruction.r1.name, "0", "\$sp")
            instructions.add(storeReturnValueCode)
        }

        val restoreFramePointerCode = MipsInstruction(MipsOpcode.lw, "\$fp", "1", "\$fp")
        instructions.add(restoreFramePointerCode)

        return instructions
    }

    private fun calculateParamAddresses(functionSymbol: Symbol) {
        val functionType = functionSymbol.getAttribute(Attribute.TYPE) as FunctionExpressionType
        val paramNames = functionSymbol.getAttribute(Attribute.FUNCTION_PARAM_NAMES) as Array<String>

        var fpOffset = 3 // frame pointer sits above ret val and past fp
        functionType.params.forEachIndexed { i, paramType ->
            paramFpOffsets.put(paramNames[i], fpOffset)

            fpOffset += paramType.size
        }
    }

    private fun allocateVariables(irCode: List<IrCode>, instructions: MutableList<MipsInstruction>) {
        val distinctSymbols = getDistictSymbolsUsed(irCode)

        val variableSegmentSize = distinctSymbols
                .map { (it.getAttribute(Attribute.TYPE) as ExpressionType).size }
                .sum()

        var fpOffset = 1 - variableSegmentSize // allocate starting at the top of the stack, moving down
        for (symbol in distinctSymbols) {
            variableFpOffsets.put(symbol.name, fpOffset)

            val variableType = symbol.getAttribute(Attribute.TYPE) as ExpressionType

            fpOffset += variableType.size
        }

        val setSpInstruction = MipsInstruction(MipsOpcode.addi, "\$sp", "\$fp", "${ -variableSegmentSize }")
        instructions.add(setSpInstruction)
    }

    private fun getDistictSymbolsUsed(irCode: List<IrCode>): List<Symbol> {
        val allSymbols = mutableListOf<SymbolTableEntry>()
        irCode.filterIsInstance<ThreeAddressCode>().forEach {
            allSymbols.add(it.r1)
            allSymbols.add(it.r2)
            allSymbols.add(it.r3)
        }
        irCode.filterIsInstance<FunctionCallCode>().forEach {
            allSymbols.add(it.r1)
            allSymbols.addAll(it.args)
        }

        val distinctSymbols = allSymbols
                .filterIsInstance<Symbol>()
                .filter { !paramFpOffsets.containsKey(it.name)}
                .filter { !globalVarGpOffsets.containsKey(it.name)}
                .distinct()

        return distinctSymbols
    }

    private fun storePastFp(instructions: MutableList<MipsInstruction>) {
        val fpStore = MipsInstruction(MipsOpcode.sw, "\$fp", "-1", "\$sp")
        instructions.add(fpStore)

        val incrementSp = MipsInstruction(MipsOpcode.addi, "\$sp", "\$sp", "-1")
        instructions.add(incrementSp)
    }

    private fun setFramePointer(instructions: MutableList<MipsInstruction>) {
        val fpAssign = MipsInstruction(MipsOpcode.addi, "\$fp", "\$sp", "-2")

        instructions.add(fpAssign)
    }
}

private fun Symbol.isLiteral(): Boolean {
    return this.getAttribute(Attribute.IS_LITERAL) as Boolean
}

private fun ThreeAddressCode.hasReturnValue(): Boolean {
    return  this.r1 != null
            && this.r2 != null
            && this.r3 != null
}