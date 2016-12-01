package codeGeneration.RegAlloc;

import parser.ParseCoordinator;
import parser.semantic.ir.LinearIr;
import scanner.DirectScanner;
import scanner.Scanner;
import util.Reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fishlinghu on 2016/11/30.
 */
public class RegAlloc {
    private class regSet{
        public String dst = "";
        public String op1 = "";
        public String op2 = "";
    }

    private List<List<String>> originalIR = new ArrayList<List<String>>();
    private List<List<String>> outputIRNaive = new ArrayList<List<String>>();

    @org.junit.Test
    public void inputIR() {
        // parse in the input IR and store in the DS here
        Reader reader = new Reader();
        String programText = reader.readFromFile("./examples/test5.tiger");

        Scanner scanner = new DirectScanner(programText);

        ParseCoordinator parseCoordinator = new ParseCoordinator(scanner);

        LinearIr temp = parseCoordinator.getIr();
        String oldIR = temp.toString();

        System.out.print("=====================\n");
        System.out.print( oldIR );
        System.out.print("=====================\n");

        String[] tempArr = oldIR.split("\n");
        ArrayList<String> listOfIR = new ArrayList<String>(Arrays.asList(tempArr)); // Split IR line by line

        int i = 0;
        while(i < listOfIR.size()){
            originalIR.add( new ArrayList<String>( Arrays.asList(listOfIR.get(i).split(", ")) ) );
            System.out.print(" ---");
            System.out.print( originalIR.get(i).get(0) );
            System.out.print("\n");
            i = i + 1;
        }
    }
    public void genRegAllocNaive(){
        // naive method for generating reg allocation code
    }
}
