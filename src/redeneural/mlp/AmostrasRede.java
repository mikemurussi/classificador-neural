package redeneural.mlp;

/**
 *
 * @author Michael Murussi
 */
public class AmostrasRede {

    private final double[][] entrada;
    private final double[][] saida;

    public AmostrasRede(double[][] entrada, double[][] saida) {
        this.entrada = entrada;
        this.saida = saida;
    }

    public double[][] getEntrada() {
        return entrada;
    }

    public double[][] getSaida() {
        return saida;
    }


    /**
     * Retorna o número de parâmetros de entrada da amostra.
     * O número de parâmetros é definido pelo número de colunas da entrada.
     *
     * @return Número de parâmetros de entrada na amostra
     */
    public int getNumeroParametros() {
        return this.entrada[0].length;
    }

    /**
     * Retorna o número de classes da amostra.
     * O número de classes é definido pelo número de colunas da saída esperada
     *
     * @return Número de classes na amostra
     */
    public int getNumeroClasses() {
        return saida[0].length;
    }

}
