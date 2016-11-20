package parser.semantic.ir

import parser.syntactic.NonTerminal
import parser.semantic.ParseStream
import parser.semantic.SemanticException
import parser.semantic.symboltable.Attribute
import parser.semantic.symboltable.HashSymbolTable
import parser.syntactic.Rule
import scanner.TokenType
import parser.semantic.symboltable.Symbol
import parser.semantic.symboltable.SymbolTable
import parser.syntactic.Rule.*

import scanner.TokenType.*
import java.util.*

class IrGenerator(private val parseStream: ParseStream) {

    private var currentSymbolTable: SymbolTable = HashSymbolTable()
    private val ir = LinearIr()

    private val loopEndStack = ArrayDeque<Label>()
    private val functionParseStack = ArrayDeque<Symbol>()

    private var letCount = 1

    fun run()  {
        while(letCount > 0) {
            takeSemanticAction()
        }
    }

    fun getIR(): LinearIr {
        return ir
    }

    fun getSymbolTable(): SymbolTable {
        return currentSymbolTable
    }

    private fun takeSemanticAction() {
        val rule = parseStream.nextRule()

        if (rule == TYPE_DECLARATION_RULE) {
            generateTypeDeclaration()
        } else if (rule == VAR_DECLARATION_RULE) {
            generateVarDeclaration()
        } else if (rule == FUNCTION_DECLARATION_RULE) {
            generateFunctionDeclaration()
        } else if (rule == STAT_SEQUENCE_RULE) {
            generateStatementSequence()
        } else if (rule == LET_END_RULE) {
            if (letCount > 1) {
                currentSymbolTable = currentSymbolTable.parentScope
            }
            letCount -= 1
        } else if (rule == MAIN_RULE) {
            val mainSymbol = currentSymbolTable.newLabel("main")
            ir.emit(mainSymbol)
        }
    }

    fun generateTypeDeclaration() {
        val newType = nextIdAsSymbol()

        val type = calculateType(parseStream.nextRule())

        newType.putAttribute(Attribute.TYPE, type)

        currentSymbolTable.insert(newType)
    }

    fun calculateType(parseRuleUsed: Rule): ExpressionType {
        if (parseRuleUsed == BASE_TYPE_RULE) {
            return calculateBasicType()

        } else if (parseRuleUsed == ARRAY_TYPE_RULE) {
            return calculateArrayType()

        } else if (parseRuleUsed == USER_DEFINED_TYPE_RULE) {
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

        val baseType = calculateType(parseStream.nextRule())

        return ArrayExpressionType(baseType, arrayLength)
    }

    fun calculateUserDefinedType(): ExpressionType {
        val typeToken = parseStream.nextParsableToken()

        if (typeToken.grammarSymbol == ID && typeToken.text != null) {
            val typeSymbol = currentSymbolTable.lookup(typeToken.text) as Symbol

            return typeSymbol.getAttribute(Attribute.TYPE) as ExpressionType
        } else {
            throw RuntimeException("token should have TokenType ID to be parsed as symbol")
        }
    }

    fun generateVarDeclaration() {
        val newVars = calculateVarList()

        val type = calculateType(parseStream.nextRule())

        newVars.forEach {
            it.putAttribute(Attribute.TYPE, type)
            currentSymbolTable.insert(it)
        }

        if (hasOptionalInit()) {
            val initialValue = generateConst()

            newVars.forEach {
                generateOptionalInit(it, initialValue)
            }
        }
    }

    private fun hasOptionalInit() = parseStream.nextRule() == OPTIONAL_INIT_RULE

    fun calculateVarList(): List<Symbol> {
        val varList: MutableList<Symbol> = mutableListOf()

        do {
            val idToken = parseStream.nextParsableToken()
            if (idToken.grammarSymbol == ID && idToken.text != null) {
                varList.add(Symbol(idToken.text))
            }
        } while (parseStream.nextRule() == VAR_LIST_TAIL_RULE)

        return varList
    }

    fun generateOptionalInit(symbolToAssign: Symbol, valueAssigned: Symbol) {
        if (valueAssigned.getAttribute(Attribute.TYPE) == symbolToAssign.getAttribute(Attribute.TYPE)) {

            ir.emit(ThreeAddressCode(symbolToAssign, IrOperation.ASSIGN, valueAssigned, null))
        }
    }

    fun generateFunctionDeclaration() {
        val functionSymbol = nextIdAsSymbol()
        currentSymbolTable.insert(functionSymbol)

        calculateFunctionAttributes(functionSymbol)

        val functionStartLabel = currentSymbolTable.newLabel(functionSymbol.name)
        functionSymbol.putAttribute(Attribute.FUNCTION_START_LABEL, functionStartLabel)
        ir.emit(functionStartLabel)

        currentSymbolTable = currentSymbolTable.createChildScope(functionSymbol)
        functionParseStack.push(functionSymbol)

        addArgumentSymbols(functionSymbol)

        // Deliberately skip STAT_SEQUENCE_START_RULE
        parseStream.nextRule()

        generateStatementSequence()

        ir.emit(ThreeAddressCode(null, IrOperation.RETURN, null, null))

        functionParseStack.pop()
        currentSymbolTable = currentSymbolTable.parentScope
    }

    private fun addArgumentSymbols(functionSymbol: Symbol) {
        val paramsNames = functionSymbol.getAttribute(Attribute.FUNCTION_PARAM_NAMES) as Array<String>

        val paramTypes = (functionSymbol.getAttribute(Attribute.TYPE) as FunctionExpressionType).params

        paramsNames.forEachIndexed { i, paramName ->
            val argument = Symbol(paramName)
            argument.putAttribute(Attribute.TYPE, paramTypes[i])

            currentSymbolTable.insert(argument)
        }
    }

    fun calculateFunctionAttributes(functionSymbol: Symbol) {
        val params = calculateParamTypes()

        val returnType: ExpressionType

        if (parseStream.nextRule() == RETURN_TYPE_RULE) {
            returnType = calculateType(parseStream.nextRule())

        } else {
            returnType = VoidExpressionType()
        }

        functionSymbol.putAttribute(Attribute.TYPE, FunctionExpressionType(params.values.toTypedArray(), returnType))
        functionSymbol.putAttribute(Attribute.FUNCTION_PARAM_NAMES, params.keys.toTypedArray())
    }

    fun calculateParamTypes(): Map<String, ExpressionType> {
        val params: MutableMap<String, ExpressionType> = mutableMapOf()
        if (parseStream.nextRule() == PARAM_LIST_RULE) {
            do {
                val paramName = parseStream.nextParsableToken().text
                val paramType = calculateType(parseStream.nextRule())

                params.put(paramName!!, paramType)

            } while (parseStream.nextRule() == PARAM_LIST_TAIL_RULE)
        }

        return params
    }

    fun generateStatementSequence() {
        var statementParseRule: Rule

        do {
            statementParseRule = parseStream.nextRule()

            if (statementParseRule == ID_STATMENT_START_RULE) {
                generateStatementStartingWithId()

            } else if (statementParseRule == IF_STATMENT_RULE) {
                generateIfStatement()

            } else if (statementParseRule == WHILE_STATEMENT_RULE) {
                generateWhileStatement()

            } else if (statementParseRule == FOR_STATEMENT_RULE) {
                generateForStatement()

            } else if (statementParseRule == BREAK_STATEMENT_RULE) {
                generateBreakStatement()

            } else if (statementParseRule == RETURN_STATEMENT_RULE) {
                generateReturnStatement()

            } else if (statementParseRule == LET_STATEMENT_RULE) {
                generateLetStatement()
            }
        } while (statementParseRule == STAT_SEQUENCE_TAIL_RULE)
    }

    private fun generateLetStatement() {
        letCount += 1
        val newScopeLabel = currentSymbolTable.newLabel()

        ir.emit(newScopeLabel)

        currentSymbolTable = currentSymbolTable.createChildScope(newScopeLabel)
    }

    private fun generateReturnStatement() {
        val expressionGenerator = ExpressionGenerator(currentSymbolTable, parseStream, ir)
        val expressionReturned = expressionGenerator.generateReducedExpression()

        checkReturnType(expressionReturned)

        ir.emit(ThreeAddressCode(expressionReturned, IrOperation.RETURN, null, null))
    }

    private fun checkReturnType(expressionReturned: Symbol) {
        val functionType = functionParseStack.peek().getAttribute(Attribute.TYPE) as FunctionExpressionType
        if (functionType.returnType != expressionReturned.getAttribute(Attribute.TYPE)) {

            if (functionType.returnType is VoidExpressionType) {
                throw SemanticException("void functions cannot have return statements")

            } else {
                throw SemanticException("expression returned needs to be type ${functionType.returnType}")
            }
        }
    }

    private fun generateBreakStatement() {
        if (loopEndStack.size == 0) {
            throw SemanticException("break statement must be enclosed by a loop")
        }

        val zeroSymbol = makeIntWithValue(0)

        val loopEndLabel = loopEndStack.pop()
        ir.emit(ThreeAddressCode(loopEndLabel, IrOperation.BREQ, zeroSymbol, zeroSymbol))
    }

    private fun generateForStatement() {
        val startOfLoopLabel = currentSymbolTable.newLabel()
        currentSymbolTable = currentSymbolTable.createChildScope(startOfLoopLabel)

        val indexVariable = generateIndexInitialization()

        val endExpression = generateEndIndex()

        ir.emit(startOfLoopLabel)

        val endOfLoopLabel = currentSymbolTable.newLabel()
        loopEndStack.push(endOfLoopLabel)

        ir.emit(ThreeAddressCode(endOfLoopLabel, IrOperation.BRGT, indexVariable, endExpression))

        generateStatementSequence()

        ir.emit(ThreeAddressCode(indexVariable, IrOperation.ADD, indexVariable, makeIntWithValue(1)))

        ir.emit(ThreeAddressCode(startOfLoopLabel, IrOperation.BREQ, endExpression, endExpression))

        currentSymbolTable = currentSymbolTable.parentScope

        ir.emit(endOfLoopLabel)
    }

    private fun generateEndIndex(): Symbol {
        val endExpressionGenerator = ExpressionGenerator(currentSymbolTable, parseStream, ir)
        val endExpression = endExpressionGenerator.generateReducedExpression()

        if (!isIntegerExpressionType(endExpression)) {
            throw SemanticException("end condition of for loop must have integer expression type")
        }
        return endExpression
    }

    private fun generateIndexInitialization(): Symbol {
        val conditionalVariable = nextIdAsSymbol()
        val initializationExpressionGenerator = ExpressionGenerator(currentSymbolTable, parseStream, ir)
        val initializationExpression = initializationExpressionGenerator.generateReducedExpression()

        if (!isIntegerExpressionType(initializationExpression)) {
            throw SemanticException("initialization of for loop must have integer expression type")
        }

        ir.emit(ThreeAddressCode(conditionalVariable, IrOperation.ASSIGN, initializationExpression, null))
        return conditionalVariable
    }

    private fun generateWhileStatement() {
        val zeroSymbol = makeIntWithValue(0)

        val startOfLoopLabel = currentSymbolTable.newLabel()
        currentSymbolTable = currentSymbolTable.createChildScope(startOfLoopLabel)
        ir.emit(startOfLoopLabel)

        val expressionGenerator = ExpressionGenerator(currentSymbolTable, parseStream, ir)
        val conditionalExpression = expressionGenerator.generateReducedExpression()

        val endOfLoopLabel = currentSymbolTable.newLabel()
        loopEndStack.push(endOfLoopLabel)

        ir.emit(ThreeAddressCode(endOfLoopLabel, IrOperation.BREQ, conditionalExpression, zeroSymbol))

        generateStatementSequence()

        ir.emit(ThreeAddressCode(startOfLoopLabel, IrOperation.BREQ, zeroSymbol, zeroSymbol))

        currentSymbolTable = currentSymbolTable.parentScope
        ir.emit(endOfLoopLabel)
    }

    private fun generateIfStatement() {
        val ifTailRule = parseStream.nextRule()
        if (ifTailRule == NO_ELSE_RULE) {
            generateIfWithoutElse();

        } else if (ifTailRule == ELSE_RULE) {
            generateIfAndElse();
        } else {
            throw RuntimeException("expected either 'else' clause, or and endif.")
        }
    }

    private fun generateIfAndElse() {
        val conditionalExpression = generateIfCondititionalExpression()

        val startElseLabel = currentSymbolTable.newLabel()
        val endElseLabel = currentSymbolTable.newLabel()

        val zeroSymbol = makeIntWithValue(0)
        val branchToElseCode = ThreeAddressCode(startElseLabel, IrOperation.BREQ, zeroSymbol, conditionalExpression)
        ir.emit(branchToElseCode)

        currentSymbolTable = currentSymbolTable.createChildScope(endElseLabel)
        generateStatementSequence()
        currentSymbolTable = currentSymbolTable.parentScope

        val skipElseCode = ThreeAddressCode(endElseLabel, IrOperation.BREQ, zeroSymbol, zeroSymbol)
        ir.emit(skipElseCode)


        ir.emit(startElseLabel)

        currentSymbolTable = currentSymbolTable.createChildScope(startElseLabel)
        generateStatementSequence()
        currentSymbolTable = currentSymbolTable.parentScope

        ir.emit(endElseLabel)
    }

    private fun generateIfWithoutElse() {
        val conditionalExpression = generateIfCondititionalExpression()

        val endIfLabel = currentSymbolTable.newLabel()

        val zeroSymbol = makeIntWithValue(0)

        ir.emit(ThreeAddressCode(endIfLabel, IrOperation.BREQ, zeroSymbol, conditionalExpression))

        currentSymbolTable = currentSymbolTable.createChildScope(endIfLabel)
        generateStatementSequence()
        currentSymbolTable = currentSymbolTable.parentScope

        ir.emit(endIfLabel)
    }

    private fun generateIfCondititionalExpression(): Symbol {
        val expressionGenerator = ExpressionGenerator(currentSymbolTable, parseStream, ir)
        val conditionalExpression = expressionGenerator.generateReducedExpression()

        if (!isIntegerExpressionType(conditionalExpression)) {
            throw SemanticException("conditional expression must have integer type")
        }

        return conditionalExpression
    }

    fun generateStatementStartingWithId() {
        val idStatementRule = parseStream.nextRule()
        if (idStatementRule == Rule.ARRAY_INDEX_RULE) {
            generateArrayStatement()
        } else if (idStatementRule == VARIABLE_VALUE_RULE) {

            val baseSymbol = lookupNextId()
            val functionCallOrAssignment = parseStream.nextRule()

            if (functionCallOrAssignment == Rule.FUNCTION_STATEMENT_START_RULE) {
                generateFunctionCallAsStatement(baseSymbol)

            } else if (functionCallOrAssignment == ASSIGNMENT_STATEMENT_RULE) {
                generateAssignmentStatement(baseSymbol)

            } else {
                throw RuntimeException("expected rule to parse function or assignment statement, but found $functionCallOrAssignment")
            }
        } else {
            throw RuntimeException("expected either lvalue or array statement")
        }
    }

    private fun generateArrayStatement() {
        val arraySymbol = lookupNextId()

        if (arraySymbol.getAttribute(Attribute.TYPE) !is ArrayExpressionType) {
            throw SemanticException("symbol ${arraySymbol.name} is not an array")
        }

        val arrayIndex = generateArrayIndex()

        if (parseStream.nextRule() == Rule.FUNCTION_INVOCATION_RULE) {
            //TODO calling functions from an array access array[i]();

        } else {
            val expressionGenerator = ExpressionGenerator(currentSymbolTable, parseStream, ir)

            val rightSideResult = expressionGenerator.generateAssignmentExpression()

            ir.emit(ThreeAddressCode(arraySymbol, IrOperation.ARRAY_STORE, arrayIndex, rightSideResult))
        }
    }

    fun generateFunctionCallAsStatement(function: Symbol) {

        val parameterTypes = (function.getAttribute(Attribute.TYPE) as FunctionExpressionType).params

        val arguments = generateArguments(parameterTypes)

        ir.emit(FunctionCallCode(IrOperation.CALL, function, *arguments))
    }

    fun generateArguments(paramTypes: Array<ExpressionType>): Array<Symbol> {
        val argumentsList: MutableList<Symbol> = mutableListOf()

        if (parseStream.nextRule() == EXPRESSION_LIST_RULE) {
            do {
                val expressionGenerator = ExpressionGenerator(currentSymbolTable, parseStream, ir)
                argumentsList.add(expressionGenerator.generateReducedExpression())

            } while (parseStream.nextRule() == EXPRESSION_LIST_TAIL_RULE)
        }

        checkArgumentCompatibility(argumentsList, paramTypes)

        return argumentsList.toTypedArray()
    }

    private fun checkArgumentCompatibility(argumentsList: MutableList<Symbol>, paramTypes: Array<ExpressionType>) {
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

    fun generateAssignmentStatement(leftSideVariable: Symbol) {
        val expressionGenerator = ExpressionGenerator(currentSymbolTable, parseStream, ir)

        val rightSideResult = expressionGenerator.generateAssignmentExpression()

        ir.emit(ThreeAddressCode(leftSideVariable, IrOperation.ASSIGN, rightSideResult, null))
    }

    private fun generateArrayIndex(): Symbol {
        val expressionGenerator = ExpressionGenerator(currentSymbolTable, parseStream, ir)

        val expressionValue = expressionGenerator.generateReducedExpression()
        if (!isIntegerExpressionType(expressionValue)) {
            throw SemanticException("array index must be integer expression")
        }

        return expressionValue
    }

    private fun isIntegerExpressionType(leftOperand: Symbol) = leftOperand.getAttribute(Attribute.TYPE) is IntegerExpressionType

    fun generateConst(): Symbol {
        val literalToken = parseStream.nextParsableToken()

        val symbol = currentSymbolTable.newTemporary()

        if (literalToken.grammarSymbol == TokenType.INTLIT && literalToken.text != null) {
            symbol.putAttribute(Attribute.TYPE, IntegerExpressionType())
            symbol.putAttribute(Attribute.LITERAL_VALUE, literalToken.text.toInt())

        } else if (literalToken.grammarSymbol == TokenType.FLOATLIT && literalToken.text != null) {
            symbol.putAttribute(Attribute.TYPE, FloatExpressionType())
            symbol.putAttribute(Attribute.LITERAL_VALUE, literalToken.text.toFloat())
        } else {
            throw RuntimeException("const can be only int or float type")
        }

        symbol.putAttribute(Attribute.IS_LITERAL, true)

        return symbol
    }

    fun lookupNextId(): Symbol {
        val parsableToken = parseStream.nextParsableToken()

        if (parsableToken.grammarSymbol == ID && parsableToken.text != null) {
            return currentSymbolTable.lookup(parsableToken.text) as Symbol
        } else {
            throw RuntimeException("token should have TokenType ID to be parsed as symbol")
        }
    }

    fun nextIdAsSymbol(): Symbol {
        val parsableToken = parseStream.nextParsableToken()

        if (parsableToken.grammarSymbol == ID && parsableToken.text != null) {
            return Symbol(parsableToken.text)
        } else {
            throw RuntimeException("token should have TokenType ID to be parsed as symbol")
        }
    }

    private fun makeIntWithValue(value: Int): Symbol {
        val integer = currentSymbolTable.newTemporary()

        integer.putAttribute(Attribute.TYPE, IntegerExpressionType())
        integer.putAttribute(Attribute.IS_LITERAL, true)
        integer.putAttribute(Attribute.LITERAL_VALUE, value)

        return integer
    }
}
