package codeGeneration.instructionSelection

import codeGeneration.instructionSelection.mips.*
import parser.semantic.ir.*
import parser.semantic.symboltable.Attribute
import parser.semantic.symboltable.Symbol
import parser.semantic.symboltable.SymbolTableEntry


class InstructionSelector(private val ir: LinearIr) {

    private val formatter = MipsFormatter()

    private val globalVarGpOffsets = mutableMapOf<String, Int>()
    private val functionSymbols = mutableListOf<Symbol>()
    private val stackInstructionSelectors = mutableMapOf<Symbol, StackInstructionSelector>()

    fun run(): MipsFormatter {
        makeDataSegment()

        makeTextSegment()

        return formatter
    }

    private fun makeTextSegment() {

        val functionSegments = getFunctionInstructionLists()

        for ((functionSymbol, instructionList) in functionSegments) {
            val stackInstructionSelector = StackInstructionSelector(functionSymbol, globalVarGpOffsets)

            stackInstructionSelector.makeStackFrame(instructionList)

            stackInstructionSelectors.put(functionSymbol, stackInstructionSelector)
        }

        for ((functionSymbol, instructionList) in functionSegments) {
            for (instruction in instructionList) {
                generateAssembly(instruction, functionSymbol)
            }
        }
    }

    private fun generateAssembly(instruction: IrCode, functionSymbol: Symbol) {
        if (instruction is Label) {
            formatter.appendInstruction(MipsLabel(instruction.name))

            if (isFunctionStart(instruction)) {
                val stackInstructionSelector = stackInstructionSelectors[findFunctionSymbolByLabel(instruction)]

                stackInstructionSelector!!.stackCreationInstructions.forEach { formatter.appendInstruction(it) }
            }

        } else if (instruction is FunctionCallCode) {
            val stackInstructionSelector = stackInstructionSelectors[instruction.functionSymbol]

            val functionCallStackCode = stackInstructionSelector!!.generateCallingCode(instruction)

            functionCallStackCode.forEach { formatter.appendInstruction(it) }

        } else if (instruction is ThreeAddressCode) {
            val instructions = generateThreeAddressCodeAssembly(instruction, functionSymbol)

            instructions.forEach { formatter.appendInstruction(it) }
        } else {
            throw RuntimeException("one of those should have worked")
        }
    }

    private fun  generateThreeAddressCodeAssembly(instruction: ThreeAddressCode, functionSymbol: Symbol): List<MipsInstruction> {
        when (instruction.op) {
            (IrOperation.ADD) ->
                    return generateAdd(instruction)

            (IrOperation.AND) ->
                    return generateAnd(instruction)

            (IrOperation.ARRAY_LOAD) ->
                    return generateArrayLoad(instruction, functionSymbol)

            (IrOperation.ARRAY_STORE) ->
                    return generateArrayStore(instruction, functionSymbol)

            (IrOperation.ASSIGN) ->
                    return generateAssign(instruction)

            (IrOperation.BREQ) ->
                    return generateBranchInstruction(instruction, MipsOpcode.beq)

            (IrOperation.BRGEQ) ->
                    return generateBranchInstruction(instruction, MipsOpcode.bge)

            (IrOperation.BRGT) ->
                    return generateBranchInstruction(instruction, MipsOpcode.bgt)

            (IrOperation.BRLEQ) ->
                    return generateBranchInstruction(instruction, MipsOpcode.ble)

            (IrOperation.BRLT) ->
                    return generateBranchInstruction(instruction, MipsOpcode.blt)

            (IrOperation.BRNEQ) ->
                    return generateBranchInstruction(instruction, MipsOpcode.bne)

            (IrOperation.DIV) ->
                    return generateDivision(instruction)

            (IrOperation.LOAD) ->
                    return generateLoad(instruction, functionSymbol)

            (IrOperation.MULT) ->
                    return generateMultiplication(instruction)

            (IrOperation.OR) ->
                    return generateOr(instruction)

            (IrOperation.STORE) ->
                    return generateStore(instruction, functionSymbol)

            (IrOperation.RETURN) ->
                    return generateReturn(instruction, functionSymbol)

            (IrOperation.SUB) ->
                    return generateSub(instruction)
            else ->
                    throw RuntimeException("unrecognized Ir code")
        }
    }

    private fun  generateSub(irInstruction: ThreeAddressCode): List<MipsInstruction> {
        val instructions = mutableListOf<MipsInstruction>()

        val destinationReg = irInstruction.r1 as Symbol

        val addType = destinationReg.getAttribute(Attribute.TYPE)

        val addInstruction: MipsInstruction
        if (addType is IntegerExpressionType) {
            addInstruction = MipsInstruction(MipsOpcode.sub, irInstruction.r1.name, irInstruction.r2.name, irInstruction.r3.name)

        } else if (addType is FloatExpressionType) {
            addInstruction = MipsInstruction(MipsOpcode.sub_s, irInstruction.r1.name, irInstruction.r2.name, irInstruction.r3.name)

        } else {
            throw RuntimeException("must sub int or float")
        }

        instructions.add(addInstruction)

        return instructions
    }

    private fun generateReturn(irInstruction: ThreeAddressCode, functionSymbol: Symbol): List<MipsInstruction> {
        val stackBuilder = stackInstructionSelectors[functionSymbol]!!

        return stackBuilder.generateReturnCode(irInstruction)
    }

    private fun generateStore(irInstruction: ThreeAddressCode, functionSymbol: Symbol): List<MipsInstruction> {
        val stackBuilder = stackInstructionSelectors[functionSymbol]!!

        val loadInstruction: MipsInstruction
        if (stackBuilder.variableFpOffsets.containsKey(irInstruction.r1.name)) {
            val fpOffset = stackBuilder.variableFpOffsets[irInstruction.r1.name]

            loadInstruction = MipsInstruction(MipsOpcode.sw, irInstruction.r2.name, "$fpOffset", "\$fp")

        } else if (stackBuilder.paramFpOffsets.containsKey(irInstruction.r1.name)) {
            val fpOffset = stackBuilder.paramFpOffsets[irInstruction.r1.name]

            loadInstruction = MipsInstruction(MipsOpcode.sw, irInstruction.r2.name, "$fpOffset", "\$fp")

        } else if (globalVarGpOffsets.containsKey(irInstruction.r1.name)) {
            val gpOffset = globalVarGpOffsets[irInstruction.r1.name]

            loadInstruction = MipsInstruction(MipsOpcode.sw, irInstruction.r2.name, "$gpOffset", "\$gp")
        } else {
            throw RuntimeException("needs to be in one of the scopes")
        }

        return listOf(loadInstruction)
    }

    private fun generateOr(irInstruction: ThreeAddressCode): List<MipsInstruction> {
        return listOf(MipsInstruction(MipsOpcode.or, irInstruction.r1.name, irInstruction.r2.name, irInstruction.r3.name))
    }

    private fun generateMultiplication(irInstruction: ThreeAddressCode): List<MipsInstruction> {
        val resultType = (irInstruction.r1 as Symbol).getAttribute(Attribute.TYPE)

        val divideInstruction: MipsInstruction
        if (resultType is IntegerExpressionType) {
            divideInstruction = MipsInstruction(MipsOpcode.mult, irInstruction.r2.name, irInstruction.r3.name)
        } else {
            divideInstruction = MipsInstruction(MipsOpcode.mul_s, irInstruction.r2.name, irInstruction.r3.name)
        }

        val moveFromLowInstruction = MipsInstruction(MipsOpcode.mflo, irInstruction.r1.name)

        return listOf(divideInstruction, moveFromLowInstruction)
    }

    private fun  generateLoad(irInstruction: ThreeAddressCode, functionSymbol: Symbol): List<MipsInstruction> {
        val stackBuilder = stackInstructionSelectors[functionSymbol]!!

        val loadInstruction: MipsInstruction
        if (stackBuilder.variableFpOffsets.containsKey(irInstruction.r1.name)) {
            val fpOffset = stackBuilder.variableFpOffsets[irInstruction.r1.name]

            loadInstruction = MipsInstruction(MipsOpcode.lw, irInstruction.r2.name, "$fpOffset", "\$fp")

        } else if (stackBuilder.paramFpOffsets.containsKey(irInstruction.r1.name)) {
            val fpOffset = stackBuilder.paramFpOffsets[irInstruction.r1.name]

            loadInstruction = MipsInstruction(MipsOpcode.lw, irInstruction.r2.name, "$fpOffset", "\$fp")

        } else if (globalVarGpOffsets.containsKey(irInstruction.r1.name)) {
            val gpOffset = globalVarGpOffsets[irInstruction.r1.name]

            loadInstruction = MipsInstruction(MipsOpcode.lw, irInstruction.r2.name, "$gpOffset", "\$gp")
        } else {
            throw RuntimeException("needs to be in one of the scopes")
        }

        return listOf(loadInstruction)
    }


    private fun generateDivision(irInstruction: ThreeAddressCode): List<MipsInstruction> {
        val resultType = (irInstruction.r1 as Symbol).getAttribute(Attribute.TYPE)

        val divideInstruction: MipsInstruction
        if (resultType is IntegerExpressionType) {
            divideInstruction = MipsInstruction(MipsOpcode.div, irInstruction.r2.name, irInstruction.r3.name)
        } else {
            divideInstruction = MipsInstruction(MipsOpcode.div_s, irInstruction.r2.name, irInstruction.r3.name)
        }

        val moveFromLowInstruction = MipsInstruction(MipsOpcode.mflo, irInstruction.r1.name)

        return listOf(divideInstruction, moveFromLowInstruction)
    }

    private fun generateBranchInstruction(irInstruction: ThreeAddressCode, opcode: MipsOpcode): List<MipsInstruction> {
        return listOf(MipsInstruction(opcode, irInstruction.r2.name, irInstruction.r3.name, irInstruction.r1.name))
    }

    private fun generateAssign(irInstruction: ThreeAddressCode): List<MipsInstruction> {
        val opcode: MipsOpcode
        if ((irInstruction.r1 as Symbol).getAttribute(Attribute.TYPE) is IntegerExpressionType) {
            opcode = MipsOpcode.add
        } else {
            opcode = MipsOpcode.add_s
        }

        return listOf(MipsInstruction(opcode, irInstruction.r1.name, irInstruction.r2.name, irInstruction.r3.name))
    }

    private fun generateArrayStore(irInstruction: ThreeAddressCode, functionSymbol: Symbol): List<MipsInstruction> {
        val arraySymbol = irInstruction.r1 as Symbol

        val localStackBuilder = stackInstructionSelectors[functionSymbol]

        if (localStackBuilder!!.variableFpOffsets.containsKey(arraySymbol.name)) {
            val fpOffset = localStackBuilder.variableFpOffsets[arraySymbol.name]

            val calculateIndexInstruction = MipsInstruction(MipsOpcode.addi, "\$t9,", irInstruction.r2.name, "$fpOffset")

            val calculateAddressInstruction = MipsInstruction(MipsOpcode.add, "\$t9", "\$t9", "\$fp")

            return listOf(calculateIndexInstruction, calculateAddressInstruction, MipsInstruction(MipsOpcode.sw, irInstruction.r3.name, "0", "\$t9"))

        } else if (globalVarGpOffsets.containsKey(arraySymbol.name)) {
            val gpOffset = globalVarGpOffsets[arraySymbol.name]

            val calculateIndexInstruction = MipsInstruction(MipsOpcode.addi, "\$t9", irInstruction.r2.name, "$gpOffset")

            val calculateAddressInstruction = MipsInstruction(MipsOpcode.add, "\$t9", "\$t9", "\$gp")

            return listOf(calculateIndexInstruction, calculateAddressInstruction, MipsInstruction(MipsOpcode.sw, irInstruction.r3.name, "0", "\$t9"))
        } else {
            throw RuntimeException("has to be one of the above")
        }
    }

    private fun generateArrayLoad(irInstruction: ThreeAddressCode, functionSymbol: Symbol): List<MipsInstruction> {
        val arraySymbol = irInstruction.r2 as Symbol
        val arrayType = arraySymbol.getAttribute(Attribute.TYPE) as ArrayExpressionType

        val localStackBuilder = stackInstructionSelectors[functionSymbol]

        if (localStackBuilder!!.variableFpOffsets.containsKey(arraySymbol.name)) {

            val fpOffset = localStackBuilder.variableFpOffsets[arraySymbol.name]

            val calculateIndexInstruction = MipsInstruction(MipsOpcode.addi, "\$t9", irInstruction.r3.name, "$fpOffset")

            val calculateAddressInstruction = MipsInstruction(MipsOpcode.add, "\$t9", "\$t9", "\$fp")

            return listOf(calculateIndexInstruction, calculateAddressInstruction, MipsInstruction(MipsOpcode.lw, irInstruction.r1.name, "0", "\$t9"))

        } else if (globalVarGpOffsets.containsKey(arraySymbol.name)) {
            val gpOffset = globalVarGpOffsets[arraySymbol.name]

            val calculateIndexInstruction = MipsInstruction(MipsOpcode.addi, "\$t9", irInstruction.r3.name, "$gpOffset")

            val calculateAddressInstruction = MipsInstruction(MipsOpcode.add, "\$t9", "\$t9", "\$gp")

            return listOf(calculateIndexInstruction, calculateAddressInstruction, MipsInstruction(MipsOpcode.lw, irInstruction.r1.name, "0", "\$t9"))
        } else {
            throw RuntimeException("has to be one of the above")
        }
    }

    private fun generateAnd(irInstruction: ThreeAddressCode): List<MipsInstruction> {
        return mutableListOf(MipsInstruction(MipsOpcode.and, irInstruction.r1.name, irInstruction.r2.name, irInstruction.r3.name))
    }

    private fun generateAdd(irInstruction: ThreeAddressCode): List<MipsInstruction> {
        val instructions = mutableListOf<MipsInstruction>()

        val destinationReg = irInstruction.r1 as Symbol

        val addType = destinationReg.getAttribute(Attribute.TYPE)

        val addInstruction: MipsInstruction
        if (addType is IntegerExpressionType) {
            addInstruction = MipsInstruction(MipsOpcode.add, irInstruction.r1.name, irInstruction.r2.name, irInstruction.r3.name)

        } else if (addType is FloatExpressionType) {
            addInstruction = MipsInstruction(MipsOpcode.add_s, irInstruction.r1.name, irInstruction.r2.name, irInstruction.r3.name)

        } else {
            throw RuntimeException("must add int or float")
        }

        instructions.add(addInstruction)

        return instructions
    }


    private fun getFunctionInstructionLists(): Map<Symbol, List<IrCode>> {
        findAllFunctionLabels()

        val functionSegments = mutableMapOf<Symbol, List<IrCode>>()

        val fakeStartingSymbol = Symbol("not a real Symbol")

        var currentFunctionSymbol = fakeStartingSymbol
        var currentFunctionCodes = mutableListOf<IrCode>()

        ir.forEach {
            if (isFunctionStart(it)) {
                functionSegments.put(currentFunctionSymbol, currentFunctionCodes)

                currentFunctionCodes = mutableListOf()
                currentFunctionSymbol = findFunctionSymbolByLabel(it as Label)
            }

            currentFunctionCodes.add(it)
        }

        functionSegments.remove(fakeStartingSymbol)
        return functionSegments
    }

    private fun findFunctionSymbolByLabel(label: Label): Symbol {
        return functionSymbols.first { it.getAttribute(Attribute.FUNCTION_START_LABEL) == label }
    }

    private fun isFunctionStart(code: IrCode): Boolean {
        return code is Label && functionSymbols.any { it.getAttribute(Attribute.FUNCTION_START_LABEL) == code }
    }

    private fun findAllFunctionLabels() {
        val functionLabelsUsed = ir.filterIsInstance<FunctionCallCode>()
                .map { it.functionSymbol }
                .distinct()

        functionSymbols.addAll(functionLabelsUsed)
    }

    private fun makeDataSegment() {
        val irIterator = ir.iterator()

        var currentInstruction = irIterator.next()

        var globalPointerOffset = 0
        while (currentInstruction is ThreeAddressCode) {
            if (currentInstruction.op == IrOperation.ASSIGN) {

                val variableSymbol = currentInstruction.r1 as Symbol
                val variableType = variableSymbol.getAttribute(Attribute.TYPE) as ExpressionType

                val variableName = variableSymbol.name
                globalVarGpOffsets.put(variableName, globalPointerOffset)

                globalPointerOffset += variableType.size

                val value = (currentInstruction.r2 as Symbol).getAttribute(Attribute.LITERAL_VALUE)

                val assemblerVariable = createAssemblerVariable(variableName, variableType, value)
                formatter.addVariable(assemblerVariable)
            }

            currentInstruction = irIterator.next()
        }

        val literals = getLiterals()

        literals.forEach {

            val literalType = it.getAttribute(Attribute.TYPE) as ExpressionType
            val literalName = it.name

            globalVarGpOffsets.put(literalName, globalPointerOffset)

            globalPointerOffset += literalType.size

            val value = it.getAttribute(Attribute.LITERAL_VALUE)

            formatter.addVariable(createAssemblerVariable(literalName, literalType, value))
        }
    }

    private fun getLiterals(): List<Symbol> {
        val allSymbols = mutableListOf<SymbolTableEntry>()
        ir.filterIsInstance<ThreeAddressCode>().forEach {
            if (it.r1 != null) allSymbols.add(it.r1)
            if (it.r2 != null) allSymbols.add(it.r2)
            if (it.r3 != null) allSymbols.add(it.r3)
        }
        ir.filterIsInstance<FunctionCallCode>().forEach {
            if (it.r1 != null) allSymbols.add(it.r1)
            allSymbols.addAll(it.args)
        }

        val literals = allSymbols
                .filterIsInstance<Symbol>()
                .filter { it.getAttribute(Attribute.IS_LITERAL) as Boolean }
                .distinct()
        return literals
    }

    private fun assertIsAssignmentInstruction(instruction: ThreeAddressCode) {
        if (instruction.op != IrOperation.ASSIGN) {
            throw RuntimeException("expected assignment operation for top level variable declaration");
        }
    }
}
