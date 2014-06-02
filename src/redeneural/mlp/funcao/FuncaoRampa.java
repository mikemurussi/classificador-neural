package redeneural.mlp.funcao;

/**
 *
 * @author Michael Murussi
 */
public class FuncaoRampa extends Funcao {

    private final double limiteInferior;
    private final double limiteSuperior;

    public FuncaoRampa(double limiteInferior, double limiteSuperior) {
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
    }

    @Override
    public double calcula(double entrada) {

        if (entrada < limiteInferior)
            return limiteInferior;
        else if (entrada > limiteSuperior)
            return limiteSuperior;
        else
            return entrada;

    }

    @Override
    public double derivada(double saida) {
        return 1;
    }

}