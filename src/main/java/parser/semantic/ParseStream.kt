package parser.semantic


import parser.syntactic.GrammarSymbol
import parser.syntactic.NonTerminal
import scanner.TokenType
import parser.semantic.symboltable.Attribute
import parser.semantic.symboltable.Symbol
import parser.syntactic.Rule
import scanner.Token

import java.util.Arrays
import java.util.HashSet
import java.util.concurrent.SynchronousQueue

class ParseStream {

    data class ParsableToken(val grammarSymbol: GrammarSymbol, val text:String?)

    private val filteredTokens: MutableList<ParsableToken> = mutableListOf()
    private val filteredRules: MutableList<Rule> = mutableListOf()

    @JvmName("put")
    internal fun put(grammarSymbol: GrammarSymbol, text: String) {
        if (isUsefulForSemanticParse(grammarSymbol)) {

            try {
                filteredTokens.add(ParsableToken(grammarSymbol, text))
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    @JvmName("put")
    internal fun put(ruleExpanded: Rule) {
        if (isUsefulForSemanticParse(ruleExpanded)) {

            try {
                filteredRules.add(ruleExpanded)
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
        }
    }

    fun nextParsableToken(): ParsableToken {
        try {
            val returnedToken = filteredTokens.removeAt(0)
            println(returnedToken)
            return returnedToken
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }

    }

    fun nextRule(): Rule {
        try {
            val returnedRule = filteredRules.removeAt(0)
            println(returnedRule)
            return returnedRule
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    private fun isUsefulForSemanticParse(grammarSymbol: GrammarSymbol): Boolean {
        return TokenType.PARSABLE_TOKEN_TYPES.contains(grammarSymbol)
    }

    private fun isUsefulForSemanticParse(rule: Rule): Boolean {
        return Rule.RULES_FOR_PARSING.contains(rule)
    }
}
