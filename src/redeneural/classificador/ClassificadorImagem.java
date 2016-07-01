package redeneural.classificador;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import redeneural.mlp.AreaDelimitada;
import redeneural.util.FileUtil;
import redeneural.util.ImageUtil;
import redeneural.mlp.Neuronio;
import redeneural.mlp.Rede;
import redeneural.model.Classe;
import redeneural.model.Projeto;

/**
 *
 * @author Michael Murussi
 */
public final class ClassificadorImagem implements Runnable {

    private final File imageIn;
    private final File imageOut;
    private final Rede rede;
    private final List<Classe> classes;
    private final int vizinhos;
    private final double parametroCorte;
    private final Color corIndeciso;
    private final Color corFundo;
    private final boolean removerFundo;
    private final int qtdCanaisAtivos;
    private final boolean[] canaisAtivos;
    private final AreaDelimitada areaDelimitada;
    
    public ClassificadorImagem(Projeto projeto, File imageIn, File imageOut, Color corFundo, boolean removerFundo) throws CloneNotSupportedException {
        this.rede = (Rede)projeto.getRede().clone();
        this.classes = projeto.getClasses();
        this.vizinhos = projeto.getVizinhos();
        this.parametroCorte = projeto.getParametroCorte();
        this.corIndeciso = projeto.getCorIndeciso();
        this.imageIn = imageIn;
        this.imageOut = imageOut;
        this.qtdCanaisAtivos = projeto.getQuantidadeCanaisAtivos();
        this.canaisAtivos = projeto.getCanaisAtivos();
        this.corFundo = corFundo;
        this.removerFundo = removerFundo;
        this.areaDelimitada = projeto.getAreaDelimitada();
    }

    public void classifica(File imageIn, File imageOut) throws IOException {

        BufferedImage a = ImageIO.read(imageIn);
        BufferedImage b = new BufferedImage(a.getWidth(), a.getHeight(), BufferedImage.TYPE_INT_RGB);

        classifica(a, b);

        String formato;
        if (FileUtil.getFileExtension(imageOut).equalsIgnoreCase("tif")) {
            formato = "TIFF";
        } else {
            formato = "PNG";
        }

        ImageIO.write(b, formato, imageOut);

    }

    public void classifica(BufferedImage imageIn, BufferedImage imageOut) {

        int[] dataIn = ImageUtil.loadImageData(imageIn);
        int[] dataOut = new int[dataIn.length];
        Arrays.fill(dataOut, Color.white.getRGB());

        int qtdEntradas = ((vizinhos * 2 + 1) * (vizinhos * 2 + 1)) * qtdCanaisAtivos;

        // validação da rede
        if (rede.getCamadaEntrada().getNeuronios().length != qtdEntradas) {
            throw new RuntimeException("Camada de entrada da rede incompatível");
        }
        if (rede.getCamadaSaida().getNeuronios().length != classes.size()) {
            throw new RuntimeException("Camada de saída da rede incompatível");
        }

        double[] entrada = new double[qtdEntradas];
        Neuronio[] neuronios = rede.getCamadaSaida().getNeuronios();
        Classe[] mapeamentoNeuronioClasse = Classe.geraMapeamentoNeuronioClasse(classes);

        int h = imageIn.getHeight();
        int w = imageIn.getWidth();

        for (int i = vizinhos; i < h - vizinhos; i++) {
            for (int j = vizinhos; j < w - vizinhos; j++) {

                // entrada = vizinhança
                int k = 0;
                for (int vi = i - vizinhos; vi <= i + vizinhos; vi++) {
                    for (int vj = j - vizinhos; vj <= j + vizinhos; vj++) {
                        int pixel = dataIn[w * vi + vj];
                        if (canaisAtivos[0]) {
                            entrada[k] = ((pixel >> 16) & 0xff) / 255d;
                            k++;
                        }
                        if (canaisAtivos[1]) {
                            entrada[k] = ((pixel >> 8) & 0xff) / 255d;
                            k++;
                        }
                        if (canaisAtivos[2]) {
                            entrada[k] = (pixel & 0xff) / 255d;
                            k++;
                        }
                    }
                }

                rede.propaga(entrada);

                int neuronioVencedor = Classe.getNeuronioVencedor(neuronios, parametroCorte);

                if (neuronioVencedor > -1) {
                    dataOut[w * i + j] = mapeamentoNeuronioClasse[neuronioVencedor].getCor().getRGB();
                } else {
                    dataOut[w * i + j] = corIndeciso.getRGB();
                }

            }
        }

        if (removerFundo) {
            removeFundo(dataOut, h, w);
        }

        ImageUtil.saveImageData(imageOut, dataOut);

    }

    private void removeFundo(int[] data, int h, int w) {
        final int rgbFundo = corFundo.getRGB();
        
        if(this.areaDelimitada.getTipo() != null) {
            for(int i = 0; i < data.length; i++) {
                int x = i % w;
                int y = i / w;
                if(!this.areaDelimitada.isArea(x, y)) {
                    data[i] = rgbFundo;
                }
            }
        } else {
            // bordas não classificadas são consideradas fundo
            for (int j = 0; j < w; j++) {
                // borda superior
                for (int i = 0; i < vizinhos; i++) {
                    data[w * i + j] = rgbFundo;
                }
                // borda inferior
                for (int i = (h - vizinhos) - 1; i < h; i++) {
                    data[w * i + j] = rgbFundo;
                }
            }
            for (int i = 0; i < h; i++) {
                // borda esquerda
                for (int j = 0; j < vizinhos; j++) {
                    data[w * i + j] = rgbFundo;
                }
                // borda direita
                for (int j = (w - vizinhos) - 1; j < w; j++) {
                    data[w * i + j] = rgbFundo;
                }
            }

            // marca fundo a partir do canto superior esquerdo
            Point point = new Point(vizinhos, vizinhos);
            Stack<Point> stack = new Stack<>();
            stack.push(point);

            Point[] vizinhanca = new Point[8];
            for (int i=0; i < vizinhanca.length; i++) {
                vizinhanca[i] = new Point();
            }

            final int rgb = data[w * point.y + point.x];
            int px;

            while (!stack.empty()) {
                Point p = stack.pop();

                setVizinhanca(p, vizinhanca);
                for (Point pv : vizinhanca) {
                    if ((pv.x >= 0) && (pv.x < w) && (pv.y >= 0) && (pv.y < h)) {
                        px = data[w * pv.y + pv.x];
                        if (px == rgb && px != rgbFundo) {
                            stack.push(new Point(pv));
                            data[w * pv.y + pv.x] = rgbFundo;
                        }
                    }
                }
            }
        }
    }

    private static void setVizinhanca(Point p, Point[] v) {

        // em y-1
        v[0].setLocation(p.x - 1, p.y - 1);
        v[1].setLocation(p.x, p.y - 1);
        v[2].setLocation(p.x + 1, p.y - 1);
        // em y
        v[3].setLocation(p.x - 1, p.y);
        v[4].setLocation(p.x + 1, p.y);
        // em y+1
        v[5].setLocation(p.x - 1, p.y + 1);
        v[6].setLocation(p.x, p.y + 1);
        v[7].setLocation(p.x + 1, p.y + 1);        
        
    }

    @Override
    public void run() {
        try {
            classifica(imageIn, imageOut);
        } catch (IOException ex) {
            Logger.getLogger(ClassificadorImagem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
