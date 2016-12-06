package codeGeneration.instructionSelection.mips

import parser.semantic.ir.ExpressionType
import parser.semantic.ir.FloatExpressionType
import parser.semantic.ir.IntegerExpressionType

fun createAssemblerVariable(variableName: String, type: ExpressionType, value: Any): AssemblerVariable {
    val assemblerDataType: AssemblerDataType

    if (type is IntegerExpressionType) {
        assemblerDataType = AssemblerDataType.WORD

    } else if (type is FloatExpressionType) {
        assemblerDataType = AssemblerDataType.FLOAT
    } else {
        throw RuntimeException("couldn't recognize storable data type")
    }

    return AssemblerVariable(variableName, assemblerDataType, value.toString())
}

class AssemblerVariable(val label: String, val type: AssemblerDataType, val value: String) {
    override fun toString(): String {
        return "$label: .${ type.mipsAssemblyName } $value"
    }
}
