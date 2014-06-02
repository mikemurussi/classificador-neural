package redeneural;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import redeneural.mlp.AmostrasRede;
import redeneural.mlp.Backpropagation;
import redeneural.mlp.Neuronio;
import redeneural.mlp.Rede;
import redeneural.mlp.funcao.FuncaoSigmoide;

/**
 *
 * @author Michael Murussi
 */
public class TreinoImagem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi());  
        registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi());

        int w, h;
        int[] pixels;
        int[] mascara;

        try {
            // imagem para treinamento
            BufferedImage image = ImageIO.read(new File("treino.tif"));
            w = image.getWidth();
            h = image.getHeight();
            pixels = image.getRGB(0, 0, w, h, null, 0, w);

            // máscara com os resultados esperados
            image = ImageIO.read(new File("mascara.tif"));
            mascara = image.getRGB(0, 0, w, h, null, 0, w);
        } catch (IOException ex) {
            System.err.println(ex.toString());
            return;
        }

        /*
        for (int col = 0; col < w; col++) {
            for (int lin = 0; lin < h; lin++) {
                int argb = pixels[w * lin + col];
                int a = (argb >> 24) & 0xff;
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;
            }
        }
        */

        double[][] entrada = new double[mascara.length][9];
        double[][] saida = new double[mascara.length][2];

        int corSolido = ((128 & 0xff) << 16) | ((128 & 0xff) << 8) | (128 & 0xff);
        int corPoro = ((255 & 0xff) << 16) | ((255 & 0xff) << 8) | (255 & 0xff);
        int qtdAmostras = 0;

        HashSet<Integer> hashSet = new HashSet<>();

        for (int lin = 1; lin < h - 1; lin++) {
            for (int col = 1; col < w - 1; col++) {
                int index = w * lin + col;

                int corMascara = mascara[index] & 0xffffff;

                if (!hashSet.contains(corMascara)) {
                    hashSet.add(corMascara);

                    int a = (corMascara >> 24) & 0xff;
                    int r = (corMascara >> 16) & 0xff;
                    int g = (corMascara >> 8) & 0xff;
                    int b = corMascara & 0xff;

                    System.out.println(a + " " + r + " " + g + " " + b);
                }

                if ((corMascara == corSolido) || (corMascara == corPoro)) {

                    entrada[qtdAmostras][0] = (pixels[w * (lin - 1) + (col - 1)] & 0xff) / 255d;
                    entrada[qtdAmostras][1] = (pixels[w * (lin - 1) + col] & 0xff) / 255d;
                    entrada[qtdAmostras][2] = (pixels[w * (lin - 1) + (col + 1)] & 0xff) / 255d;
                    entrada[qtdAmostras][3] = (pixels[w * lin + (col - 1)] & 0xff) / 255d;
                    entrada[qtdAmostras][4] = (pixels[index] & 0xff) / 255;
                    entrada[qtdAmostras][5] = (pixels[w * lin + (col + 1)] & 0xff) / 255d;
                    entrada[qtdAmostras][6] = (pixels[w * (lin + 1) + (col - 1)] & 0xff) / 255d;
                    entrada[qtdAmostras][7] = (pixels[w * (lin + 1) + col] & 0xff) / 255d;
                    entrada[qtdAmostras][8] = (pixels[w * (lin + 1) + (col + 1)] & 0xff) / 255d;

                    if (corMascara == corPoro) {
                        saida[qtdAmostras][0] = 1;
                        saida[qtdAmostras][1] = 0;
                    } else {
                        saida[qtdAmostras][0] = 0;
                        saida[qtdAmostras][1] = 1;
                    }

                    qtdAmostras++;
                }

            }
        }

        // ajusta arrays de acordo com a quantidade de amostras
        entrada = Arrays.copyOf(entrada, qtdAmostras);
        saida = Arrays.copyOf(saida, qtdAmostras);

        double[][] treinoEntrada = Arrays.copyOf(entrada, qtdAmostras / 2);
        double[][] treinoSaida = Arrays.copyOf(saida, qtdAmostras / 2);

        double[][] validacaoEntrada = Arrays.copyOfRange(entrada, qtdAmostras / 2 + 1, qtdAmostras);
        double[][] validacaoSaida = Arrays.copyOfRange(saida, qtdAmostras / 2 + 1, qtdAmostras);

        // treina a rede
        AmostrasRede amostrasTreino = new AmostrasRede(treinoEntrada, treinoSaida);
        AmostrasRede amostrasValidacao = new AmostrasRede(validacaoEntrada, validacaoSaida);

        int[] neuroniosCamada = {9, 18, 4, 2};
        Rede rede = new Rede(neuroniosCamada, new FuncaoSigmoide());
        rede.inicializaPesos(0, 1, 0);

        Backpropagation backpropagation = new Backpropagation(rede, 0.25, 1000, 0.01);
        backpropagation.setAmostrasTreinamento(amostrasTreino);
        backpropagation.setAmostrasValidacao(amostrasValidacao);
        backpropagation.run();

        System.out.println("Iterações: " + backpropagation.getNumIteracoes());
        System.out.println("EMQ: " + backpropagation.getErroMedioQuadrado());
        
        // classifica imagem
        int[] data = new int[pixels.length];
        double[] entradaRede = new double[9];
        Neuronio[] neuronios = rede.getCamadaSaida().getNeuronios();
        double parametroCorte = 0.5;
        int corIndeciso = (255 << 16);

        for (int lin = 1; lin < h - 1; lin++) {
            for (int col = 1; col < w - 1; col++) {
                entradaRede[0] = (pixels[w * (lin - 1) + (col - 1)] & 0xff) / 255d;
                entradaRede[1] = (pixels[w * (lin - 1) + col] & 0xff) / 255d;
                entradaRede[2] = (pixels[w * (lin - 1) + (col + 1)] & 0xff) / 255d;
                entradaRede[3] = (pixels[w * lin + (col - 1)] & 0xff) / 255d;
                entradaRede[4] = (pixels[w * lin + col] & 0xff) / 255d;
                entradaRede[5] = (pixels[w * lin + (col + 1)] & 0xff) / 255d;
                entradaRede[6] = (pixels[w * (lin + 1) + (col - 1)] & 0xff) / 255d;
                entradaRede[7] = (pixels[w * (lin + 1) + col] & 0xff) / 255d;
                entradaRede[8] = (pixels[w * (lin + 1) + (col + 1)] & 0xff) / 255d;

                rede.propaga(entradaRede);

                int cor;
                if (neuronios[0].getSaida() > parametroCorte && neuronios[1].getSaida() > parametroCorte) {
                    cor = corIndeciso;
                } else if (neuronios[0].getSaida() > parametroCorte) {
                    cor = corPoro;
                } else if (neuronios[1].getSaida() > parametroCorte) {
                    cor = corSolido;
                } else {
                    cor = corIndeciso;
                }

                data[w * lin + col] = cor;
            }
        }

        // grava imagem classificada
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, w, h, data, 0, w);
        try {
            ImageIO.write(image, "TIFF", new File("classificada.tif"));
        } catch (IOException ex) {
            Logger.getLogger(TreinoImagem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
