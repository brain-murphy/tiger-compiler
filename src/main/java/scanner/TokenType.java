package scanner;

import parser.syntactic.GrammarSymbol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum TokenType implements GrammarSymbol {

    NULL(""),

    ID("^[a-zA-Z][a-zA-Z0-9]*"),
    INTLIT("^0|([1-9][0-9]*)"),
    FLOATLIT("^0|([1-9][0-9]*)\\.[0-9]*"),


    COMMA(","),
    COLON(":"),
    SEMI(";"),
    LPAREN("("),
    RPAREN(")"),
    LBRACK("["),
    RBRACK("]"),
    LBRACE("{"),
    RBRACE("}"),
    PERIOD("."),
    PLUS("+"),
    MINUS("-"),
    MULT("*"),
    DIV("/"),
    EQ("="),
    NEQ("<>"),
    LESSER("<"),
    GREATER(">"),
    LESSEREQ("<="),
    GREATEREQ(">="),
    AND("&"),
    OR("|"),
    ASSIGN(":="),

    ARRAY("array"),
    BREAK("break"),
    DO("do"),
    ELSE("else"),
    FOR("for"),
    FUNC("function"),
    RETURN("return"),
    IF("if"),
    IN("in"),
    LET("let"),
    OF("of"),
    THEN("then"),
    TO("to"),
    TYPE("type"),
    VAR("var"),
    WHILE("while"),
    ENDIF("endif"),
    BEGIN("begin"),
    END("end"),
    ENDDO("enddo"),
    INTTYPEID("int"),
    FLOATTYPEID("float");

    public static final TokenType[] KEYWORDS = {
        ARRAY, BREAK, DO, ELSE, FOR, FUNC, RETURN, IF, IN, LET, OF, THEN, TO, TYPE, VAR, WHILE, ENDIF, BEGIN,
            END, ENDDO, INTTYPEID, FLOATTYPEID
    };

    private String pattern;

    TokenType(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public int getPrecedence() {
        return ordinal();
    }

    private static TokenType[] PARSABLE_TOKEN_TYPES_ARRAY = {
            TokenType.ID,
            TokenType.INTLIT,
            TokenType.FLOATLIT,
            TokenType.FLOATTYPEID,
            TokenType.INTTYPEID
    };

    // hash set for faster lookup
    public static Set<TokenType> PARSABLE_TOKEN_TYPES;
    static {
        PARSABLE_TOKEN_TYPES = new HashSet<>();
        Collections.addAll(PARSABLE_TOKEN_TYPES, PARSABLE_TOKEN_TYPES_ARRAY);
    }
}
