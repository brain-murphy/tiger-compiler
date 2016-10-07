import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.List;
import java.util.Objects;

public class Parser implements ParserInterface
    {
    private ArrayList<ArrayList<String>> listOfRules = new ArrayList<ArrayList<String>>();
    private HashMap<String, HashMap<String, Integer>> parsingTable = new HashMap<String, HashMap<String, Integer>>();
    private HashMap<String, HashMap<String, Integer>> firstSet = new HashMap<String, HashMap<String, Integer>>(); //<NT, <T or NT, # of rules>>
    // After completing the construction of firstSet, there should be only T in the latter part
    private HashMap<String, HashMap<String, String>> followSet = new HashMap<String, HashMap<String, String>>();
    private ArrayList<Token> listOfTokens = new ArrayList<Token>();
    

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
            };

            for (int i = 0; i < arrayOfRules.length; i++) // Need the total number of rules
                {
                ArrayList<String> tempList = new ArrayList<String>();
                for(int j = 0; j < arrayOfRules[i].length; ++j)
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
            if( parsingTable.containsKey( listOfRules.get(i).get(0) ) != true )
                {
                // a new NT
                System.out.println(i);
                HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
                HashMap<String, Integer> tempMap2 = new HashMap<String, Integer>();
                HashMap<String, String> tempMap3 = new HashMap<String, String>();
                parsingTable.put( listOfRules.get(i).get(0), tempMap );
                firstSet.put( listOfRules.get(i).get(0), tempMap2 );
                followSet.put( listOfRules.get(i).get(0), tempMap3 );
                }
            }
        }

    private void compute_FirstSet()
        {
        // First, traversing through all rules
        for(int i = 0; i < listOfRules.size(); ++i)
            {
            firstSet.get( listOfRules.get(i).get(0) ).put( listOfRules.get(i).get(1), i );
            }
        // Expand all NT in the first set
        boolean setChangingFlag = true;
        while(setChangingFlag == true)
            {
            setChangingFlag = false;
            for (Map.Entry<String, HashMap<String, Integer>> entry : firstSet.entrySet()) 
                {
                //key = entry.getKey();
                //value = entry.getValue();
                HashMap<String, Integer> tempMap = entry.getValue();
                ArrayList<ArrayList<String>> symbolList = new ArrayList<ArrayList<String>>();
                ArrayList<String> nt_for_remove_List = new ArrayList<String>();

                for(Map.Entry<String, Integer> l2_entry : tempMap.entrySet())
                    {
                    if( firstSet.containsKey( l2_entry.getKey() ) )
                        {
                        // Found a NT, need to further expand it
                        setChangingFlag = true;
                        HashMap<String, Integer> other_NT = firstSet.get( l2_entry.getKey() );
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
                for(int i = 0; i < symbolList.size(); ++i)
                    {
                    int rule_number = tempMap.get( nt_for_remove_List.get(i) );
                    for(int j = 0; j < symbolList.get(i).size(); ++j)
                        {
                        tempMap.put( symbolList.get(i).get(j), rule_number );
                        }
                    tempMap.remove( nt_for_remove_List.get(i) );
                    }
                }
            }

        }

    private void compute_FollowSet()
        {
        followSet.get( listOfRules.get(0).get(0) ).put( "$","$"  ); // Add $ to the start NT
        for(int i = 0; i < listOfRules.size(); ++i)
            {
            // Iterating through each rule
            int j = listOfRules.get(i).size() - 1;
            if( followSet.containsKey( listOfRules.get(i).get(j) ) )
                {
                // The last symbol is a NT
                followSet.get( listOfRules.get(i).get(j) ).put( listOfRules.get(i).get(0), listOfRules.get(i).get(0) );
                // Do we need to do this in every iteration?
                }
            while( j >= 1 )
                {
                if( followSet.containsKey( listOfRules.get(i).get(j-1) ) )
                    {
                    HashMap<String, String> currentNTMap = followSet.get( listOfRules.get(i).get(j-1) );
                    // This is a NT
                    // Follow(j-1) = First(j) - NULL
                    if( followSet.containsKey( listOfRules.get(i).get(j) ) )
                        {
                        HashMap<String, Integer> tempMap = firstSet.get( listOfRules.get(i).get(j) );
                        for(String key : tempMap.keySet())
                            {
                            if( Objects.equals(key, "NULL") != true && Objects.equals(key, listOfRules.get(i).get(j-1)) != true )
                                {
                                currentNTMap.put( key, key );
                                }
                            }
                        if( firstSet.get( listOfRules.get(i).get(j) ).containsKey("NULL") == true )
                            {
                            // Follow(j-1) contains Follow( listOfRules[i][0] )
                            currentNTMap.put( listOfRules.get(i).get(0), listOfRules.get(i).get(0) );
                            }
                        }
                    else
                        if( Objects.equals(listOfRules.get(i).get(j), "NULL") != true )
                            currentNTMap.put( listOfRules.get(i).get(j), listOfRules.get(i).get(j) );
                    }
                --j;
                }
            }
        // Then we have to expand all NT in each Follow set

        boolean setChangingFlag = true;
        while(setChangingFlag == true)
            {
            System.out.println("==========================================");
            setChangingFlag = false;
            for (Map.Entry<String, HashMap<String, String>> entry : followSet.entrySet()) 
                {
                HashMap<String, String> currentMap = entry.getValue();
                String currentNT = entry.getKey();
                ArrayList<String> expandedSymbolsList = new ArrayList<String>();
                ArrayList<String> removedKeyList = new ArrayList<String>();

                System.out.println( currentNT );

                for(String key : currentMap.keySet())
                    {
                    System.out.print( key + ", ");
                    if( followSet.containsKey( key ) )
                        {
                        setChangingFlag = true;
                        removedKeyList.add( key );
                        // We found a NT, it needs to be expand
                        HashMap<String, String> tempMap = followSet.get( key );
                        for(String element_of_set : tempMap.keySet())
                            {
                            if( Objects.equals(element_of_set, currentNT) != true && Objects.equals(element_of_set, "NULL") != true )
                                {
                                //currentMap.put( element_of_set, element_of_set );
                                expandedSymbolsList.add( element_of_set );
                                }
                            }
                        }
                    }
                System.out.println();
                for(int i = 0; i < expandedSymbolsList.size(); ++i)
                    {
                    currentMap.put( expandedSymbolsList.get(i), expandedSymbolsList.get(i) );
                    }
                System.out.println();
                for(int i = 0; i < removedKeyList.size(); ++i)
                    {
                    currentMap.remove( removedKeyList.get(i) );
                    }
                }
            }
        }

    private void print_firstSet()
        {
        int i = 0;
        while( i < listOfRules.size() )
            {
            String currentNT = listOfRules.get(i).get(0);
            System.out.print( i + ": " + currentNT );
            HashMap<String, Integer> tempMap = firstSet.get( currentNT );
            for (Map.Entry<String, Integer> entry : tempMap.entrySet()) 
                {
                System.out.print("(" + entry.getKey() + "," + entry.getValue() + ") ,");
                }
            System.out.println();
            ++i;
            }
        }

    private void fill_parsingTable()
        {
        for (Map.Entry<String, HashMap<String, Integer>> entry : parsingTable.entrySet()) 
            {
            HashMap<String, Integer> currentRow = entry.getValue();
            String currentNT = entry.getKey();

            HashMap<String, Integer> currentFirstSet = firstSet.get( currentNT );
            for (Map.Entry<String, Integer> entry_of_First_set : currentFirstSet.entrySet()) 
                {
                if( Objects.equals( entry_of_First_set.getKey(), "NULL" ) == true )
                    {
                    HashMap<String, String> currentFollowSet = followSet.get( currentNT );
                    for (String element_of_set : currentFollowSet.keySet()) 
                        {
                        currentRow.put( element_of_set, entry_of_First_set.getValue() );
                        }
                    }
                else
                    {
                    currentRow.put( entry_of_First_set.getKey(), entry_of_First_set.getValue() );
                    }
                }
            }
        }

    private void parsing()
        {
        Stack<String> symbol_stack = new Stack<String>();
        // Push the start symbol to the stack
        symbol_stack.push( listOfRules.get(0).get(0) );
        while( true )
            {
            if( symbol_stack.empty() == true &&  ) // No more input, parsing finish successfully
                {

                break;
                }
            else if( symbol_stack.empty() == true && ) // No more symbols in the stack, but still has input, error
                {
                // What if no more input but we still has nonterminal?
                }
            else if( parsingTable.containsKey( symbol_stack.peek() ) != true )
                {
                // Top of stack is a terminal
                if(  )
                    {
                    // terminal match the input
                    }
                else
                    {
                    // Error, terminal did not match the input
                    }
                }
            else
                {
                // A NT, we need to see the input and parsing table to decide how to expand the NT (which rules to use)
                int rule_number = parsingTable.get( TOS ).get( next_input );
                // pop TOS
                for(int i = listOfRules.get( rule_number ).size() - 1; i >= 0 ; --i)
                    {
                    // Push the symbols in the rule backward
                    symbol_stack.push( listOfRules.get( rule_number ).get( i ) );
                    }
                }
            }

        }

    public Parser() 
        {
        fill_listOfRules();
        initialize_parsingTable();
        compute_FirstSet();
        print_firstSet();
        compute_FollowSet();
        fill_parsingTable();
        //this.fileText = fileText;
        //scannedTokens = new ArrayList<>();
        }
    public static void main(String[] args)
        {
        Parser obj = new Parser();
        }
    }
