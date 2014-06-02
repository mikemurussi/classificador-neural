package redeneural.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

/**
 *
 * @author Michael Murussi
 */
public class AmostrasCsvWriter implements AmostrasWriter {

    @Override
    public void write(File file, double[][] amostras) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            exporta(new PrintStream(fos), amostras);
        }
    }

    private static void exporta(PrintStream printStream, double[][] amostras) {

        for (double[] dy: amostras) {
            for (double dx: dy) {
                printStream.printf(Locale.ENGLISH, "%f;", dx);
            }
            printStream.println();
        }
        printStream.flush();

    }

}
