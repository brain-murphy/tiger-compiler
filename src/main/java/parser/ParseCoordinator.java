package parser;

import parser.semantic.ParseStream;
import parser.semantic.SemanticException;
import parser.semantic.ir.IrGenerator;
import parser.syntactic.Parser;
import scanner.DirectScanner;
import scanner.Scanner;

/**
 * Created by Brian on 11/11/2016.
 */
public class ParseCoordinator {

    private Scanner scanner;

    public ParseCoordinator(Scanner scanner) {

        this.scanner = scanner;
    }

    public void runParse() {
        ParseStream parseStream = new ParseStream();

        Parser parser = new Parser(scanner, parseStream);

        IrGenerator irGenerator = new IrGenerator(parseStream);

        parser.parse();

        try {
            irGenerator.run();
            System.out.println(irGenerator.getIR());

        } catch (SemanticException exc) {
            System.out.println(exc.getMessage());
            exc.printStackTrace();
        }
    }
}
