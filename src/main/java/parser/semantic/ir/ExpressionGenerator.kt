package parser.semantic.ir

import parser.semantic.ParseStream
import parser.semantic.SemanticException
import parser.semantic.symboltable.Attribute
import parser.semantic.symboltable.Symbol
import parser.semantic.symboltable.SymbolTable
import parser.syntactic.NonTerminal
import parser.syntactic.Rule
import parser.syntactic.Rule.*
import scanner.TokenType
import java.util.*

class ExpressionGenerator(private val symbolTable: SymbolTable,
                          private val parseStream: ParseStream,
                          private val irOutput: LinearIr) {

    fun generateAssignmentExpression(): Symbol {
        val result: Symbol

        val firstTermParsingRule = parseStream.nextRule()

        if (firstTermParsingRule == EXPRESSION_NOT_STARTING_WITH_ID_RULE) {
            result = generateExpressionNotStartingWithId()

        } else if (firstTermParsingRule == EXPRESSION_OR_FUNCTION_START_RULE) {
            result = generateExpressionStartingWithId()

        } else {
            throw RuntimeException("expected rule to parse right hand side of an assignment statement. Found: $firstTermParsingRule")
        }

        return result
    }

    private fun generateExpressionStartingWithId(): Symbol {
        val result: Symbol

        val expressionOrFunctionTailRule = parseStream.nextRule()

        if (expressionOrFunctionTailRule == FUNCTION_INVOCATION_RULE) {
            result = generateExprTail(generateFunctionInvocation())

        } else if (expressionOrFunctionTailRule == LVALUE_EXPRESSION_START_RULE) {
            result = completeRecursiveWalk(generateLValue())

        } else {
            throw RuntimeException("expected rule to parse expression starting with id. Found: $expressionOrFunctionTailRule")
        }

        return result
    }

    private fun generateExpressionNotStartingWithId(): Symbol {
        val expressionStart = generateNonIdExpressionStart()

        var operationResult = completeRecursiveWalk(expressionStart)

        return operationResult
    }

    private fun completeRecursiveWalk(expressionStart: Symbol): Symbol {
        var operationResult = expressionStart

        operationResult = generateCTermTail(operationResult)
        operationResult = generateBTermTail(operationResult)
        operationResult = generateATermTail(operationResult)
        operationResult = generateExprTail(operationResult)

        return operationResult
    }

    private fun generateNonIdExpressionStart(): Symbol {
        val result: Symbol

        val expressionStartRule = parseStream.nextRule()
        if (expressionStartRule == CONST_EXPRESSION_START_RULE) {
            result = generateConst()

        } else if (expressionStartRule == PAREN_EXPRESSION_START_RULE) {
            result = generateStandaloneExpression()

        } else {
            throw RuntimeException("expected rule to parse non-id expression start. Found: $expressionStartRule")
        }

        return result
    }

    /**
     * parses expressions where it is not necessary to account for it starting with a function invocation
     */
    fun generateStandaloneExpression(): Symbol {
        assertNextRule(EXPR_START_RULE, "expected rule to start standalone expression")

        val aTerm = generateATerm()

        return generateExprTail(aTerm)
    }

    private fun generateExprTail(aTerm: Symbol): Symbol {
        val result: Symbol

        val exprTailOperationRule = parseStream.nextRule()

        if (exprTailOperationRule == AND_TERM_RULE) {
            result = generateANDOperation(aTerm, generateATerm())

        } else if (exprTailOperationRule == OR_TERM_RULE) {
            result = generateOROperation(aTerm, generateATerm())

        } else if (exprTailOperationRule == EXPR_END_RULE) {
            result = aTerm

        } else {
            throw RuntimeException("expected rule to parse expr tail. Found: $exprTailOperationRule")
        }

        return result
    }

    private fun generateATerm(): Symbol {
        assertNextRule(ATERM_START_RULE, "expected rule to start ATerm")

        val bTerm = generateBTerm()

        return generateATermTail(bTerm)
    }

    private fun generateATermTail(bTerm: Symbol): Symbol {
        val result: Symbol

        val aTermOperationRule = parseStream.nextRule()

        if (aTermOperationRule == EQ_TERM_RULE) {
            result = generateBooleanOperation(leftOperand = bTerm, op = IrOperation.BREQ, rightOperand = generateBTerm())

        } else if (aTermOperationRule == NEQ_TERM_RULE) {
            result = generateBooleanOperation(bTerm, IrOperation.BRNEQ, generateBTerm())

        } else if (aTermOperationRule == LESSER_TERM_RULE) {
            result = generateBooleanOperation(bTerm, IrOperation.BRLT, generateBTerm())

        } else if (aTermOperationRule == GREATER_TERM_RULE) {
            result = generateBooleanOperation(bTerm, IrOperation.BRGT, generateBTerm())

        } else if (aTermOperationRule == LESSEREQ_TERM_RULE) {
            result = generateBooleanOperation(bTerm, IrOperation.BRLEQ, generateBTerm())

        } else if (aTermOperationRule == GREATEREQ_TERM_RULE) {
            result = generateBooleanOperation(bTerm, IrOperation.BRGEQ, generateBTerm())

        } else if (aTermOperationRule == ATERM_TAIL_NULL_RULE) {
            result = bTerm

        } else {
            throw RuntimeException("expected rule to parse ATerm tail. Found: $aTermOperationRule")
        }

        return result
    }

    private fun generateBTerm(): Symbol {
        assertNextRule(BTERM_START_RULE, "expected rule to start BTerm")

        val cTerm = generateCTerm()

        return generateBTermTail(cTerm)
    }

    private fun generateBTermTail(cTerm: Symbol): Symbol {
        val bTermOperationRule = parseStream.nextRule()

        if (bTermOperationRule == PLUS_TERM_RULE) {
            return generateADDOperation(cTerm, generateCTerm())

        } else if (bTermOperationRule == MINUS_TERM_RULE) {
            return generateSUBTRACTOperation(cTerm, generateCTerm())

        } else if (bTermOperationRule == BTERM_TAIL_NULL_RULE) {
            return cTerm

        } else {
            throw RuntimeException("expected rule to parse bTerm tail. Found: $bTermOperationRule")
        }
    }

    private fun generateCTerm(): Symbol {
        assertNextRule(CTERM_START_RULE, "expected rule to start CTerm")

        val factor = generateFactor()

        return generateCTermTail(factor);
    }

    private fun generateCTermTail(factor: Symbol): Symbol {
        val cTermOperationRule = parseStream.nextRule()

        if (cTermOperationRule == MULT_TERM_RULE) {
            return generateMULTOperation(factor, generateFactor())

        } else if (cTermOperationRule == DIV_TERM_RULE) {
            return generateDIVOperation(factor, generateFactor())

        } else if (cTermOperationRule == CTERM_TAIL_NULL_RULE) {
            return factor

        } else {
            throw RuntimeException("expected rule to parse cTerm tail. Found: $cTermOperationRule")
        }
    }

    private fun generateFactor(): Symbol {
        val result: Symbol
        val factorRule = parseStream.nextRule()

        if (factorRule == CONST_TERM_RULE) {
            result = generateConst()

        } else if (factorRule == LVALUE_TERM_RULE) {
            result = generateLValue()

        } else if (factorRule == PAREN_TERM_RULE) {
            result = generateStandaloneExpression()

        } else {
            throw RuntimeException("expected rule to expand factor. Found: $factorRule")
        }

        return result
    }

    fun assertNextRule(expected: Rule, message: String) {
        val nextRule = parseStream.nextRule()

        if (nextRule != expected) {
            throw RuntimeException("$message. Found: $nextRule")
        }
    }

    private fun generateFunctionInvocation(): Symbol {
        val functionNameToken = parseStream.nextParsableToken()
        val result = symbolTable.newTemporary()

        if (functionNameToken.grammarSymbol == TokenType.ID && functionNameToken.text != null) {
            val functionSymbol = symbolTable.lookup(functionNameToken.text) as Symbol

            val functionSymbolType = checkSymbolIsFunction(functionSymbol)

            val arguments = generateArguments(functionSymbolType.params)

            irOutput.emit(FunctionCallCode(result, IrOperation.CALLR, functionSymbol, *arguments))

        } else {
            throw RuntimeException("Expected token to be function name")
        }

        return result
    }

    private fun checkSymbolIsFunction(functionSymbol: Symbol): FunctionExpressionType {
        val functionSymbolType = functionSymbol.getAttribute(Attribute.TYPE)
        if (functionSymbolType !is FunctionExpressionType) {
            throw SemanticException("${functionSymbol.name} is not a function")
        }

        return functionSymbolType
    }

    private fun generateArguments(paramTypes: Array<ExpressionType>): Array<Symbol> {
        val argumentsList: List<Symbol>

        val hasExpressionListRule = parseStream.nextRule()
        if (hasExpressionListRule == EXPRESSION_LIST_RULE) {
            argumentsList = makeExpressionList()

        } else if (hasExpressionListRule == NO_EXPRESSION_LIST_RULE) {
            argumentsList = listOf()

        } else {
            throw RuntimeException("expected rule to denote whether the function has an expression list. Found: $hasExpressionListRule")
        }

        checkArgumentCompatibility(argumentsList, paramTypes)

        return argumentsList.toTypedArray()
    }

    private fun makeExpressionList(): List<Symbol> {
        val argumentsList: MutableList<Symbol> = mutableListOf()

        var isExpressionEndedRule: Rule
        do {
            val expressionGenerator = ExpressionGenerator(symbolTable, parseStream, irOutput)
            argumentsList.add(expressionGenerator.generateStandaloneExpression())

            isExpressionEndedRule = parseStream.nextRule()
        } while (isExpressionEndedRule == EXPRESSION_LIST_TAIL_RULE)

        assertExpressionEnded(isExpressionEndedRule)

        return argumentsList
    }

    private fun assertExpressionEnded(isExpressionEndedRule: Rule) {
        if (isExpressionEndedRule != EXPRESSION_LIST_END_RULE) {
            throw RuntimeException("expected rule to denote end of expression list. Found: $isExpressionEndedRule")
        }
    }

    private fun checkArgumentCompatibility(argumentsList: List<Symbol>, paramTypes: Array<ExpressionType>) {
        val argumentTypes = argumentsList.map { it.getAttribute(Attribute.TYPE) }

        if (argumentsList.size != paramTypes.size) {
            throw SemanticException("expected params ${Arrays.toString(paramTypes)}, found $argumentTypes")
        }

        paramTypes.forEachIndexed { i, paramType ->
            if (paramType != argumentTypes[i]) {
                throw SemanticException("expected params ${Arrays.toString(paramTypes)}, found $argumentTypes")
            }
        }
    }

    private fun generateBooleanOperation(leftOperand: Symbol, op: IrOperation, rightOperand: Symbol): Symbol {
        if (!isIntegerExpressionType(leftOperand) || !isIntegerExpressionType(rightOperand)) {
            throw SemanticException("operands for ${op.name} instructions must be integers")
        }

        val result = symbolTable.newTemporary()
        result.putAttribute(Attribute.TYPE, IntegerExpressionType())

        irOutput.emit(ThreeAddressCode(result, IrOperation.ASSIGN, makeIntWithValue(1), null))

        val skipAssigningZeroLabel = symbolTable.newLabel()
        irOutput.emit(ThreeAddressCode(skipAssigningZeroLabel, op, leftOperand, rightOperand))

        irOutput.emit(ThreeAddressCode(result, IrOperation.ASSIGN, makeIntWithValue(0), null))

        irOutput.emit(skipAssigningZeroLabel)

        return result
    }

    private fun generateConst(): Symbol {
        val literalToken = parseStream.nextParsableToken()

        val const: Symbol

        if (literalToken.grammarSymbol == TokenType.INTLIT && literalToken.text != null) {
            const = makeIntWithValue(literalToken.text.toInt())

        } else if (literalToken.grammarSymbol == TokenType.FLOATLIT && literalToken.text != null) {
            const = makeFloatWithValue(literalToken.text.toFloat())

        } else {
            throw RuntimeException("expected constant (int or float) token")
        }

        return const
    }

    private fun generateLValue(): Symbol {
        val nameToken = parseStream.nextParsableToken()
        val symbol = lookupSymbol(nameToken)

        val nextRule = parseStream.nextRule()
        if (nextRule == Rule.ARRAY_INDEX_RULE) {

            var lastArraySymbol = generateMultiDimensionalArrayAccess(symbol)

            return lastArraySymbol

        } else if (nextRule == Rule.VARIABLE_VALUE_RULE) {
            return symbol

        } else {
            throw RuntimeException("expected an lvalue rule but found: $nextRule")
        }
    }

    private fun generateMultiDimensionalArrayAccess(symbol: Symbol): Symbol {
        var lastArraySymbol = symbol
        var nextArrayEndRule: Rule
        do {
            lastArraySymbol = generateArrayAccess(lastArraySymbol)
            nextArrayEndRule = parseStream.nextRule()
        } while (nextArrayEndRule == ARRAY_INDEX_RULE)

        if (nextArrayEndRule != VARIABLE_VALUE_RULE) {
            throw RuntimeException("expected end of array accesses. Found: $nextArrayEndRule")
        }
        return lastArraySymbol
    }

    private fun generateArrayAccess(nameSymbol: Symbol): Symbol {
        if (nameSymbol.getAttribute(Attribute.TYPE) !is ArrayExpressionType) {
            throw SemanticException("${nameSymbol.name} is not an array")
        }

        val indexExpressionGenerator = ExpressionGenerator(symbolTable, parseStream, irOutput)
        val index = indexExpressionGenerator.generateStandaloneExpression()

        if (!isIntegerExpressionType(index)) {
            throw SemanticException("array index must be an integer type")
        }

        val result = symbolTable.newTemporary()

        val arrayElementType = nameSymbol.getAttribute(Attribute.TYPE) as ArrayExpressionType
        result.putAttribute(Attribute.TYPE, arrayElementType.baseType)

        irOutput.emit(ThreeAddressCode(result, IrOperation.ARRAY_LOAD, nameSymbol, index))

        return result
    }

    private fun generateMULTOperation(leftOperand: Symbol, rightOperand: Symbol): Symbol {
        val resultType = getMixedOperandResultType(leftOperand, rightOperand)
        
        val result = symbolTable.newTemporary()
        result.putAttribute(Attribute.TYPE, resultType)

        irOutput.emit(ThreeAddressCode(result, IrOperation.MULT, leftOperand, rightOperand))

        return result
    }

    private fun generateDIVOperation(leftOperand: Symbol, rightOperand: Symbol): Symbol {
        val resultType = getMixedOperandResultType(leftOperand, rightOperand)

        val result = symbolTable.newTemporary()
        result.putAttribute(Attribute.TYPE, resultType)

        irOutput.emit(ThreeAddressCode(result, IrOperation.DIV, leftOperand, rightOperand))

        return result
    }

    private fun generateSUBTRACTOperation(leftOperand: Symbol, rightOperand: Symbol): Symbol {
        val resultType = getMixedOperandResultType(leftOperand, rightOperand)

        val result = symbolTable.newTemporary()
        result.putAttribute(Attribute.TYPE, resultType)

        irOutput.emit(ThreeAddressCode(result, IrOperation.SUB, rightOperand, leftOperand))

        return result
    }

    private fun generateADDOperation(leftOperand: Symbol, rightOperand: Symbol): Symbol {
        val resultType = getMixedOperandResultType(leftOperand, rightOperand)

        val result = symbolTable.newTemporary()
        result.putAttribute(Attribute.TYPE, resultType)

        irOutput.emit(ThreeAddressCode(result, IrOperation.ADD, leftOperand, rightOperand))

        return result
    }

    private fun getMixedOperandResultType(leftOperand: Symbol, rightOperand: Symbol): ExpressionType {
        val leftOperandType = leftOperand.getAttribute(Attribute.TYPE)
        val rightOperandType = rightOperand.getAttribute(Attribute.TYPE)

        val resultType: ExpressionType
        if (leftOperandType is FloatExpressionType || rightOperandType is FloatExpressionType) {
            resultType = FloatExpressionType()

        } else if (leftOperandType is IntegerExpressionType && rightOperandType is IntegerExpressionType) {
            resultType = IntegerExpressionType()

        } else {
            throw SemanticException("arguments not compatible $leftOperandType $rightOperandType")
        }

        return resultType
    }

    private fun generateOROperation(leftOperand: Symbol, rightOperand: Symbol): Symbol {
        if (!isIntegerExpressionType(leftOperand) || !isIntegerExpressionType(rightOperand)) {
            throw SemanticException("operands for OR instruction must be integers")
        }

        val result = symbolTable.newTemporary()

        result.putAttribute(Attribute.TYPE, IntegerExpressionType())

        irOutput.emit(ThreeAddressCode(result, IrOperation.OR, leftOperand, rightOperand))

        return result
    }

    private fun generateANDOperation(leftOperand: Symbol, rightOperand: Symbol): Symbol {
        if (!isIntegerExpressionType(leftOperand) || !isIntegerExpressionType(rightOperand)) {
            throw SemanticException("operands for AND instruction must be integers")
        }

        val result = symbolTable.newTemporary()

        result.putAttribute(Attribute.TYPE, IntegerExpressionType())

        irOutput.emit(ThreeAddressCode(result, IrOperation.AND, leftOperand, rightOperand))

        return result
    }

    private fun isIntegerExpressionType(leftOperand: Symbol) = leftOperand.getAttribute(Attribute.TYPE) is IntegerExpressionType

    private fun makeIntWithValue(value: Int): Symbol {
        val integer = symbolTable.newTemporary()

        integer.putAttribute(Attribute.TYPE, IntegerExpressionType())
        integer.putAttribute(Attribute.IS_LITERAL, true)
        integer.putAttribute(Attribute.LITERAL_VALUE, value)

        return integer
    }

    private fun makeFloatWithValue(value: Float): Symbol {
        val float = symbolTable.newTemporary()

        float.putAttribute(Attribute.TYPE, FloatExpressionType())
        float.putAttribute(Attribute.IS_LITERAL, true)
        float.putAttribute(Attribute.LITERAL_VALUE, value)

        return float
    }

    private fun lookupSymbol(parsableToken: ParseStream.ParsableToken): Symbol {
        if (parsableToken.grammarSymbol == TokenType.ID && parsableToken.text != null) {
            return symbolTable.lookup(parsableToken.text) as Symbol

        } else {
            throw RuntimeException("token should have TokenType ID to be parsed as symbol")
        }
    }
}

