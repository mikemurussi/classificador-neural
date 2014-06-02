package redeneural.util;

import java.io.File;
import java.util.Locale;

/**
 *
 * @author Michael Murussi
 */
public final class FileUtil {

    /**
     * Retorna a extensão do arquivo passado como parâmetro, ou null caso
     * o arquivo não possua extensão.
     *
     * @param file
     * @return Extensão do arquivo
     */
    public static String getFileExtension(File file) {

        if (file == null) return null;

        String fileName = file.getName();
        String fileExt = null;
        int i = fileName.lastIndexOf(".");
        if (i > 0 && i < fileName.length() - 1) {
            fileExt = fileName.substring(i + 1).toLowerCase(Locale.ENGLISH);
        }

        return fileExt;

    }

    public static String getFileNameWithoutExtension(File file) {

        if (file == null) return null;

        String fileExt = getFileExtension(file);
        String fileName = file.getName();
        return fileName.substring(0, fileName.length() - fileExt.length() - 1);

    }

}
