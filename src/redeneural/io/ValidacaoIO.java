package redeneural.io;

import redeneural.util.FileUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import redeneural.classificador.validacao.Validacao;

/**
 *
 * @author Michael Murussi
 */
public final class ValidacaoIO {

    public static void write(File file, Validacao validacao) throws FileNotFoundException, IOException {

        String fileExt = FileUtil.getFileExtension(file);
        if (fileExt == null) throw new FileNotFoundException("Arquivo inválido");

        // adiciona formatos
        ValidacaoWriter csv = new ValidacaoCsvWriter(validacao);
        ValidacaoWriter xls = new ValidacaoXlsWriter(validacao);
        Map<String, ValidacaoWriter> map = new HashMap<>();
        map.put("csv", csv);
        map.put("txt", csv);
        map.put("xls", xls);
        map.put("xlsx", xls);

        // seleciona formato
        ValidacaoWriter writer = map.get(fileExt);
        if (writer == null) throw new java.lang.IllegalArgumentException("Formato de arquivo não suportado!");
        writer.write(file);

    }

}
