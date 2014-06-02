package redeneural.classificador;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.imageio.ImageIO;
import redeneural.model.Classe;
import redeneural.model.Projeto;
import redeneural.util.ImageUtil;

/**
 *
 * @author Michael Murussi
 */
public class AmostraColetaBuilder {

    private final Projeto projeto;
    private double[][] amostrasTreinamento;
    private double[][] amostrasValidacao;   
    private List<Classe> classes;
    private int width = 0;
    private int height = 0;
    private double percValidacao = 0.33d;

    public AmostraColetaBuilder(Projeto projeto) {
        this.projeto = projeto;
    }

    public void build(File arquivoImagem, List<Classe> classes) throws IOException {

        BufferedImage imagem = ImageIO.read(arquivoImagem);
        build(imagem, classes);

    }

    public void build(BufferedImage imagem, List<Classe> classes) {

        this.classes = classes;
        this.width = imagem.getWidth();
        this.height = imagem.getHeight();

        int[] imageData = ImageUtil.loadImageData(imagem);

        preparaAmostras(imageData);

    }

    public void setPercValidacao(double percValidacao) {
        this.percValidacao = percValidacao;
    }

    public double[][] getAmostrasTreinamento() {
        return amostrasTreinamento;
    }

    public double[][] getAmostrasValidacao() {
        return amostrasValidacao;
    }

    public static int calculaQtdEntradas(int vizinhos) {
        return (vizinhos * 2 + 1) * (vizinhos * 2 + 1);
    }

    /**
     * Prepara as amostras de treino e validação, separando-as aleatoriamente e
     * proporcionalmente ao número de amostras em cada classe.
     *
     * @param image
     */
    private void preparaAmostras(int[] image) {

        int vizinhos = projeto.getVizinhos();
        int qtdEntradas = calculaQtdEntradas(vizinhos) * projeto.getQuantidadeCanaisAtivos();
        int qtdClasses = this.classes.size();
        int qtdAmostras = 0;
        for (Classe c: classes) {
            qtdAmostras += c.getSelecaoAmostras().size();
        }

        double[][] conjuntoTreinamento = new double[qtdAmostras][qtdEntradas + qtdClasses];
        double[][] conjuntoValidacao = new double[qtdAmostras][qtdEntradas + qtdClasses];

        int idxTreino = 0;
        int idxValidacao = 0;
        Random random = new Random();

        for (Classe classe: classes) {
            
            int v = 0, t = 0;

            Set<Point> amostras = classe.getSelecaoAmostras();

            // quantidades de cada conjunto para esta classe
            int qtdValidacao = (int)Math.round(amostras.size() * percValidacao);
            int qtdTreino = amostras.size() - qtdValidacao;

            int idx;
            double[][] conjunto;

            for (Point p: amostras) {

                boolean b = false;
                // tenta incluir no conjunto de validação enquanto não lotado
                if (v < qtdValidacao) {
                    // se já lotou o conjunto de treino, então joga direto para o de validação
                    if (t < qtdTreino) {
                        b = random.nextBoolean();
                    } else {
                        b = true;
                    }
                }
                if (b) {
                    conjunto = conjuntoValidacao;
                    idx = idxValidacao;
                    idxValidacao++;
                    v++;
                } else {
                    conjunto = conjuntoTreinamento;
                    idx = idxTreino;
                    idxTreino++;
                    t++;
                }

                Arrays.fill(conjunto[idx], 0);

                boolean[] canaisAtivos = projeto.getCanaisAtivos();

                // entrada = vizinhança
                int k = 0;
                for (int x = p.x - vizinhos; x <= p.x + vizinhos; x++) {
                    if (x >= 0 && x < width) {
                        for (int y = p.y - vizinhos; y <= p.y + vizinhos; y++) {
                            if (y >= 0 && y < height) {
                                int pixel = image[width * y + x];
                                if (canaisAtivos[0]) {
                                    conjunto[idx][k] = ((pixel >> 16) & 0xff) / 255d;
                                    k++;
                                }
                                if (canaisAtivos[1]) {
                                    conjunto[idx][k] = ((pixel >> 8) & 0xff) / 255d;
                                    k++;
                                }
                                if (canaisAtivos[2]) {
                                    conjunto[idx][k] = (pixel & 0xff) / 255d;
                                    k++;
                                }
                            }
                        }
                    }
                }

                // classe da amostra
                conjunto[idx][qtdEntradas + classe.getNeuronio()] = 1d;
            }

            
        }

        // atribui amostras de treinamento embaralhadas
        this.amostrasTreinamento = new double[idxTreino][];
        AmostraColetaBuilder.shuffleCopy(conjuntoTreinamento, amostrasTreinamento);

        this.amostrasValidacao = new double[idxValidacao][];
        AmostraColetaBuilder.shuffleCopy(conjuntoValidacao, amostrasValidacao);

    }

    /**
     * Copia array {@code orig} para {@code dest} embaralhando.
     * 
     * @param orig
     * @param dest 
     */
    private static void shuffleCopy(double[][] orig, double[][] dest) {

        List<Integer> indices = new ArrayList<>(dest.length);
        for (int i=0; i < dest.length; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
        int idx = 0;
        for(int i: indices) {
            dest[idx++] = orig[i];
        }
        
    }

}
