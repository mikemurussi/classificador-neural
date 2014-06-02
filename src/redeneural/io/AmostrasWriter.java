package redeneural.io;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Michael Murussi
 */
public interface AmostrasWriter {

    void write(File file, double[][] amostras) throws IOException;

}
