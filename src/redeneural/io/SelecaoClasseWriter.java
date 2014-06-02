package redeneural.io;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import redeneural.model.Classe;

/**
 *
 * @author Michael Murussi
 */
public class SelecaoClasseWriter {

    private final List<Classe> classes;

    public SelecaoClasseWriter(List<Classe> classes) {
        this.classes = classes;
    }

    public void write(File file) throws FileNotFoundException, IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            salva(new PrintStream(fos));
        }
    }

    private void salva(PrintStream printStream) {

        for (Classe c: classes) {
            printStream.println(String.format("[%s]", c.getNome()));
            for (Point p: c.getSelecaoAmostras()) {
                printStream.println(String.format("%d,%d", p.x, p.y));
            }
        }

    }

}
