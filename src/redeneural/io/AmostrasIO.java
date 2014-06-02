package redeneural.io;

import redeneural.util.FileUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Michael Murussi
 */
public final class AmostrasIO {

    public static double[][] load(File file) throws FileNotFoundException, IOException {

        String fileExt = FileUtil.getFileExtension(file);
        if (fileExt == null) throw new FileNotFoundException("Arquivo inválido");

        // adiciona formatos
        AmostrasReader csv = new AmostrasCsvReader();
        AmostrasReader xls = new AmostrasXlsReader();
        Map<String, AmostrasReader> map = new HashMap<>();
        map.put("csv", csv);
        map.put("txt", csv);
        map.put("xls", xls);
        map.put("xlsx", xls);

        // seleciona formato
        AmostrasReader reader = map.get(fileExt);
        if (reader == null) throw new IOException("Tipo de arquivo inválido");

        return reader.load(file);
        
    }

    public static void write(File file, double[][] amostras) throws IOException {

        String fileExt = FileUtil.getFileExtension(file);
        if (fileExt == null) throw new FileNotFoundException("Arquivo inválido");

        AmostrasCsvWriter csv = new AmostrasCsvWriter();
        AmostrasXlsWriter xls = new AmostrasXlsWriter();

        Map<String, AmostrasWriter> map = new HashMap<>();
        map.put("csv", csv);
        map.put("txt", csv);
        map.put("xls", xls);
        map.put("xlsx", xls);

        AmostrasWriter writer = map.get(fileExt);
        if (writer == null) throw new java.lang.IllegalArgumentException("Formato de arquivo não suportado!");
        writer.write(file, amostras);

    }

}
