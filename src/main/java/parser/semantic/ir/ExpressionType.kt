package parser.semantic.ir

open class ExpressionType()

class ArrayExpressionType(val baseType: ExpressionType, val length: Int) : ExpressionType()

class FunctionExpressionType(val params: Array<ExpressionType>, val returnType: ExpressionType) : ExpressionType()

class IntegerExpressionType() : ExpressionType()

class FloatExpressionType() : ExpressionType()

class VoidExpressionType() : ExpressionType()
