import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.List;

public class Parser implements ParserInterface
    {
    private ArrayList<ArrayList<String>> listOfRules = new ArrayList<ArrayList<String>>();
    private Map<String, Map<String, int>> parsingTable = new HashMap<String, HashMap<String, int>>();
    private void fill_listOfRules()
        {

        String[][] arrayOfRules = 
            {
            // first item: length of the row, second item: NT for expanding
            {"7", "<tiger-program>", "LET", "<declaration-segment>", "IN", "<stat-seq>", "END"},
            {"5", "<declaration-segment>", "<type-declaration-list>", "<var-declaration-list>", "<funct-declaration-list>"},
            {"3", "<type-declaration-list>", "NULL"},
            {"4", "<type-declaration-list>", "<type-declaration>", "<type-declaration-list>"},
            {"3", "<var-declaration-list>", "NULL"},
            {"4", "<var-declaration-list>","<var-declaration>", "<var-declaration-list>"},
            {"3", "<funct-declaration-list>", "NULL"},
            {"4", "<funct-declaration-list>", "<funct-declaration>", "<funct-declaration-list>"},
            {"7", "<type-declaration>", "TYPE", "<id>", "EQ", "<type>", "SEMI"},
            {"3", "<type>", "<type-id>"},
            {"6", "<type>", "ARRAY", "INTLIT", "OF", "<type-id>"},
            {"3", "<type>", "ID"},
            {"3", "<type-id>", "INT"},
            {"3", "<type-id>", "FLOAT"},
            {"8", "<var-declaration>", "VAR", "<id-list>", "COLON", "<type>", "<optional-init>", "SEMI"},
            {"4", "<id-list>", "ID", "<id-list-tail>"},
            {"3", "<id-list-tail>", "COMMA", "<id-list>"},
            {"3", "<id-list-tail>", "NULL"},
            {"3", "<optional-init>", "NULL"},
            {"4", "<optional-init>", "ASSIGN", "<const>"},
            {"12", "<funct-declaration>", "FUNC", "ID", "LPAREN", "<param-list>", "RPAREN", "<ret-type>", "BEGIN", "<stat-seq>", "END", "SEMI"},
            {"3", "<param-list>", "NULL"},
            {"4", "<param-list>", "<param>", "<param-list-tail>"},
            {"3", "<param-list-tail>", "NULL"},
            {"5", "<param-list-tail>", "COMMA", "<param>", "<param-list-tail>"},
            {"3", "<ret-type>", "NULL"},
            {"4", "<ret-type>", "COLON", "<type>"},
            {"5", "<param>", "ID", "COLON", "<type>"},
            {"4", "<stat-seq>", "<stat>", "<stat-seq-tail>"},
            {"3", "<stat-seq-tail>", "NULL"},
            {"3", "<stat-seq-tail>", "<stat-seq>"},
            {"10", "<stat>", "IF", "<expr>", "THEN", "<stat-seq>", "ELSE", "<stat-seq>", "ENDIF", "SEMI"},
            {"8", "<stat>", "WHILE", "<expr>", "DO", "<stat-seq>", "ENDDO", "SEMI"},
            {"12", "<stat>", "FOR", "ID", "ASSIGN", "<expr>", "TO", "<expr>", "DO", "<stat-seq>", "ENDDO", "SEMI"},
            {"4", "<stat>", "ID", "<stat-id>"},
            {"4", "<stat>", "BREAK", "SEMI"},
            {"5", "<stat>", "RETURN", "<expr>", "SEMI"},
            {"7", "<stat>", "LET", "<declaration-segment>", "IN", "<stat-seq>", "END"},
            {"5", "<stat-id>", "<lvalue>", "ASSIGN", "<stat-tail>"},
            {"5", "<stat-id>", "LPAREN", "<expr­list>", "RPAREN", "SEMI"},
            {"3", "<stat-tail>", "<expr>", "SEMI"},
            {"6", "<stat-tail>", "ID", "LPAREN", "<expr­list>", "RPAREN", "SEMI"},
            {"4", "<expr>", "<Aterm>", "<expr-tail>"},
            {"4", "<expr-tail>", "AND", "<Aterm>"},
            {"4", "<expr-tail>", "OR", "<Aterm>"},
            {"3", "<expr-tail>", "NULL"},
            {"4", "<Aterm>", "<Bterm>", "<Aterm-tail>"},
            {"4", "<Aterm-tail>", "EQ", "<Bterm>"},
            {"4", "<Aterm-tail>", "NEQ", "<Bterm>"},
            {"4", "<Aterm-tail>", "LESSER", "<Bterm>"},
            {"4", "<Aterm-tail>", "GREATER", "<Bterm>"},
            {"4", "<Aterm-tail>", "LESSEREQ", "<Bterm>"},
            {"4", "<Aterm-tail>", "GREATEREQ", "<Bterm>"},
            {"3", "<Aterm-tail>", "NULL"},
            {"4", "<Bterm>", "<Cterm>", "<Bterm-tail>"},
            {"4", "<Bterm-tail>", "PLUS", "<Cterm>"},
            {"4", "<Bterm-tail>", "MINUS", "<Cterm>"},
            {"3", "<Bterm-tail>", "NULL"},
            {"4", "<Cterm>", "<factor>", "<Cterm-tail>"},
            {"4", "<Cterm-tail>", "MULT", "<factor>"},
            {"4", "<Cterm-tail>", "DIV", "<factor>"},
            {"3", "<Cterm-tail>", "NULL"},
            {"3", "<factor>", "<const>"},
            {"4", "<factor>", "ID", "<lvalue>"},
            {"5", "<factor>", "LPAREN", "<expr>", "RPAREN"},
            {"3", "<const>", "INTLIT"},
            {"3", "<const>", "FLOATLIT"},
            {"3", "<expr-list>", "NULL"},
            {"4", "<expr-list>", "<expr>", "<expr-list-tail>"},
            {"5", "<expr-list-tail>", "COMMA", "<expr>", "<expr-list-tail>"},
            {"3", "<expr-list-tail>", "NULL"},
            {"3", "<lvalue>", "NULL"},
            {"5", "<lvalue>", "LBRACK", "<expr>", "RBRACK"}
            }

            for (int i = 0; i < arrayOfRules.length; i++) // Need the total number of rules
            {   
                listOfRules.add(new ArrayList<String>);
            }
        }

    private void fill_parsingTable()
        {

        }

    public Parser(String fileText) 
        {
        fill_listOfRules();
        fill_parsingTable();
        this.fileText = fileText;
        scannedTokens = new ArrayList<>();
        }
    }
