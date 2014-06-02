package redeneural.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Michael Murussi
 */
public class AmostrasCsvReader implements AmostrasReader {

    @Override
    public double[][] load(File file) throws FileNotFoundException, IOException {

        FileReader fileReader = new FileReader(file);
        try (BufferedReader buffReader = new BufferedReader(fileReader)) {
            List<List> linhas = new ArrayList<>();

            Pattern patternCampos = Pattern.compile("[\\-]?[0-9]+[\\.][0-9]+|[\\-]?([0-9])+");
            Matcher matcher;
            String linha;
            while ((linha = buffReader.readLine()) != null) {
                List<Double> campos = new ArrayList<>();
                matcher = patternCampos.matcher(linha);
                while (matcher.find()) {
                    campos.add(Double.parseDouble(matcher.group()));
                }
                linhas.add(campos);
            }

            double[][] dados = new double[linhas.size()][linhas.size() > 0 ? linhas.get(0).size() : 0];
            for (int i = 0; i < dados.length; i++) {
                List<Double> a = linhas.get(i);
                for (int j = 0; j < dados[i].length; j++) {
                    dados[i][j] = a.size() > j ? a.get(j) : 0.0;
                }
            }
            return dados;

        } finally {
            fileReader.close();
        }

    }

}
