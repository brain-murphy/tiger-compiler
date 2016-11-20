package parser.syntactic;

import scanner.TokenType;
import util.General;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static parser.syntactic.NonTerminal.*;
import static scanner.TokenType.*;
import static util.General.arrayHash;

public class Rule {
    public static final Rule[] ALL_RULES =
            {
                    // first item: NT for expanding
                    new Rule(TIGER_PROGRAM, LET, DECLARATION_SEGMENT, IN, MAIN, LET_END),
                    new Rule(MAIN, STAT_SEQ),
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
                    new Rule(STAT_SEQ_TAIL, STAT, STAT_SEQ_TAIL),
                    new Rule(STAT, IF, EXPR, THEN, STAT_SEQ, IF_STATEMENT_TAIL),
                    new Rule(IF_STATEMENT_TAIL, ELSE, STAT_SEQ, ENDIF, SEMI),
                    new Rule(IF_STATEMENT_TAIL, ENDIF),
                    new Rule(STAT, WHILE, EXPR, DO, STAT_SEQ, ENDDO, SEMI),
                    new Rule(STAT, FOR, ID, ASSIGN, EXPR, TO, EXPR, DO, STAT_SEQ, ENDDO, SEMI),
                    new Rule(STAT, BREAK, SEMI),
                    new Rule(STAT, RETURN, EXPR, SEMI),
                    new Rule(STAT, LET, DECLARATION_SEGMENT, IN, STAT_SEQ, LET_END),
                    new Rule(LET_END, END),
                    new Rule(STAT, LVALUE, STAT_ID),
                    new Rule(STAT_ID, LPAREN, EXPR_LIST, RPAREN, SEMI),

                    new Rule(STAT_ID, ASSIGN, STAT_TAIL, SEMI),
                    new Rule(STAT_TAIL, EXPR_NOT_STARTING_WITH_ID),
                    new Rule(STAT_TAIL, ID, EXPR_OR_FUNC_TAIL),

                    new Rule(EXPR_NOT_STARTING_WITH_ID, NOT_ID_EXPR_START, C_TERM_TAIL, B_TERM_TAIL, A_TERM_TAIL, EXPR_TAIL),

                    new Rule(NOT_ID_EXPR_START, CONST),
                    new Rule(NOT_ID_EXPR_START, LPAREN, EXPR, RPAREN),

                    new Rule(EXPR_OR_FUNC_TAIL, LPAREN, EXPR_LIST, RPAREN, EXPR_TAIL),
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

    // Keep in hashSet for faster lookup
    private static Set<Rule> ruleSet;
    public static Set<Rule> RULES_FOR_PARSING;
    static {
        ruleSet = new HashSet<>(ALL_RULES.length);
        Collections.addAll(ruleSet, ALL_RULES);
    }

    private static Rule getRuleForExpansion(NonTerminal nonTerminalExpanded, GrammarSymbol... expansion) {
        Rule rule = new Rule(nonTerminalExpanded, expansion);

        if (ruleSet.contains(rule)) {
            return rule;
        } else {
            throw new RuntimeException("cannot find rule: " + rule.toString());
        }
    }

    public static final Rule TYPE_DECLARATION_RULE = getRuleForExpansion(TYPE_DECLARATION, TokenType.TYPE, ID, EQ, NonTerminal.TYPE, SEMI);
    public static final Rule BASE_TYPE_RULE = getRuleForExpansion(NonTerminal.TYPE, TYPE_ID);
    public static final Rule ARRAY_TYPE_RULE = getRuleForExpansion(NonTerminal.TYPE, ARRAY, LBRACK, INTLIT, RBRACK, OF, TYPE_ID);
    public static final Rule USER_DEFINED_TYPE_RULE = getRuleForExpansion(NonTerminal.TYPE, ID);

    public static final Rule VAR_DECLARATION_RULE = getRuleForExpansion(NonTerminal.VAR_DECLARATION, VAR, NonTerminal.ID_LIST, COLON, NonTerminal.TYPE, NonTerminal.OPTIONAL_INIT, SEMI);
    public static final Rule VAR_LIST_TAIL_RULE = getRuleForExpansion(NonTerminal.ID_LIST_TAIL, COMMA, NonTerminal.ID_LIST);
    public static final Rule VAR_LIST_END_RULE = getRuleForExpansion(ID_LIST_TAIL, NULL);
    public static final Rule OPTIONAL_INIT_RULE = getRuleForExpansion(NonTerminal.OPTIONAL_INIT, ASSIGN, NonTerminal.CONST);
    public static final Rule NO_OPTIONAL_INIT_RULE = getRuleForExpansion(OPTIONAL_INIT, NULL);

    public static final Rule FUNCTION_DECLARATION_RULE = getRuleForExpansion(FUNCT_DECLARATION, FUNC, ID, LPAREN, PARAM_LIST, RPAREN, RET_TYPE, BEGIN, STAT_SEQ, END, SEMI);
    public static final Rule RETURN_TYPE_RULE = getRuleForExpansion(RET_TYPE, COLON, NonTerminal.TYPE);
    public static final Rule NO_RETURN_TYPE_RULE = getRuleForExpansion(RET_TYPE, NULL);
    public static final Rule PARAM_LIST_RULE = getRuleForExpansion(PARAM_LIST, PARAM, PARAM_LIST_TAIL);
    public static final Rule NO_PARAM_LIST_RULE = getRuleForExpansion(PARAM_LIST, NULL);
    public static final Rule PARAM_LIST_TAIL_RULE = getRuleForExpansion(PARAM_LIST_TAIL, COMMA, PARAM, PARAM_LIST_TAIL);
    public static final Rule PARAM_LIST_END_RULE = getRuleForExpansion(PARAM_LIST_TAIL, NULL);

    public static final Rule MAIN_RULE = getRuleForExpansion(MAIN, STAT_SEQ);

    public static final Rule STAT_SEQUENCE_RULE = getRuleForExpansion(STAT_SEQ, STAT, STAT_SEQ_TAIL);
    public static final Rule STAT_SEQUENCE_TAIL_RULE = getRuleForExpansion(STAT_SEQ_TAIL, STAT, STAT_SEQ_TAIL);
    public static final Rule STAT_SEQUENCE_END_RULE = getRuleForExpansion(STAT_SEQ_TAIL, NULL);

    public static final Rule ID_STATMENT_START_RULE = getRuleForExpansion(STAT, LVALUE, STAT_ID);
    public static final Rule FUNCTION_STATEMENT_START_RULE = getRuleForExpansion(STAT_ID, LPAREN, EXPR_LIST, RPAREN, SEMI);
    public static final Rule ASSIGNMENT_STATEMENT_RULE = getRuleForExpansion(STAT_ID, ASSIGN, STAT_TAIL, SEMI);

    public static final Rule EXPRESSION_NOT_STARTING_WITH_ID_RULE = getRuleForExpansion(STAT_TAIL, EXPR_NOT_STARTING_WITH_ID);
    public static final Rule EXPRESSION_OR_FUNCTION_START_RULE = getRuleForExpansion(STAT_TAIL, ID, EXPR_OR_FUNC_TAIL);
    public static final Rule FUNCTION_INVOCATION_RULE = getRuleForExpansion(EXPR_OR_FUNC_TAIL, LPAREN, EXPR_LIST, RPAREN, EXPR_TAIL);
    public static final Rule LVALUE_EXPRESSION_START_RULE = getRuleForExpansion(EXPR_OR_FUNC_TAIL, LVALUE_TAIL, C_TERM_TAIL, B_TERM_TAIL, A_TERM_TAIL, EXPR_TAIL);

    public static final Rule NO_EXPRESSION_LIST_RULE = getRuleForExpansion(EXPR_LIST, NULL);
    public static final Rule EXPRESSION_LIST_RULE = getRuleForExpansion(EXPR_LIST, EXPR, EXPR_LIST_TAIL);
    public static final Rule EXPRESSION_LIST_TAIL_RULE = getRuleForExpansion(EXPR_LIST_TAIL, COMMA, EXPR, EXPR_LIST_TAIL);
    public static final Rule EXPRESSION_LIST_END_RULE = getRuleForExpansion(EXPR_LIST_TAIL, NULL);

    public static final Rule AND_TERM_RULE = getRuleForExpansion(EXPR_TAIL, AND, A_TERM);
    public static final Rule OR_TERM_RULE = getRuleForExpansion(EXPR_TAIL, OR, A_TERM);
    public static final Rule EQ_TERM_RULE = getRuleForExpansion(A_TERM_TAIL, EQ, B_TERM);
    public static final Rule NEQ_TERM_RULE = getRuleForExpansion(A_TERM_TAIL, NEQ, B_TERM);
    public static final Rule LESSER_TERM_RULE = getRuleForExpansion(A_TERM_TAIL, LESSER, B_TERM);
    public static final Rule GREATER_TERM_RULE = getRuleForExpansion(A_TERM_TAIL, GREATER, B_TERM);
    public static final Rule LESSEREQ_TERM_RULE = getRuleForExpansion(A_TERM_TAIL, LESSEREQ, B_TERM);
    public static final Rule GREATEREQ_TERM_RULE = getRuleForExpansion(A_TERM_TAIL, GREATEREQ, B_TERM);
    public static final Rule PLUS_TERM_RULE = getRuleForExpansion(B_TERM_TAIL, PLUS, C_TERM);
    public static final Rule MINUS_TERM_RULE = getRuleForExpansion(B_TERM_TAIL, MINUS, C_TERM);
    public static final Rule MULT_TERM_RULE = getRuleForExpansion(C_TERM_TAIL, MULT, FACTOR);
    public static final Rule CONST_TERM_RULE = getRuleForExpansion(FACTOR, CONST);
    public static final Rule LVALUE_TERM_RULE = getRuleForExpansion(FACTOR, LVALUE);
    public static final Rule PAREN_TERM_RULE = getRuleForExpansion(FACTOR, LPAREN, EXPR, RPAREN);
    public static final Rule EXPR_END_RULE = getRuleForExpansion(EXPR_TAIL, NULL);
    public static final Rule ARRAY_INDEX_RULE = getRuleForExpansion(LVALUE_TAIL, LBRACK, EXPR, RBRACK);
    public static final Rule VARIABLE_VALUE_RULE = getRuleForExpansion(LVALUE_TAIL, NULL);

    public static final Rule IF_STATMENT_RULE = getRuleForExpansion(STAT, IF, EXPR, THEN, STAT_SEQ, IF_STATEMENT_TAIL);
    public static final Rule ELSE_RULE = getRuleForExpansion(IF_STATEMENT_TAIL, ELSE, STAT_SEQ, ENDIF, SEMI);
    public static final Rule NO_ELSE_RULE = getRuleForExpansion(IF_STATEMENT_TAIL, ENDIF);
    public static final Rule WHILE_STATEMENT_RULE = getRuleForExpansion(STAT, WHILE, EXPR, DO, STAT_SEQ, ENDDO, SEMI);
    public static final Rule FOR_STATEMENT_RULE = getRuleForExpansion(STAT, FOR, ID, ASSIGN, EXPR, TO, EXPR, DO, STAT_SEQ, ENDDO, SEMI);
    public static final Rule BREAK_STATEMENT_RULE = getRuleForExpansion(STAT, BREAK, SEMI);
    public static final Rule RETURN_STATEMENT_RULE = getRuleForExpansion(STAT, RETURN, EXPR, SEMI);
    public static final Rule LET_STATEMENT_RULE = getRuleForExpansion(STAT, LET, DECLARATION_SEGMENT, IN, STAT_SEQ, LET_END);
    public static final Rule LET_END_RULE = getRuleForExpansion(LET_END, END);

    private static Rule[] getUsefulParsingRules() {
        return new Rule[] {
                TYPE_DECLARATION_RULE,
                BASE_TYPE_RULE,
                ARRAY_TYPE_RULE,
                USER_DEFINED_TYPE_RULE,

                VAR_DECLARATION_RULE,
                VAR_LIST_TAIL_RULE,
                VAR_LIST_END_RULE,
                OPTIONAL_INIT_RULE,
                NO_OPTIONAL_INIT_RULE,

                FUNCTION_DECLARATION_RULE,
                RETURN_TYPE_RULE,
                NO_RETURN_TYPE_RULE,
                PARAM_LIST_RULE,
                NO_PARAM_LIST_RULE,
                PARAM_LIST_TAIL_RULE,
                PARAM_LIST_END_RULE,

                MAIN_RULE,

                STAT_SEQUENCE_RULE,
                STAT_SEQUENCE_TAIL_RULE,
                STAT_SEQUENCE_END_RULE,
                ID_STATMENT_START_RULE,

                EXPRESSION_LIST_RULE,
                NO_EXPRESSION_LIST_RULE,
                EXPRESSION_LIST_TAIL_RULE,
                EXPRESSION_LIST_END_RULE,

                ARRAY_INDEX_RULE,
                VARIABLE_VALUE_RULE,
                FUNCTION_STATEMENT_START_RULE,
                ASSIGNMENT_STATEMENT_RULE,

                EXPRESSION_NOT_STARTING_WITH_ID_RULE,
                EXPRESSION_OR_FUNCTION_START_RULE,
                FUNCTION_INVOCATION_RULE,
                LVALUE_EXPRESSION_START_RULE,

                AND_TERM_RULE,
                OR_TERM_RULE,
                EQ_TERM_RULE,
                NEQ_TERM_RULE,
                LESSER_TERM_RULE,
                GREATER_TERM_RULE,
                LESSEREQ_TERM_RULE,
                GREATEREQ_TERM_RULE,
                PLUS_TERM_RULE,
                MINUS_TERM_RULE,
                MULT_TERM_RULE,
                CONST_TERM_RULE,
                PAREN_TERM_RULE,
                LVALUE_TERM_RULE,
                EXPR_END_RULE,
                ARRAY_INDEX_RULE,
                VARIABLE_VALUE_RULE,

                IF_STATMENT_RULE,
                ELSE_RULE,
                NO_ELSE_RULE,
                WHILE_STATEMENT_RULE,
                FOR_STATEMENT_RULE,
                BREAK_STATEMENT_RULE,
                RETURN_STATEMENT_RULE,
                LET_STATEMENT_RULE,
                LET_END_RULE
        };
    }

    static {
        RULES_FOR_PARSING = new HashSet<>();
        Collections.addAll(RULES_FOR_PARSING, getUsefulParsingRules());
    }

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
