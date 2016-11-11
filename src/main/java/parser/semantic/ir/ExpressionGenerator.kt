package parser.semantic.ir

import parser.semantic.ParseStream
import parser.semantic.SemanticException
import parser.semantic.symboltable.Attribute
import parser.semantic.symboltable.Symbol
import parser.semantic.symboltable.SymbolTable
import parser.syntactic.NonTerminal
import parser.syntactic.Rule
import scanner.TokenType

class ExpressionGenerator(private val symbolTable: SymbolTable,
                          private val parseStream: ParseStream,
                          private val irOutput: LinearIr) {
    private var expressionEndsToParse = 1

    fun generateAssignmentExpression(): Symbol {
        var lastResult = parseFirstTerm()

        while (expressionEndsToParse > 0) {
            lastResult = expressionParsing(lastResult)
        }

        return lastResult
    }

    private fun parseFirstTerm(): Symbol {
        if (parseStream.nextRule() == Rule.EXPRESSION_NOT_STARTING_WITH_ID_RULE) {
            return expressionParsing(null)

        } else {
            if (parseStream.nextRule() == Rule.FUNCTION_INVOCATION_RULE) {
                return generateFunctionInvocation()

            } else {
                return generateLValue()
            }
        }
    }

    /**
     * parses expressions where it is not necessary to account for it starting with a function invocation
     */
    fun generateReducedExpression(): Symbol {
        var lastResult: Symbol? = null

        while (expressionEndsToParse > 0) {
            lastResult = expressionParsing(lastResult)
        }

        return lastResult!!
    }

    private fun expressionParsing(lastValue: Symbol?): Symbol {
        val leftOperand: Symbol


        if (lastValue == null) {
            leftOperand = expressionParsing(null)
        } else {
            leftOperand = lastValue
        }

        val operationRule = parseStream.nextRule()

        if (operationRule == Rule.AND_TERM_RULE) {
            return generateANDOperation(leftOperand, expressionParsing(null))

        } else if (operationRule == Rule.OR_TERM_RULE) {
            return generateOROperation(leftOperand, expressionParsing(null))

        } else if (operationRule == Rule.EQ_TERM_RULE) {
            return generateBooleanOperation(leftOperand, IrOperation.BREQ, rightOperand = expressionParsing(null))

        } else if (operationRule == Rule.NEQ_TERM_RULE) {
            return generateBooleanOperation(leftOperand, IrOperation.BRNEQ, rightOperand = expressionParsing(null))

        } else if (operationRule == Rule.LESSER_TERM_RULE) {
            return generateBooleanOperation(leftOperand, IrOperation.BRLT, rightOperand = expressionParsing(null))

        } else if (operationRule == Rule.GREATER_TERM_RULE) {
            return generateBooleanOperation(leftOperand, IrOperation.BRGT, rightOperand = expressionParsing(null))

        } else if (operationRule == Rule.LESSEREQ_TERM_RULE) {
            return generateBooleanOperation(leftOperand, IrOperation.BRLEQ, rightOperand = expressionParsing(null))

        } else if (operationRule == Rule.GREATEREQ_TERM_RULE) {
            return generateBooleanOperation(leftOperand, IrOperation.BRGEQ, rightOperand = expressionParsing(null))

        } else if (operationRule == Rule.PLUS_TERM_RULE) {
            return generateADDOperation(leftOperand, expressionParsing(null))

        } else if (operationRule == Rule.MINUS_TERM_RULE) {
            return generateSUBTRACTOperation(leftOperand, expressionParsing(null))

        } else if (operationRule == Rule.MULT_TERM_RULE) {
            return generateMULTOperation(leftOperand, expressionParsing(null))

        } else if (operationRule == Rule.CONST_TERM_RULE) {
            return generateConst()

        } else if (operationRule == Rule.LVALUE_TERM_RULE) {
            return generateLValue()

        } else if (operationRule == Rule.PAREN_TERM_RULE) {
            expressionEndsToParse += 1
            return expressionParsing(null)

        } else if (operationRule == Rule.EXPR_END_RULE) {
            expressionEndsToParse -= 1
            return leftOperand
        }

        throw RuntimeException("could not match rule found while parsing expression")
    }



    private fun generateFunctionInvocation(): Symbol {
        val functionNameToken = parseStream.nextParsableToken()
        val result = symbolTable.newTemporary()

        if (functionNameToken.grammarSymbol == TokenType.ID && functionNameToken.text != null) {
            val functionSymbol = symbolTable.lookup(functionNameToken.text) as Symbol

            val functionSymbolType = functionSymbol.getAttribute(Attribute.TYPE)
            if (functionSymbolType !is FunctionExpressionType) {
                throw SemanticException("${functionSymbol.name} is not a function")
            }

            val arguments = generateArguments(functionSymbolType.params)

            irOutput.emit(FunctionCallCode(result, IrOperation.CALLR, functionSymbol, *arguments))
        } else {
            throw RuntimeException("Expected token to be function name")
        }

        return result
    }

    private fun generateArguments(paramTypes: Array<ExpressionType>): Array<Symbol> {
        val argumentsList: MutableList<Symbol> = mutableListOf()

        if (parseStream.nextRule() == Rule.getRuleForExpansion(NonTerminal.EXPR_LIST, NonTerminal.EXPR, NonTerminal.EXPR_LIST_TAIL)) {
            do {
                val expressionGenerator = ExpressionGenerator(symbolTable, parseStream, irOutput)
                argumentsList.add(expressionGenerator.expressionParsing(null))

            } while (parseStream.nextRule() == Rule.getRuleForExpansion(NonTerminal.EXPR_LIST_TAIL, TokenType.COMMA, NonTerminal.EXPR, NonTerminal.EXPR_LIST_TAIL))
        }

        checkArgumentCompatibility(argumentsList, paramTypes)

        return argumentsList.toTypedArray()
    }

    private fun checkArgumentCompatibility(argumentsList: MutableList<Symbol>, paramTypes: Array<ExpressionType>) {
        val argumentTypes = argumentsList.map { it.getAttribute(Attribute.TYPE) }

        if (argumentsList.size != paramTypes.size) {
            throw SemanticException("expected params $paramTypes, found $argumentTypes")
        }

        paramTypes.forEachIndexed { i, paramType ->
            if (paramType != argumentTypes[i]) {
                throw SemanticException("expected params $paramTypes, found $argumentTypes")
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

        if (parseStream.nextRule() == Rule.ARRAY_INDEX_RULE) {
            return generateArrayAccess(symbol)
        } else {
            return symbol
        }
    }

    private fun generateArrayAccess(nameSymbol: Symbol): Symbol {
        if (nameSymbol.getAttribute(Attribute.TYPE) !is ArrayExpressionType) {
            throw SemanticException("${nameSymbol.name} is not an array")
        }

        val index = generateAssignmentExpression()

        if (!isIntegerExpressionType(index)) {
            throw SemanticException("array index must be an integer type")
        }

        val result = symbolTable.newTemporary()

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
            throw SemanticException("arguments not compatible $leftOperand $rightOperand")
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

