package codeGeneration.instructionSelection.mips

class MipsFormatter() {
    val dataSegment = mutableListOf<AssemblerVariable>()
    val textSegment = mutableListOf<MipsInstruction>()

    fun addVariable(variable: AssemblerVariable) {
        dataSegment.add(variable)
    }

    fun appendInstruction(instruction: MipsInstruction) {
        textSegment.add(instruction)
    }

    fun insertInstruction(index: Int, instruction: MipsInstruction) {
        textSegment.add(index, instruction)
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append("\t.data\n")

        dataSegment.forEach {
            stringBuilder.append(it.toString())
            stringBuilder.append("\n")
        }


        stringBuilder.append("\n\t.text\n")

        textSegment.forEach {
            stringBuilder.append(it.toString())
            stringBuilder.append("\n")
        }

        return stringBuilder.toString()
    }
}