package parser;

import scanner.TokenType;
import util.General;

import java.util.Arrays;

import static parser.NonTerminal.*;
import static scanner.TokenType.*;
import static util.General.arrayHash;

public class Rule {
    public static final Rule[] ALL_RULES =
            {
                    // first item: NT for expanding
                    new Rule(TIGER_PROGRAM, LET, DECLARATION_SEGMENT, IN, STAT_SEQ, END),
                    new Rule(DECLARATION_SEGMENT, TYPE_DECLARATION_LIST, VAR_DECLARATION_LIST, FUNCT_DECLARATION_LIST),
                    new Rule(TYPE_DECLARATION_LIST, NULL),
                    new Rule(TYPE_DECLARATION_LIST, TYPE_DECLARATION, TYPE_DECLARATION_LIST),
                    new Rule(VAR_DECLARATION_LIST, NULL),
                    new Rule(VAR_DECLARATION_LIST, VAR_DECLARATION, VAR_DECLARATION_LIST),
                    new Rule(FUNCT_DECLARATION_LIST, NULL),
                    new Rule(FUNCT_DECLARATION_LIST, FUNCT_DECLARATION, FUNCT_DECLARATION_LIST),
                    new Rule(TYPE_DECLARATION, TokenType.TYPE, ID, EQ, NonTerminal.TYPE, SEMI),
                    new Rule(NonTerminal.TYPE, TYPE_ID),
                    new Rule(NonTerminal.TYPE, ARRAY, LBRACK, INTLIT, RBRACK, OF, TYPE_ID),
                    new Rule(NonTerminal.TYPE, ID),
                    new Rule(TYPE_ID, INTTYPEID),
                    new Rule(TYPE_ID, FLOATTYPEID),
                    new Rule(VAR_DECLARATION, VAR, ID_LIST, COLON, NonTerminal.TYPE, OPTIONAL_INIT, SEMI),
                    new Rule(ID_LIST, ID, ID_LIST_TAIL),
                    new Rule(ID_LIST_TAIL, COMMA, ID_LIST),
                    new Rule(ID_LIST_TAIL, NULL),
                    new Rule(OPTIONAL_INIT, NULL),
                    new Rule(OPTIONAL_INIT, ASSIGN, CONST),
                    new Rule(FUNCT_DECLARATION, FUNC, ID, LPAREN, PARAM_LIST, RPAREN, RET_TYPE, BEGIN, STAT_SEQ, END, SEMI),
                    new Rule(PARAM_LIST, NULL),
                    new Rule(PARAM_LIST, PARAM, PARAM_LIST_TAIL),
                    new Rule(PARAM_LIST_TAIL, NULL),
                    new Rule(PARAM_LIST_TAIL, COMMA, PARAM, PARAM_LIST_TAIL),
                    new Rule(RET_TYPE, NULL),
                    new Rule(RET_TYPE, COLON, NonTerminal.TYPE),
                    new Rule(PARAM, ID, COLON, NonTerminal.TYPE),
                    new Rule(STAT_SEQ, STAT, STAT_SEQ_TAIL),
                    new Rule(STAT_SEQ_TAIL, NULL),
                    new Rule(STAT_SEQ_TAIL, STAT_SEQ),
                    new Rule(STAT, IF, EXPR, THEN, STAT_SEQ, ELSE, STAT_SEQ, ENDIF, SEMI),
                    new Rule(STAT, WHILE, EXPR, DO, STAT_SEQ, ENDDO, SEMI),
                    new Rule(STAT, FOR, ID, ASSIGN, EXPR, TO, EXPR, DO, STAT_SEQ, ENDDO, SEMI),
                    new Rule(STAT, BREAK, SEMI),
                    new Rule(STAT, RETURN, EXPR, SEMI),
                    new Rule(STAT, LET, DECLARATION_SEGMENT, IN, STAT_SEQ, END),
                    new Rule(STAT, LVALUE, STAT_ID),
                    new Rule(STAT_ID, LPAREN, EXPR_LIST, RPAREN, SEMI),

                    new Rule(STAT_ID, ASSIGN, STAT_TAIL, SEMI),
                    new Rule(STAT_TAIL, EXPR_NOT_STARTING_WITH_ID),
                    new Rule(STAT_TAIL, ID, EXPR_OR_FUNC_TAIL),

                    new Rule(EXPR_NOT_STARTING_WITH_ID, NOT_ID_EXPR_START, C_TERM_TAIL, B_TERM_TAIL, A_TERM_TAIL, EXPR_TAIL),

                    new Rule(NOT_ID_EXPR_START, CONST),
                    new Rule(NOT_ID_EXPR_START, LPAREN, EXPR, RPAREN),

                    new Rule(EXPR_OR_FUNC_TAIL, LPAREN, EXPR_LIST, RPAREN),
                    new Rule(EXPR_OR_FUNC_TAIL, LVALUE_TAIL, C_TERM_TAIL, B_TERM_TAIL, A_TERM_TAIL, EXPR_TAIL),


                    new Rule(EXPR, A_TERM, EXPR_TAIL),
                    new Rule(EXPR_TAIL, AND, A_TERM),
                    new Rule(EXPR_TAIL, OR, A_TERM),
                    new Rule(EXPR_TAIL, NULL),
                    new Rule(A_TERM, B_TERM, A_TERM_TAIL),
                    new Rule(A_TERM_TAIL, EQ, B_TERM),
                    new Rule(A_TERM_TAIL, NEQ, B_TERM),
                    new Rule(A_TERM_TAIL, LESSER, B_TERM),
                    new Rule(A_TERM_TAIL, GREATER, B_TERM),
                    new Rule(A_TERM_TAIL, LESSEREQ, B_TERM),
                    new Rule(A_TERM_TAIL, GREATEREQ, B_TERM),
                    new Rule(A_TERM_TAIL, NULL),
                    new Rule(B_TERM, C_TERM, B_TERM_TAIL),
                    new Rule(B_TERM_TAIL, PLUS, C_TERM),
                    new Rule(B_TERM_TAIL, MINUS, C_TERM),
                    new Rule(B_TERM_TAIL, NULL),
                    new Rule(C_TERM, FACTOR, C_TERM_TAIL),
                    new Rule(C_TERM_TAIL, MULT, FACTOR),
                    new Rule(C_TERM_TAIL, DIV, FACTOR),
                    new Rule(C_TERM_TAIL, NULL),
                    new Rule(FACTOR, CONST),
                    new Rule(FACTOR, LVALUE),
                    new Rule(FACTOR, LPAREN, EXPR, RPAREN),
                    new Rule(CONST, INTLIT),
                    new Rule(CONST, FLOATLIT),
                    new Rule(EXPR_LIST, NULL),
                    new Rule(EXPR_LIST, EXPR, EXPR_LIST_TAIL),
                    new Rule(EXPR_LIST_TAIL, COMMA, EXPR, EXPR_LIST_TAIL),
                    new Rule(EXPR_LIST_TAIL, NULL),
                    new Rule(LVALUE, ID, LVALUE_TAIL),
                    new Rule(LVALUE_TAIL, NULL),
                    new Rule(LVALUE_TAIL, LBRACK, EXPR, RBRACK)
            };
    private NonTerminal nonTerminalExpanded;
    private GrammarSymbol[] expansion;

    Rule(NonTerminal nonTerminalExpanded, GrammarSymbol... expansion) {
        this.nonTerminalExpanded = nonTerminalExpanded;
        this.expansion = expansion;
    }

    public NonTerminal getNonTerminalExpanded() {
        return nonTerminalExpanded;
    }

    public GrammarSymbol[] getExpansion() {
        return expansion;
    }

    @Override
    public int hashCode() {
        return nonTerminalExpanded.hashCode() + arrayHash(expansion);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Rule)) {
            return false;
        }

        Rule other = (Rule) obj;

        return getNonTerminalExpanded().equals(other.getNonTerminalExpanded()) &&
                Arrays.equals(getExpansion(), other.getExpansion());
    }

    @Override
    public String toString() {
        return nonTerminalExpanded.name() + " := " + General.expansionToString(expansion);
    }
}
