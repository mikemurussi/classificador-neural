package redeneural.mlp;

import java.io.Serializable;
import redeneural.mlp.funcao.Funcao;

/**
 *
 * @author Michael Murussi
 */
public class Camada implements Cloneable, Serializable {

    private Neuronio[] neuronios;

    public Camada(Camada camadaAnterior, int numNeuronio, Funcao funcao) {
        this.neuronios = new Neuronio[numNeuronio];
        for(int i = 0; i < neuronios.length; i++) {
            if (camadaAnterior == null)
                neuronios[i] = new Neuronio(funcao);
            else
                neuronios[i] = new Neuronio(camadaAnterior.neuronios, funcao);
        }
    }

    public Neuronio[] getNeuronios() {
        return neuronios;
    }

    public void calcula() {
        for (Neuronio n: neuronios) {
            n.calcula();
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Object clone(Camada camadaAnterior) throws CloneNotSupportedException {
        Camada c = (Camada) this.clone();
        c.neuronios = new Neuronio[this.neuronios.length];

        for (int i=0; i < this.neuronios.length; i++) {
            if (camadaAnterior != null) {
                c.neuronios[i] = (Neuronio) this.neuronios[i].clone(camadaAnterior.neuronios);
            } else {
                c.neuronios[i] = (Neuronio) this.neuronios[i].clone();
            }
        }
        return c;
    }

}
