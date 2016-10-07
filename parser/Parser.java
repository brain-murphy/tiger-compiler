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
    private Map<String, Map<String, int>> firstSet = new HashMap<String, HashMap<String, int>>(); //<NT, <T or NT, # of rules>>
    // After completing the construction of firstSet, there should be only T in the latter part
    private Map<String, Map<String, String>> followSet = new HashMap<String, HashMap<String, String>>();
    

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
                List<String> tempList = new ArrayList<String>();
                for(int j; j < arrayOfRules[i].length; ++j)
                    {
                    tempList.add( arrayOfRules[i][j] );
                    } 
                listOfRules.add( tempList );
                }
        }

    private void initialize_parsingTable()
        {
        for(int i = 0;i < listOfRules.size(); ++i)
            {
            if( parsingTable.containsKey( listOfRules[i][0] ) != true )
                {
                // a new NT
                Map<String, int> tempMap = new HashMap<String, int>();
                Map<String, int> tempMap2 = new HashMap<String, int>();
                Map<String, String> tempMap3 = new HashMap<String, String>();
                parsingTable.put( listOfRules[i][0], tempMap );
                firstSet.put( listOfRules[i][0], tempMap2 );
                followSet.put( listOfRules[i][0], tempMap3 );
                }
            }
        }

    private void compute_FirstSet()
        {
        // First, traversing through all rules
        for(i = 0; i < listOfRules.size(); ++i)
            {
            firstSet.get( listOfRules[i][0] ).put( listOfRules[i][1], i );
            }
        // Expand all NT in the first set
        boolean setChangingFlag = true;
        while(setChangingFlag == true)
            {
            setChangingFlag = false;
            for (Map.Entry<String, Map<String, int>> entry : firstSet.entrySet()) 
                {
                //key = entry.getKey();
                //value = entry.getValue();
                Map<String, int> tempMap = entry.getValue();
                ArrayList<ArrayList<String>> symbolList = new ArrayList<ArrayList<String>>();
                ArrayList<String> nt_for_remove_List = new ArrayList<String>();

                for(Map.Entry<String, int> l2_entry : tempMap.entrySet())
                    {
                    if( firstSet.containsKey( l2_entry.getKey() ) )
                        {
                        // Found a NT, need to further expand it
                        setChangingFlag = true;
                        Map<String, int> other_NT = firstSet.get( l2_entry.getKey() );
                        nt_for_remove_List.add( l2_entry.getKey() );
                        ArrayList<String> tempSymbolList = new ArrayList<String>();
                        // Copy the first set of the other NT to this NT
                        // Should not simply add to the NT, since we are traversing the NT now
                        // Instead, storing the new symbols to a temporary place: 2D arrayList
                        for(String key : other_NT.keySet())
                            {
                            // if( Objects.equals(key, "NULL") != true )
                                // {
                                // We don't want NULL
                            // We should put NULL
                            tempSymbolList.add( key );
                                // }
                            }
                        symbolList.add( tempSymbolList );
                        }
                    }
                // Remove the NTs we have expanded
                for(int i; i < symbolList.size(); ++i)
                    {
                    int rule_number = tempMap.get( nt_for_remove_List[i] );
                    for(int j; j < symbolList[i].size(); ++j)
                        {
                        tempMap.put( symbolList[i][j], rule_number );
                        }
                    tempMap.remove( nt_for_remove_List[i] );
                    }
                }
            }

        }

    private void compute_FollowSet()
        {
        followSet.get( listOfRules[0][0] ).put( "$","$"  ); // Add $ to the start NT
        for(int i; i < listOfRules.size(); ++i)
            {
            // Iterating through each rule
            int j = listOfRules[i].size() - 1;
            if( followSet.containsKey( listOfRules[i][j] ) )
                {
                // The last symbol is a NT
                followSet.get( listOfRules[i][j] ).put( listOfRules[i][0], listOfRules[i][0] );
                // Do we need to do this in every iteration?
                }
            while( j >= 1 )
                {
                if( followSet.containsKey( listOfRules[i][j-1] ) )
                    {
                    Map<String, String> currentNTMap = followSet.get( listOfRules[i][j-1] );
                    // This is a NT
                    // Follow(j-1) = First(j) - NULL
                    Map<String, int> tempMap = firstSet.get( listOfRules[i][j] );
                    for(String key : tempMap.keySet())
                        {
                        if( Objects.equals(key, "NULL") != true && Objects.equals(key, listOfRules[i][j-1]) != true )
                            {
                            currentNTMap.put( key, key );
                            }
                        }
                    if( firstSet.get( listOfRules[i][j] ).containsKey("NULL") == true )
                        {
                        // Follow(j-1) contains Follow( listOfRules[i][0] )
                        currentNTMap.put( listOfRules[i][0], listOfRules[i][0] );
                        }
                    }
                --j;
                }
            }
        // Then we have to expand all NT in each Follow set

        boolean setChangingFlag = true;
        while(setChangingFlag == true)
            {
            setChangingFlag = false;
            for (Map.Entry<String, Map<String, String>> entry : followSet.entrySet()) 
                {
                Map<String, int> currentMap = entry.getValue();
                String currentNT = entry.getKey();
                for(String key : currentMap.keySet())
                    {
                    if( followSet.containsKey( key ) )
                        {
                        setChangingFlag = true;
                        // We found a NT, it needs to be expand
                        Map<String, int> tempMap = followSet.get( key );
                        for(String element_of_set : tempMap.keySet())
                            {
                            if( Objects.equals(element_of_set, currentNT) != true )
                                {
                                currentMap.add( element_of_set );
                                }
                            }
                        }
                    }
                }
            }
        }

    private void fill_parsingTable()
        {

        }

    public Parser(String fileText) 
        {
        fill_listOfRules();
        initialize_parsingTable();
        fill_parsingTable();
        this.fileText = fileText;
        scannedTokens = new ArrayList<>();
        }
    }
