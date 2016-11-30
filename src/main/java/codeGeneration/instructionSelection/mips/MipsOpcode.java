package codeGeneration.instructionSelection.mips;

public enum MipsOpcode {
    add,
    addu,
    addi,
    addiu,

    sub,
    subu,

    mult,
    multu,

    div,
    divu,

    and,
    andi,

    or,
    ori,

    xor,
    xori,

    nor,


    sll,
    sllv,


    srl,
    sra,
    srlv,
    srav,

    slt,
    sltu,
    slti,


    beq,
    bne,

    j,
    jr,
    jal,


    lw,
    lh,
    lhu,

    lb,
    lbu,

    sw,
    sh,
    sb,

    lui,

    mfhi,
    mflo,

    mfcZ,
    mtcZ,
}
