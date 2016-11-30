package codeGeneration.instructionSelection.mips

import parser.semantic.symboltable.SymbolTableEntry

class MipsInstruction(val opcode: MipsOpcode, vararg val params: SymbolTableEntry)
