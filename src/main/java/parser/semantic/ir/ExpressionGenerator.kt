package parser.semantic.ir

import parser.semantic.ParseStream
import parser.semantic.SemanticException
import parser.semantic.symboltable.Attribute
import parser.semantic.symboltable.Symbol
import parser.semantic.symboltable.SymbolTable
import parser.syntactic.Rule

class ExpressionGenerator(val symbolTable: SymbolTable,
                          val parseStream: ParseStream,
                          val irOutput: LinearIr) {
    var currentRule = parseStream.nextRule()

    fun generateExpression(): Symbol {
        var lastResult: Symbol = parseFirstTerm()

        while (currentRule != Rule.EXPR_END_RULE) {
            lastResult = expressionParsing(lastResult)
        }

        return lastResult
    }

    fun parseFirstTerm(): Symbol {

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

        } else if (operationRule == Rule.LESSER_TERM_RULE) {

        } else if (operationRule == Rule.GREATER_TERM_RULE) {

        } else if (operationRule == Rule.LESSEREQ_TERM_RULE) {

        } else if (operationRule == Rule.GREATEREQ_TERM_RULE) {

        } else if (operationRule == Rule.PLUS_TERM_RULE) {

        } else if (operationRule == Rule.MINUS_TERM_RULE) {

        } else if (operationRule == Rule.MULT_TERM_RULE) {

        } else if (operationRule == Rule.CONST_TERM_RULE) {

        } else if (operationRule == Rule.LVALUE_TERM_RULE) {

        } else if (operationRule == Rule.PAREN_TERM_RULE) {

        } else if (operationRule == Rule.EXPR_END_RULE) {

        }
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


}

