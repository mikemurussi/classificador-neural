package redeneural.io;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import redeneural.model.Classe;

/**
 *
 * @author Michael Murussi
 */
public class SelecaoClasseReader {

    private final List<Classe> classes;

    public SelecaoClasseReader(List<Classe> classes) {
        this.classes = classes;
    }

    public List<Classe> load(File file) throws FileNotFoundException, IOException {

        FileReader fileReader = new FileReader(file);
        try (BufferedReader buffReader = new BufferedReader(fileReader)) {

            Map<String, Classe> classesMap = new HashMap<>();
            for (Classe c: classes) {
                classesMap.put(c.getNome(), c);
            }

            Pattern patternAmostra = Pattern.compile("([0-9]+)");
            Matcher matcher;
            String linha;
            Classe classe = null;
            while ((linha = buffReader.readLine()) != null) {
                if (!linha.isEmpty()) {
                    if (linha.charAt(0) == '[') {
                        String nome = linha.substring(1, linha.length() - 1);
                        classe = classesMap.get(nome);
                        if (classe == null) {
                            classe = new Classe(nome);
                            classe.setNeuronio(classesMap.size());
                            classesMap.put(nome, classe);
                        }
                    } else {
                        if (classe != null) {
                            matcher = patternAmostra.matcher(linha);
                            int x, y;
                            if (matcher.find()) {
                                x = Integer.parseInt(matcher.group());
                                if (matcher.find()) {
                                    y = Integer.parseInt(matcher.group());
                                    classe.addAmostra(new Point(x, y));
                                }
                            }
                        }
                    }
                }
            }

            return new ArrayList<>(classesMap.values());
        } finally {
            fileReader.close();
        }

    }

}
