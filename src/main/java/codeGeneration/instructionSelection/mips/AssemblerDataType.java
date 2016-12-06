package codeGeneration.instructionSelection.mips;

public enum AssemblerDataType {
    ASCII("ascii"),
    ASCIIZ("asciiz"),
    BYTE("byte"),
    HALFWORD("halfword"),
    WORD("word"),
    SPACE("space");

    private String mipsAssemblyName;

    AssemblerDataType(String pMipsAssemblyName) {
        mipsAssemblyName = pMipsAssemblyName;
    }

    public String getMipsAssemblyName() {
        return mipsAssemblyName;
    }
}
