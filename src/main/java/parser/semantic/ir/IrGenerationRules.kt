package parser.semantic.ir

import parser.syntactic.NonTerminal
import parser.semantic.ParseStream
import parser.semantic.symboltable.Attribute
import parser.syntactic.Rule
import scanner.TokenType
import parser.semantic.symboltable.Symbol
import parser.semantic.symboltable.SymbolTable

import scanner.TokenType.*;

class IrGenerationRules(val parseStream: ParseStream,
                        var currentSymbolTable: SymbolTable) {
    fun takeAction(rule: Rule): Symbol {
        if (rule == Rule.getRuleForExpansion(NonTerminal.TYPE_DECLARATION, TokenType.TYPE, ID, EQ, NonTerminal.TYPE, SEMI)) {
            generateTypeDeclaration()
        } else if (rule == Rule.getRuleForExpansion(NonTerminal.VAR_DECLARATION, VAR, NonTerminal.ID_LIST, COLON, NonTerminal.TYPE, NonTerminal.OPTIONAL_INIT, SEMI)) {
            generateVarDeclaration()
        }
    }

    fun generateTypeDeclaration() {
        val newType = toSymbol(parseStream.nextParsableToken())

        val type = calculateExpressionType(parseStream.nextRule())

        newType.putAttribute(Attribute.TYPE, type)

        currentSymbolTable.insert(newType)
    }

    fun calculateExpressionType(parseRuleUsed: Rule): ExpressionType {
        if (parseRuleUsed == Rule.getRuleForExpansion(NonTerminal.TYPE, NonTerminal.TYPE_ID)) {
            return calculateBasicType()

        } else if (parseRuleUsed == Rule.getRuleForExpansion(NonTerminal.TYPE, ARRAY, LBRACK, INTLIT, RBRACK, OF, NonTerminal.TYPE_ID)) {
            return calculateArrayType()

        } else if (parseRuleUsed == Rule.getRuleForExpansion(NonTerminal.TYPE, ID)) {
            return calculateUserDefinedType()
        } else {
            throw RuntimeException("could not recognize rule for parsing type")
        }
    }


    fun calculateBasicType(): ExpressionType {
        val basicType = parseStream.nextParsableToken().grammarSymbol as TokenType

        if (basicType == TokenType.INTTYPEID) {
            return IntegerExpressionType()
        } else if (basicType == TokenType.FLOATTYPEID) {
            return FloatExpressionType()
        } else {
            throw RuntimeException("could not recognize basic type")
        }
    }

    fun calculateArrayType(): ExpressionType {
        val lengthToken = parseStream.nextParsableToken()

        val arrayLength: Int
        if (lengthToken.grammarSymbol == TokenType.INTLIT && lengthToken.text != null) {
            arrayLength = lengthToken.text.toInt()
        } else {
            throw RuntimeException("array length token should be int literal but could not be parsed")
        }

        val baseType = calculateExpressionType(parseStream.nextRule())

        return ArrayExpressionType(baseType, arrayLength)
    }

    fun calculateUserDefinedType(): ExpressionType {
        val typeToken = parseStream.nextParsableToken()

        if (typeToken.grammarSymbol == ID && typeToken.text != null) {
            val typeSymbol = currentSymbolTable.lookup(typeToken.text)

            return typeSymbol.getAttribute(Attribute.TYPE) as ExpressionType
        } else {
            throw RuntimeException("token should have TokenType ID to be parsed as symbol")
        }
    }

    fun generateVarDeclaration() {
        val newVars = calculateVarList()

        val type = calculateExpressionType(parseStream.nextRule())

        newVars.forEach {
            it.putAttribute(Attribute.TYPE, type)
            currentSymbolTable.insert(it)
        }

        if (parseStream.nextRule() == Rule.getRuleForExpansion(NonTerminal.OPTIONAL_INIT, ASSIGN, NonTerminal.CONST)) {

        }
    }

    fun calculateVarList(): List<Symbol> {
        val varList: MutableList<Symbol> = mutableListOf()

        do {
            val idToken = parseStream.nextParsableToken()
            if (idToken.grammarSymbol == ID && idToken.text != null) {
                varList.add(Symbol(idToken.text))
            }
        } while (parseStream.nextRule() == Rule.getRuleForExpansion(NonTerminal.ID_LIST_TAIL, COMMA, NonTerminal.ID_LIST))

        return varList
    }

    fun generateAssignment(symbolToAssign: Symbol) {
        val expressionValue = generateExpression()

    }

    fun generateExpression(): Symbol {

    }

    fun emit(vararg code: ThreeAddressCode) {
        ThreeAddressCode()
    }

    fun toSymbol(parsableToken: ParseStream.ParsableToken): Symbol {

        if (parsableToken.grammarSymbol == ID && parsableToken.text != null) {
            return Symbol(parsableToken.text)
        } else {
            throw RuntimeException("token should have TokenType ID to be parsed as symbol")
        }

    }
}
