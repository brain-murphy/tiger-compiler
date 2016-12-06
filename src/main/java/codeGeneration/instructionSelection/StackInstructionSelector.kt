package codeGeneration.instructionSelection

import codeGeneration.instructionSelection.mips.MipsInstruction
import codeGeneration.instructionSelection.mips.MipsOpcode
import parser.semantic.ir.FunctionExpressionType
import parser.semantic.ir.IntegerExpressionType
import parser.semantic.ir.IrCode
import parser.semantic.symboltable.Attribute
import parser.semantic.symboltable.Symbol


class StackInstructionSelector(private val globalVariables: List<String>) {

    fun makeStackFrame(functionSymbol: Symbol, code: List<IrCode>) {
        val instructions = mutableListOf<MipsInstruction>()

        setFramePointer(instructions)

        val functionType = functionSymbol.getAttribute(Attribute.TYPE) as FunctionExpressionType

        for (argument in functionType.params) {
            if (argument is IntegerExpressionType) {

            }
        }
    }

    private fun setFramePointer(instructions: MutableList<MipsInstruction>) {
        val fpAssign = MipsInstruction(MipsOpcode.add, "\$fp", )
    }
}