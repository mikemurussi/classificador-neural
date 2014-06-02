package redeneural.mlp.funcao;

import java.io.Serializable;

/**
 *
 * @author Michael Murussi
 */
public abstract class Funcao implements Serializable {

    public abstract double calcula(double entrada);
    public abstract double derivada(double saida);

}
