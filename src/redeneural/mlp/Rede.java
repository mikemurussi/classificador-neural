package redeneural.mlp;

import java.io.Serializable;
import java.util.Random;
import redeneural.mlp.funcao.Funcao;

/**
 *
 * @author Michael Murussi
 */
public class Rede implements Cloneable, Serializable {

    private Camada[] camadas;

    /**
     * Cria a rede de acordo com o parâmetro "numNeuroniosCamada".
     * @param numNeuroniosCamada Número de neurônios por camada
     * @param funcao Função utilizada
     */
    public Rede(int[] numNeuroniosCamada, Funcao funcao) {
        camadas = new Camada[numNeuroniosCamada.length];
        for (int i = 0; i < camadas.length; i++) {
            if (i == 0)
                camadas[i] = new Camada(null, numNeuroniosCamada[i], funcao);
            else
                camadas[i] = new Camada(camadas[i-1], numNeuroniosCamada[i], funcao);
        }
    }

    public void propaga(double[] entrada) {
        // submete a entrada à rede
        Neuronio[] neuronios = camadas[0].getNeuronios();
        for(int i = 0; i < entrada.length; i++) neuronios[i].setEntrada(entrada[i]);

        // calcula camadas escondidas e de saída da rede
        for(int i = 1; i < camadas.length; i++) {
            camadas[i].calcula();
        }
    }

    public void inicializaPesos(double intervaloInicial, double intervaloFinal, long semente) {
        Random random = new Random();
        if (semente > 0) {
            random.setSeed(semente);
        }

        for(int i = 1; i < camadas.length; i++) {
            for (Neuronio n: camadas[i].getNeuronios()) {
                double[] pesos = n.getPesos();
                for (int j = 0; j < pesos.length; j++) {
                    if (j == pesos.length - 1)
                        pesos[j] = 1.0; // peso inicial do bias fixo em 1
                    else
                        pesos[j] = random.nextDouble() * (intervaloFinal - intervaloInicial) + intervaloInicial;
                }
            }
        }
    }

    public Camada[] getCamadas() {
        return camadas;
    }

    public Camada getCamadaSaida() {
        return camadas[camadas.length-1];
    }

    public Camada getCamadaEntrada() {
        return camadas[0];
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Rede r = (Rede) super.clone();
        r.camadas = new Camada[this.camadas.length];
        
        for (int i = 0; i < this.camadas.length; i++) {
            if (i == 0) {
                r.camadas[i] = (Camada)this.camadas[i].clone(null);
            } else {
                r.camadas[i] = (Camada)this.camadas[i].clone(r.camadas[i-1]);
            }
        }
        
        return r;
    }

}
