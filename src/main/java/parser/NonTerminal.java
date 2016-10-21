package parser;

public enum NonTerminal implements GrammarSymbol {
    TIGER_PROGRAM,
    DECLARATION_SEGMENT,
    TYPE_DECLARATION_LIST,
    VAR_DECLARATION_LIST,
    FUNCT_DECLARATION_LIST,
    TYPE_DECLARATION,
    TYPE,
    TYPE_ID,
    VAR_DECLARATION,
    ID_LIST,
    ID_LIST_TAIL,
    OPTIONAL_INIT,
    FUNCT_DECLARATION,
    PARAM_LIST,
    PARAM_LIST_TAIL,
    RET_TYPE,
    PARAM,
    STAT_SEQ,
    STAT_SEQ_TAIL,
    STAT,
    STAT_ID,
    STAT_TAIL,
    STAT_TAIL_ID,
    EXPR,
    EXPR_TAIL,
    A_TERM,
    A_TERM_HEAD,
    A_TERM_TAIL,
    B_TERM,
    B_TERM_HEAD,
    B_TERM_TAIL,
    C_TERM,
    C_TERM_HEAD,
    C_TERM_TAIL,
    FACTOR,
    CONST,
    EXPR_LIST,
    EXPR_ID,
    EXPR_HEAD,
    EXPR_LIST_TAIL,
    LVALUE,
    LVALUE_TAIL,

    EXPR_NOT_STARTING_WITH_ID,
    EXPR_OR_FUNC_TAIL,
    NOT_ID_EXPR_START,

    lexp,
    atom,
    list,
    lexp_seq,
    lexp_seq_tail
}
