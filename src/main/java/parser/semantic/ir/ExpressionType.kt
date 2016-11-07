package parser.semantic.ir

open class ExpressionType(val size: Int)

class ArrayExpressionType(val baseType: ExpressionType, val length: Int) : ExpressionType(length * baseType.size)

class FunctionExpressionType(val params: Array<ExpressionType>, val returnType: ExpressionType) : ExpressionType(4)

class IntegerExpressionType() : ExpressionType(4)

class FloatExpressionType() : ExpressionType(4)

class VoidExpressionType() : ExpressionType(0)
