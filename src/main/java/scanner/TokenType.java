package scanner;

import javax.swing.text.Position;
import java.util.regex.Pattern;

public enum TokenType {

    COMMA("^,"),
    COLON("^:"),
    SEMI("^;"),
    LPAREN("^("),
    RPAREN("^)"),
    LBRACK("^["),
    RBRACK("^]"),
    LBRACE("^{"),
    RBRACE("^}"),
    PERIOD("^."),
    PLUS("^+"),
    MINUS("^-"),
    MULT("^*"),
    DIV("^/"),
    EQ("^="),
    NEQ("^<>"),
    LESSER("^<"),
    GREATER("^>"),
    LESSEREQ("^<="),
    GREATREQ("^>="),
    AND("^&"),
    OR("^|"),
    ASSIGN("^:="),

    ARRAY("^array"),
    BREAK("^break"),
    DO("^do"),
    ELSE("^else"),
    FOR("^for"),
    FUNC("^func"),
    IF("^if"),
    IN("^in"),
    LET("^let"),
    OF("^of"),
    THEN("^then"),
    TO("^to"),
    TYPE("^type"),
    VAR("^var"),
    WHILE("^while"),
    ENDIF("^endif"),
    BEGIN("^begin"),
    END("^end"),
    ENDDO("^enddo");

    private Pattern regex;

    TokenType(String regex) {
        this.regex = Pattern.compile(regex);
    }

    public Pattern getRegex() {
        return regex;
    }

    public int getPrecedence() {
        return ordinal();
    }
}
