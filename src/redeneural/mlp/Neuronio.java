package redeneural.mlp;

import java.io.Serializable;
import redeneural.mlp.funcao.Funcao;

/**
 *
 * @author Michael Murussi
 */
public class Neuronio implements Cloneable, Serializable {

    private static final double BIAS = 1.0;
    private final Funcao funcao;
    private double[] pesos;
    private Neuronio[] entradas;
    private double saida;    

    public Neuronio(Funcao funcao) {
        this.funcao = funcao;
    }

    public Neuronio(Neuronio[] entradas, Funcao funcao) {
        this.entradas = entradas;
        this.funcao = funcao;
        pesos = new double[entradas.length + 1];
    }

    /**
     * Simplesmente repassa o valor de entrada para a saída.
     * Utilizado como forma de entrada da rede.
     * @param entrada
     */
    public void setEntrada(double entrada) {
        this.saida = entrada;
    }

    public double getEntrada(int i) {
        // se for camada de entrada (entradas == null), entrada = saída
        if (entradas == null)
            return this.saida;
        else if (i < entradas.length)
            return entradas[i].getSaida();
        else
            return BIAS;
    }

    /**
     * Calcula a saída do neurônio
     * @return
     */
    public double calcula() {

        if (entradas == null) return 0;

        double soma = 0;
        for (int i = 0; i < entradas.length; i++) {
            soma += entradas[i].saida * pesos[i];
        }
        soma += pesos[entradas.length] * BIAS;

        saida = funcao.calcula(soma);

        return saida;
    }

    public double getSaida() {
        return saida;
    }

    public double[] getPesos() {
        return pesos;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Neuronio n = (Neuronio) super.clone();
        n.entradas = null;
        if (n.pesos != null) {
            n.pesos = new double[this.pesos.length];
            System.arraycopy(this.pesos, 0, n.pesos, 0, n.pesos.length);
        }        
        return n;
    }

    public Object clone(Neuronio[] entradas) throws CloneNotSupportedException {
        Neuronio n = (Neuronio) this.clone();
        n.entradas = entradas;
        return n;
    }

}
