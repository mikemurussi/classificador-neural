package redeneural.mlp.funcao;

/**
 *
 * @author Michael Murussi
 */
public class FuncaoLimitadorRigido extends Funcao {

    @Override
    public double calcula(double entrada) {
        return entrada >= 0 ? 1 : -1;
    }

    @Override
    public double derivada(double saida) {
        return 1;
    }

}
