package redeneural.mlp.funcao;

/**
 *
 * @author Michael Murussi
 */
public class FuncaoTangenteHiperbolica extends Funcao {

    @Override
    public double calcula(double entrada) {
        return (Math.exp(entrada) - Math.exp(-entrada)) / (Math.exp(entrada) + Math.exp(-entrada));
    }

    @Override
    public double derivada(double saida) {
        return (1 - Math.pow(saida, 2));
    }

}
