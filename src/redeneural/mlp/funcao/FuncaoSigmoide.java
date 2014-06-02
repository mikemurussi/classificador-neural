package redeneural.mlp.funcao;

/**
 *
 * @author Michael Murussi
 */
public class FuncaoSigmoide extends Funcao {

    @Override
    public double calcula(double entrada) {
        return 1.0 / (1.0 + Math.exp(-entrada));
    }

    @Override
    public double derivada(double saida) {
        return (saida * (1 - saida));
    }

}
