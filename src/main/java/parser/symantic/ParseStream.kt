package parser.symantic


import parser.syntactic.GrammarSymbol
import parser.syntactic.NonTerminal
import scanner.TokenType
import parser.symantic.symboltable.Attribute
import parser.symantic.symboltable.Symbol
import parser.syntactic.Rule

import java.util.Arrays
import java.util.HashSet
import java.util.concurrent.SynchronousQueue

class ParseStream {

    data class ParsableToken(val grammarSymbol: GrammarSymbol, val text:String?)

    private val filteredTokens = SynchronousQueue<ParsableToken>()
    private val filteredRules = SynchronousQueue<Rule>()

    @JvmName("put")
    internal fun put(grammarSymbol: GrammarSymbol, text: String) {
        if (isUsefulForSemanticParse(grammarSymbol)) {

            try {
                filteredTokens.put(ParsableToken(grammarSymbol, text))
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    internal fun put(ruleExpanded: Rule) {
        if (isUsefulForSemanticParse(ruleExpanded)) {

            try {
                filteredRules.put(ruleExpanded)
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
        }
    }

    fun nextParsableToken(): ParsableToken {
        try {
            return filteredTokens.take()
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }

    }

    fun nextRule(): Rule {
        try {
            return filteredRules.take()
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    private fun isUsefulForSemanticParse(grammarSymbol: GrammarSymbol): Boolean {
        return true //TODO
    }

    private fun isUsefulForSemanticParse(rule: Rule): Boolean {
        return true //TODO
    }
}
