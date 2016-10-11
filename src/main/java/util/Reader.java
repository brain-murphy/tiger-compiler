package util;

import java.io.*;

public class Reader {
    public String readFromFile(String fileName) {
        try {
            return readLines(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String readFromStandardIn() {
        return readLines(new InputStreamReader(System.in));
    }

    private String readLines(java.io.Reader reader) {
        BufferedReader bufferedReader = new BufferedReader(reader);

        StringBuilder stringBuilder = new StringBuilder();

        try {
            String line = bufferedReader.readLine();

            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");

                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString().trim();
    }
}
