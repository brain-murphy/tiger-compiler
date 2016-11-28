package parser;

import parser.semantic.ParseStream;
import parser.semantic.SemanticException;
import parser.semantic.ir.IrGenerator;
import parser.semantic.ir.LinearIr;
import parser.semantic.symboltable.SymbolTable;
import parser.syntactic.Parser;
import scanner.Scanner;

/**
 * Created by Brian on 11/11/2016.
 */
public class ParseCoordinator {

    private Scanner scanner;
    private IrGenerator irGenerator;

    public ParseCoordinator(Scanner scanner) {
        this.scanner = scanner;
        runParse();
    }

    private void runParse() {
        ParseStream parseStream = new ParseStream();

        Parser parser = new Parser(scanner, parseStream);

        irGenerator = new IrGenerator(parseStream);

        parser.parse();

        try {
            irGenerator.run();
            System.out.println(irGenerator.getIr());

        } catch (SemanticException exc) {
            System.out.println(exc.getMessage());
            exc.printStackTrace();
        }
    }

    public SymbolTable getSymbolTable() {
        return irGenerator.getSymbolTable();
    }

    public LinearIr getIr() {
        return irGenerator.getIr();
    }
}
