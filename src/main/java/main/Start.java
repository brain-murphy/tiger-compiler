package main;

import parser.syntactic.Parser;
import scanner.DirectScanner;
import scanner.Scanner;
import util.Reader;

public class Start {
    public static void main(String[] args) {

        Reader reader = new Reader();

        String programContent = null;
        if (args.length > 0) {
            String filePath = args[0];
            programContent = reader.readFromFile(filePath);
        } else {
            programContent = reader.readFromStandardIn();
        }
    }
}
