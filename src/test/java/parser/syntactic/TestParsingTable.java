package parser.syntactic;

import org.junit.Test;
import parser.syntactic.Parser;
import parser.syntactic.ParsingTable;
import parser.syntactic.Rule;
import scanner.DirectScanner;
import scanner.Scanner;
import scanner.TestDirectScanner;
import util.Csv;
import util.General;

public class TestParsingTable {

    @Test
    public void testGrammarForAmbiguities() {
        ParsingTable parsingTable = new ParsingTable(Rule.ALL_RULES, true);
    }

    @Test
    public void testPrintParsingTable() {
        Scanner scanner = new DirectScanner(TestDirectScanner.SAMPLE_PROGRAM_TEXT);

        Parser parser = new Parser(scanner);

        System.out.println(parser.parsingTable.toString());
    }

    @Test
    public void printRules() {
        Csv csv = new Csv("GrammarSymbol", "Expansion");

        for (Rule rule : Rule.ALL_RULES) {
            csv.addRow(rule.getNonTerminalExpanded().name(), General.expansionToString(rule.getExpansion()));
        }

        System.out.println(csv.toString());
    }



}
