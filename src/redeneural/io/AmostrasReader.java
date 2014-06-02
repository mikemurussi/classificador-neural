package redeneural.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Michael Murussi
 */
public interface AmostrasReader {

    double[][] load(File file) throws FileNotFoundException, IOException;

}
