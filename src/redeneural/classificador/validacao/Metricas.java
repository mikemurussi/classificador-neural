package redeneural.classificador.validacao;

import java.util.Arrays;
import java.util.List;
import redeneural.model.Classe;

/**
 *
 * @author Michael Murussi
 */
public final class Metricas {

    private final int classificacoesCorretas;
    private final int classificacoesIncorretas;
    private final int classificacoesIndeterminadas;
    private final int[][] matrizConfusao;
    private final List<Classe> classes;
    private final Classe[] classeEsperada;
    private final Classe[] classeObtida;
    private final double kappa;

    public Metricas(List<Classe> classes, Classe[] classeEsperada, Classe[] classeObtida) {

        this.classes = classes;
        this.classeEsperada = classeEsperada;
        this.classeObtida = classeObtida;
        
        int correto = 0;
        int incorreto = 0;
        int indeterminado = 0;

        for (int p=0; p < classeEsperada.length; p++) {
            if (classeEsperada[p] != null) {
                if (classeObtida[p] == null) {
                    indeterminado++;
                } else if (classeObtida[p].equals(classeEsperada[p])) {
                    correto++;
                } else {
                    incorreto++;
                }
            }
        }

        this.classificacoesCorretas = correto;
        this.classificacoesIncorretas = incorreto;
        this.classificacoesIndeterminadas = indeterminado;

        this.matrizConfusao = calculaMatrizConfusao(classes, classeEsperada, classeObtida);
        this.kappa = calculaKappa(matrizConfusao);
    }

    public Classe[] getClasseEsperada() {
        return classeEsperada;
    }

    public Classe[] getClasseObtida() {
        return classeObtida;
    }

    public List<Classe> getClasses() {
        return classes;
    }

    /**
     * Calcula a matriz de confusão, onde cada linha e cada coluna correspondem
     * a uma classe, e a última coluna corresponde a classificações indecisas
     * 
     * @param classes
     * @param classeEsperada
     * @param classeObtida
     * @return 
     */
    private static int[][] calculaMatrizConfusao(List<Classe> classes, Classe[] classeEsperada, Classe[] classeObtida) {
                
        int numClasses = classes.size();

        int[][] m = new int[numClasses][numClasses + 1];
        // zera matriz
        for (int i=0; i < numClasses; i++) {
            for (int j=0; j < numClasses + 1; j++) {
                m[i][j] = 0;
            }
        }

        // calcula matriz
        for (int p=0; p < classeEsperada.length; p++) {
            int y = classeEsperada[p] != null ? classeEsperada[p].getNeuronio() : -1;
            int x = classeObtida[p] != null ? classeObtida[p].getNeuronio() : -1;

            if (y != -1) {
                if (x == -1) {
                    m[y][numClasses] = m[y][numClasses] + 1;
                } else {
                    m[y][x] = m[y][x] + 1;
                }
            }
        }
        return m;

    }

    private static double calculaKappa(int[][] matrizConfusao) {
        int m = matrizConfusao.length;
        int[] totalX = new int[m];
        int[] totalY = new int[m];
        int total = 0;

        Arrays.fill(totalX, 0);
        Arrays.fill(totalY, 0);

        for (int y = 0; y < m; y++) {
            for (int x = 0; x < m; x++) {
                totalX[x] += matrizConfusao[y][x];
                totalY[y] += matrizConfusao[y][x];
                total += matrizConfusao[y][x];
            }
        }

        double correto = 0;
        double chance = 0;
        for (int i = 0; i < m; i++) {
            correto += matrizConfusao[i][i];
            chance += (totalX[i] * totalY[i]);
        }
        chance = chance / (double)(total * total);
        correto = correto / (double)total;        

        if (chance < 1) {
            return (correto - chance) / (1 - chance);
        } else {
            return 1;
        }
    }

    public int getNumeroInstancias() {
        return classificacoesCorretas + classificacoesIncorretas + classificacoesIndeterminadas;
    }

    public int getClassificacoesCorretas() {
        return classificacoesCorretas;
    }

    public double getClassificacoesCorretasPerc() {
        return 100 * classificacoesCorretas / (double)getNumeroInstancias();
    }

    public int getClassificacoesIncorretas() {
        return classificacoesIncorretas;
    }

    public double getClassificacoesIncorretasPerc() {
        return 100 * classificacoesIncorretas / (double)getNumeroInstancias();
    }

    public int getClassificacoesIndeterminadas() {
        return classificacoesIndeterminadas;
    }

    public double getClassificacoesIndeterminadasPerc() {
        return 100 * classificacoesIndeterminadas / (double)getNumeroInstancias();
    }

    public int[][] getMatrizConfusao() {
        return matrizConfusao;
    }

    public double getKappa() {
        return kappa;
    }

}
