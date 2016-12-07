package codeGeneration.instructionSelection.mips;

public enum MipsOpcode {
    add(3, false),
    addu(3, false),
    addi(3, false),
    addiu(3, false),

    sub(3, false),
    subu(3, false),

    mult(2, false),
    multu(2, false),

    div(2, false),
    divu(2, false),

    and(3, false),
    andi(3, false),

    or(3, false),
    ori(3, false),

    xor(3, false),
    xori(3, false),

    nor(3, false),


    sll(3, false),
    sllv(3, false),


    srl(3, false),
    sra(3, false),
    srlv(3, false),
    srav(3, false),

    slt(3, false),
    sltu(3, false),
    slti(3, false),


    beq(3, false),
    bne(3, false),
    blt(3, false),
    bgt(3, false),
    bge(3, false),
    ble(3, false),

    j(1, false),
    jr(1, false),
    jal(1, false),


    lw(3, true),
    lh(3, true),
    lhu(3, true),

    lb(3, true),
    lbu(3, true),

    sw(3, true),
    sh(3, true),
    sb(3, true),

    lui(2, false),

    mfhi(1, false),
    mflo(1, false),

    mfcZ(2, false),
    mtcZ(2, false),

    add_s(3, false),
    sub_s(3, false),
    mul_s(3, false),
    div_s(3, false);

    private int argCount;
    private boolean usesOffsetNotation;

    MipsOpcode(int argCount, boolean usesOffsetNotation) {
        this.argCount = argCount;
        this.usesOffsetNotation = usesOffsetNotation;
    }

    public int getArgCount() {
        return argCount;
    }

    public boolean usesOffsetSyntax() {
        return usesOffsetNotation;
    }
}
