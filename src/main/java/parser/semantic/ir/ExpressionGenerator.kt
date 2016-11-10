package parser.semantic.ir

import com.sun.corba.se.impl.naming.pcosnaming.ServantManagerImpl
import com.sun.deploy.security.ValidationState
import parser.semantic.ParseStream
import parser.semantic.SemanticException
import parser.semantic.symboltable.Attribute
import parser.semantic.symboltable.Symbol
import parser.semantic.symboltable.SymbolTable
import parser.syntactic.Rule
import scanner.Token
import scanner.TokenType

class ExpressionGenerator(val symbolTable: SymbolTable,
                          val parseStream: ParseStream,
                          val irOutput: LinearIr) {
    var currentRule = parseStream.nextRule()
    var expressionEndsToParse = 1

    fun generateAssignmentExpression(): Symbol {
        var lastResult: Symbol = parseFirstTerm()

        while (expressionEndsToParse > 0) {
            lastResult = expressionParsing(lastResult)
        }

        return lastResult
    }

    fun parseFirstTerm(): Symbol {
        if (parseStream.nextRule() == Rule.EXPRESSION_NOT_STARTING_WITH_ID_RULE) {
            expressionParsing(null)
        } else {

        }
    }

    fun expressionParsing(lastValue: Symbol?): Symbol {
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
            return generateEqualsOperation(leftOperand, expressionParsing(null))

        } else if (operationRule == Rule.NEQ_TERM_RULE) {
            return generateNEQOperation(leftOperand, expressionParsing(null))

        } else if (operationRule == Rule.LESSER_TERM_RULE) {

        } else if (operationRule == Rule.GREATER_TERM_RULE) {

        } else if (operationRule == Rule.LESSEREQ_TERM_RULE) {

        } else if (operationRule == Rule.GREATEREQ_TERM_RULE) {

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
            throw SemanticException("array index must be an integer type");
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

    private fun generateNEQOperation(leftOperand: Symbol, rightOperand: Symbol): Symbol {

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

    fun generateANDOperation(leftOperand: Symbol, rightOperand: Symbol): Symbol {
        if (!isIntegerExpressionType(leftOperand) || !isIntegerExpressionType(rightOperand)) {
            throw SemanticException("operands for AND instruction must be integers")
        }

        val result = symbolTable.newTemporary()

        result.putAttribute(Attribute.TYPE, IntegerExpressionType())

        irOutput.emit(ThreeAddressCode(result, IrOperation.AND, leftOperand, rightOperand))

        return result
    }

    private fun isIntegerExpressionType(leftOperand: Symbol) = leftOperand.getAttribute(Attribute.TYPE) is IntegerExpressionType

    fun generateEqualsOperation(leftOperand: Symbol, rightOperand: Symbol): Symbol {
        if (!isIntegerExpressionType(leftOperand) || !isIntegerExpressionType(rightOperand)) {
            throw SemanticException("operands for EQ instructions must be integers")
        }

        val difference = symbolTable.newTemporary()
        difference.putAttribute(Attribute.TYPE, IntegerExpressionType())
        irOutput.emit(ThreeAddressCode(difference, IrOperation.SUB, leftOperand, rightOperand))

        

        val skipAssigningZeroLabel = symbolTable.newLabel()
        ir
    }

    fun makeIntWithValue(value: Int): Symbol {
        val integer = symbolTable.newTemporary()

        integer.putAttribute(Attribute.TYPE, IntegerExpressionType())
        integer.putAttribute(Attribute.IS_LITERAL, true)
        integer.putAttribute(Attribute.LITERAL_VALUE, value)

        return integer
    }

    fun makeFloatWithValue(value: Float): Symbol {
        val float = symbolTable.newTemporary()

        float.putAttribute(Attribute.TYPE, FloatExpressionType())
        float.putAttribute(Attribute.IS_LITERAL, true)
        float.putAttribute(Attribute.LITERAL_VALUE, value)

        return float
    }

    fun lookupSymbol(parsableToken: ParseStream.ParsableToken): Symbol {

        if (parsableToken.grammarSymbol == TokenType.ID && parsableToken.text != null) {
            return symbolTable.lookup(parsableToken.text)
        } else {
            throw RuntimeException("token should have TokenType ID to be parsed as symbol")
        }

    }


}

