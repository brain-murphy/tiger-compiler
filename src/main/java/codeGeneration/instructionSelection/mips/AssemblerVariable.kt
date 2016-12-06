package codeGeneration.instructionSelection.mips

class AssemblerVariable(val label: String, val type: AssemblerDataType, val value: String) {
    override fun toString(): String {
        return "$label: .${ type.mipsAssemblyName } $value"
    }
}
