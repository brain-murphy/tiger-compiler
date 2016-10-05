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
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", },
            {"", }
            }

        int i = 0;
        while (i < ) // Need the total number of rules
            {   
            listOfRules.add(new ArrayList<String>);
            ++i;
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
