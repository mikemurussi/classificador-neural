package redeneural.classificador;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import redeneural.model.Classe;
import redeneural.util.ImageUtil;

/**
 *
 * @author Michael Murussi
 */
public class MascaraSelecaoBuilder {

    private int width = 0;
    private int height = 0;

    public List<Classe> build(File arquivoMascara) throws IOException {

        BufferedImage mascara = ImageIO.read(arquivoMascara);        
        return build(mascara);

    }

    public List<Classe> build(BufferedImage mascara) {

        this.width = mascara.getWidth();
        this.height = mascara.getHeight();

        int[] mascaraData = ImageUtil.loadImageData(mascara);

        return carregaSelecao(mascaraData);

    }

    private List<Classe> carregaSelecao(int[] mascara) {

        Map<Integer, Classe> classesRGB = new HashMap<>(255);

        // prepara seleção
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int index = width * y + x;

                // classe
                Classe c = classesRGB.get(mascara[index]);
                if (c == null) {
                    int idx = classesRGB.size();
                    c = new Classe("Classe " + (idx+1));
                    c.setNeuronio(idx);
                    c.setCor(new Color(mascara[index]));
                    classesRGB.put(mascara[index], c);
                }
                c.addAmostra(new Point(x, y));

            }
        }

        return new ArrayList<>(classesRGB.values());

    }

}
