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
            {"7", "<tiger-program>", "let", "<declaration-segment>", "in", "<stat-seq>", "end"},
            {"5", "<declaration-segment>", "<type-declaration-list>", "<var-declaration-list>", "<funct-declaration-list>"},
            {"3", "<type-declaration-list>", "NULL"},
            {"4", "<type-declaration-list>", "<type-declaration>", "<type-declaration-list>"},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
            {"", ""},
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
