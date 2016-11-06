package parser.symantic.ir

import parser.NonTerminal
import parser.ParseStream
import parser.Rule
import scanner.TokenType
import parser.symantic.symboltable.Symbol

import scanner.TokenType.EQ
import scanner.TokenType.ID
import scanner.TokenType.SEMI

class IrGenerationRules(val parseStream: ParseStream) {
    fun takeAction(rule: Rule): Symbol {
        if (rule == Rule.getRuleForExpansion(NonTerminal.TYPE_DECLARATION, TokenType.TYPE, ID, EQ, NonTerminal.TYPE, SEMI)) {
                        generateTypeDeclaration();
        }
    }

    fun generateTypeDeclaration() {
        parseStream
    }

    fun takeAction(nonTerminal: NonTerminal): Symbol {

    }
}
