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
            // first item: NT for expanding
            {"<tiger-program>", "LET", "<declaration-segment>", "IN", "<stat-seq>", "END"},
            {"<declaration-segment>", "<type-declaration-list>", "<var-declaration-list>", "<funct-declaration-list>"},
            {"<type-declaration-list>", "NULL"},
            {"<type-declaration-list>", "<type-declaration>", "<type-declaration-list>"},
            {"<var-declaration-list>", "NULL"},
            {"<var-declaration-list>","<var-declaration>", "<var-declaration-list>"},
            {"<funct-declaration-list>", "NULL"},
            {"<funct-declaration-list>", "<funct-declaration>", "<funct-declaration-list>"},
            {"<type-declaration>", "TYPE", "<id>", "EQ", "<type>", "SEMI"},
            {"<type>", "<type-id>"},
            {"<type>", "ARRAY", "INTLIT", "OF", "<type-id>"},
            {"<type>", "ID"},
            {"<type-id>", "INT"},
            {"<type-id>", "FLOAT"},
            {"<var-declaration>", "VAR", "<id-list>", "COLON", "<type>", "<optional-init>", "SEMI"},
            {"<id-list>", "ID", "<id-list-tail>"},
            {"<id-list-tail>", "COMMA", "<id-list>"},
            {"<id-list-tail>", "NULL"},
            {"<optional-init>", "NULL"},
            {"<optional-init>", "ASSIGN", "<const>"},
            {"<funct-declaration>", "FUNC", "ID", "LPAREN", "<param-list>", "RPAREN", "<ret-type>", "BEGIN", "<stat-seq>", "END", "SEMI"},
            {"<param-list>", "NULL"},
            {"<param-list>", "<param>", "<param-list-tail>"},
            {"<param-list-tail>", "NULL"},
            {"<param-list-tail>", "COMMA", "<param>", "<param-list-tail>"},
            {"<ret-type>", "NULL"},
            {"<ret-type>", "COLON", "<type>"},
            {"<param>", "ID", "COLON", "<type>"},
            {"<stat-seq>", "<stat>", "<stat-seq-tail>"},
            {"<stat-seq-tail>", "NULL"},
            {"<stat-seq-tail>", "<stat-seq>"},
            {"<stat>", "IF", "<expr>", "THEN", "<stat-seq>", "ELSE", "<stat-seq>", "ENDIF", "SEMI"},
            {"<stat>", "WHILE", "<expr>", "DO", "<stat-seq>", "ENDDO", "SEMI"},
            {"<stat>", "FOR", "ID", "ASSIGN", "<expr>", "TO", "<expr>", "DO", "<stat-seq>", "ENDDO", "SEMI"},
            {"<stat>", "ID", "<stat-id>"},
            {"<stat>", "BREAK", "SEMI"},
            {"<stat>", "RETURN", "<expr>", "SEMI"},
            {"<stat>", "LET", "<declaration-segment>", "IN", "<stat-seq>", "END"},
            {"<stat-id>", "<lvalue>", "ASSIGN", "<stat-tail>"},
            {"<stat-id>", "LPAREN", "<expr­list>", "RPAREN", "SEMI"},
            {"<stat-tail>", "<expr>", "SEMI"},
            {"<stat-tail>", "ID", "LPAREN", "<expr­list>", "RPAREN", "SEMI"},
            {"<expr>", "<Aterm>", "<expr-tail>"},
            {"<expr-tail>", "AND", "<Aterm>"},
            {"<expr-tail>", "OR", "<Aterm>"},
            {"<expr-tail>", "NULL"},
            {"<Aterm>", "<Bterm>", "<Aterm-tail>"},
            {"<Aterm-tail>", "EQ", "<Bterm>"},
            {"<Aterm-tail>", "NEQ", "<Bterm>"},
            {"<Aterm-tail>", "LESSER", "<Bterm>"},
            {"<Aterm-tail>", "GREATER", "<Bterm>"},
            {"<Aterm-tail>", "LESSEREQ", "<Bterm>"},
            {"<Aterm-tail>", "GREATEREQ", "<Bterm>"},
            {"<Aterm-tail>", "NULL"},
            {"<Bterm>", "<Cterm>", "<Bterm-tail>"},
            {"<Bterm-tail>", "PLUS", "<Cterm>"},
            {"<Bterm-tail>", "MINUS", "<Cterm>"},
            {"<Bterm-tail>", "NULL"},
            {"<Cterm>", "<factor>", "<Cterm-tail>"},
            {"<Cterm-tail>", "MULT", "<factor>"},
            {"<Cterm-tail>", "DIV", "<factor>"},
            {"<Cterm-tail>", "NULL"},
            {"<factor>", "<const>"},
            {"<factor>", "ID", "<lvalue>"},
            {"<factor>", "LPAREN", "<expr>", "RPAREN"},
            {"<const>", "INTLIT"},
            {"<const>", "FLOATLIT"},
            {"<expr-list>", "NULL"},
            {"<expr-list>", "<expr>", "<expr-list-tail>"},
            {"<expr-list-tail>", "COMMA", "<expr>", "<expr-list-tail>"},
            {"<expr-list-tail>", "NULL"},
            {"<lvalue>", "NULL"},
            {"<lvalue>", "LBRACK", "<expr>", "RBRACK"}
            }

            for (int i = 0; i < arrayOfRules.length; i++) // Need the total number of rules
                {
                List<String> tempList = new ArrayList<Class>();
                for(int j; j < arrayOfRules[i].length; ++j)
                    {
                    tempList.add( arrayOfRules[i][j] );
                    } 
                listOfRules.add( tempList );
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
