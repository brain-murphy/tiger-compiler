package codeGeneration.instructionSelection.mips

import parser.semantic.symboltable.Attribute
import parser.semantic.ir.Label
import parser.semantic.symboltable.Symbol
import parser.semantic.symboltable.SymbolTableEntry


fun makeMipsInstruction(opcode: MipsOpcode, vararg params: SymbolTableEntry): MipsInstruction {
    return MipsInstruction(opcode, *(params.map { it.toParameterString() }.toTypedArray()))
}

fun SymbolTableEntry.toParameterString(): String {
    if (this is Label) {
        return this.name

    } else if (this is Symbol) {
        val isConstant = this.getAttribute(Attribute.IS_LITERAL) as Boolean

        if (isConstant) {
            return this.getAttribute(Attribute.LITERAL_VALUE).toString()

        } else { // is variable
            return this.name
        }

    } else {
        throw RuntimeException("expected either symbol or label for mips instruction parameter")
    }
}


open class MipsInstruction(val opcode: MipsOpcode, vararg val params: String) {
    override fun toString(): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append(opcode.name)
                .append(" ")

        if (opcode.usesOffsetSyntax()) {
            appendArgsOffsetSyntax(stringBuilder)

        } else {
            appendArgsCommaSyntax(stringBuilder)
        }

        return stringBuilder.toString()
    }

    private fun appendArgsCommaSyntax(stringBuilder: StringBuilder) {
        for (paramIndex in 0..params.size - 2) {
            stringBuilder.append(params[paramIndex])
            stringBuilder.append(",")
        }

        stringBuilder.append(params.last())
    }

    private fun appendArgsOffsetSyntax(stringBuilder: StringBuilder) {
        for (paramIndex in 0..params.size - 3) {
            stringBuilder.append(params[paramIndex])
            stringBuilder.append(",")
        }

        stringBuilder.append(params[params.size - 2])

        stringBuilder.append("(")
                .append(params.last())
                .append(")")
    }
}

class MipsLabel(val name: String): MipsInstruction(MipsOpcode.add) {
    override fun toString(): String {
        return "$name:\n"
    }
}

