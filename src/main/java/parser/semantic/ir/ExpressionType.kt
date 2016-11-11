package parser.semantic.ir

import java.util.*

abstract class ExpressionType(val size: Int)

class ArrayExpressionType(val baseType: ExpressionType, val length: Int) : ExpressionType(length * baseType.size) {
    override fun equals(other: Any?): Boolean {
        if (other !is ArrayExpressionType) {
            return false
        }

        return baseType == other.baseType
    }

    override fun hashCode(): Int {
        return baseType.hashCode() + 31
    }
}

class FunctionExpressionType(val params: Array<ExpressionType>, val returnType: ExpressionType) : ExpressionType(4) {
    override fun equals(other: Any?): Boolean {
        if (other !is FunctionExpressionType) {
            return false
        }

        if (returnType != other.returnType) {
            return false
        }

        params.forEachIndexed { i, paramType ->
            if (paramType != other.params[i]) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int{
        var result = Arrays.hashCode(params)
        result = 31 * result + returnType.hashCode()
        return result
    }
}

class IntegerExpressionType() : ExpressionType(4) {
    override fun equals(other: Any?): Boolean {
        return other is IntegerExpressionType
    }

    override fun hashCode(): Int {
        return 1
    }
}

class FloatExpressionType() : ExpressionType(4) {
    override fun equals(other: Any?): Boolean {
        return other is FloatExpressionType
    }

    override fun hashCode(): Int {
        return 2
    }
}

class VoidExpressionType() : ExpressionType(0) {
    override fun equals(other: Any?): Boolean {
        return other is VoidExpressionType
    }

    override fun hashCode(): Int {
        return 0
    }
}

class StringExpressionType() : ExpressionType(4) {

}
