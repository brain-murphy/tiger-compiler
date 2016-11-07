package parser.semantic.ir

open class ExpressionType()

class ArrayExpressionType(val baseType: ExpressionType, val length: Int) : ExpressionType()

class IntegerExpressionType() : ExpressionType()

class FloatExpressionType() : ExpressionType()
