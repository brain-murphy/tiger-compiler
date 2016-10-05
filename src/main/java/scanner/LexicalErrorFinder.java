package scanner;

import static scanner.TokenType.*;

public class LexicalErrorFinder {
    private String problemString;

    public LexicalErrorFinder(String problemString) {
        this.problemString = problemString;
    }

    public int findIndexOfFirstError(TokenType typeToMatch) {
        switch (typeToMatch) {
            case AND:
                return 0;

            case ARRAY:
                return keyword(ARRAY);

            case ASSIGN:
                if (problemString.charAt(0) != ':') {
                    return 0;
                } else {
                    return 1;
                }

            case BEGIN:
                return keyword(BEGIN);

            case BREAK:
                return keyword(BREAK);

            case COLON:
                return 0;

            case COMMA:
                return 0;

            case DIV:
                return 0;

            case DO:
                return keyword(DO);

            case ELSE:
                return keyword(ELSE);

            case END:
                return keyword(END);

            case ENDDO:
                return keyword(ENDDO);

            case ENDIF:
                return keyword(ENDIF);

            case EQ:
                return 0;

            case FLOATLIT:
                return floatLiteral();

            case FOR:
                return keyword(FOR);

            case FUNC:
                return keyword(FUNC);

            case GREATER:
                return 0;

            case GREATREQ:
                if (problemString.charAt(0) == '>') {
                    return 0;
                } else {
                    return 1;
                }

            case ID:
                return id();

            case IF:
                return keyword(IF);

            case IN:
                return keyword(IN);

            case INTLIT:
                return intLiteral();

            case LBRACE:
                return 0;

            case LBRACK:
                return 0;

            case LESSER:
                return 0;

            case LESSEREQ:
                if (problemString.charAt(0) != '<') {
                    return 0;
                } else {
                    return 1;
                }

            case LET:
                return keyword(LET);

            case LPAREN:
                return 0;

            case MINUS:
                return 0;

            case MULT:
                return 0;

            case NEQ:
                if (problemString.charAt(0) != '<') {
                    return 0;
                } else {
                    return 1;
                }

            case OF:
                return keyword(OF);

            case OR:
                return 0;

            case PERIOD:
                return 0;

            case PLUS:
                return 0;

            case RBRACE:
                return 0;

            case RBRACK:
                return 0;

            case RPAREN:
                return 0;

            case SEMI:
                return 0;

            case THEN:
                return keyword(THEN);

            case TO:
                return keyword(TO);

            case TYPE:
                return keyword(TYPE);

            case VAR:
                return keyword(VAR);

            case WHILE:
                return keyword(WHILE);

            case RETURN:
                return keyword(RETURN);

            default:
                throw new RuntimeException("No lexical error finding for TokenType " + typeToMatch.name());
        }
    }

    private int keyword(TokenType tokenType) {
        for (int i = 0; i < tokenType.getPattern().length(); i++) {
            if (problemString.charAt(i) != tokenType.getPattern().charAt(i)) {
                return i;
            }
        }
        return tokenType.getPattern().length();
    }

    private int floatLiteral() {
        int decimalPointIndex = -1;
        for (int i = 0; i < problemString.length(); i++) {
            if (!Character.isDigit(problemString.charAt(i))) {
                if (problemString.charAt(i) == '.') {
                    decimalPointIndex = i;
                    break;
                } else {
                    return i;
                }
            }
        }

        if (decimalPointIndex == -1) {
            return problemString.length();
        }

        for (int i = decimalPointIndex + 1; i < problemString.length(); i++) {
            if (!Character.isDigit(problemString.charAt(i))) {
                return i;
            }
        }

        return problemString.length();
    }

    private int id() {
        if (!Character.isAlphabetic(problemString.charAt(0))) {
            return 0;
        }

        for (int i = 1; i < problemString.length(); i++) {
            if (!Character.isLetterOrDigit(problemString.charAt(i))) {
                return i;
            }
        }

        return problemString.length();
    }

    private int intLiteral() {
        for (int i = 0; i < problemString.length(); i++) {
            if (!Character.isDigit(problemString.charAt(i))) {
                return i;
            }
        }

        return problemString.length();
    }
}
