package codeGeneration.RegAlloc;

import parser.ParseCoordinator;
import parser.semantic.ir.LinearIr;
import scanner.DirectScanner;
import scanner.Scanner;
import util.Reader;

import java.util.ArrayList;
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

    private List<List<String>> orignalIR = new ArrayList<List<String>>();
    private List<List<String>> outputIRNaive = new ArrayList<List<String>>();

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
    }
    public void genRegAllocNaive(){
        // naive method for generating reg allocation code
    }
}
