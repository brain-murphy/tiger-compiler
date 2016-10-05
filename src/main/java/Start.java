import scanner.DirectScanner;
import scanner.Scanner;
import util.Reader;

public class Start {
    public static void main(String[] args) {

        Reader reader = new Reader();

        if (args.length > 0) {
            for (String filePath : args) {
                String fileContents = reader.readFromFile(filePath);

                Scanner scanner = new DirectScanner(fileContents);
                // TODO parsing
            }
        } else {
            String programContents = reader.readFromStandardIn();

            Scanner scanner = new DirectScanner(programContents);
            // TODO parsing
        }
    }
}
