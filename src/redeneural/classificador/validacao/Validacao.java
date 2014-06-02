package redeneural.classificador.validacao;

import java.util.List;
import redeneural.mlp.AmostrasRede;
import redeneural.mlp.Neuronio;
import redeneural.mlp.Rede;
import redeneural.model.Classe;

/**
 *
 * @author Michael Murussi
 */
public class Validacao {
    
    private final List<Classe> classes;
    private final Rede rede;
    private final AmostrasRede amostrasRede;
    private final double parametroCorte;
    private double[][] saidaObtida = null;
    private Classe[] classeEsperada;
    private Classe[] classeObtida;
    private Metricas metricas;

    public Validacao(Rede rede, List<Classe> classes, AmostrasRede amostrasRede, double parametroCorte) {
        this.rede = rede;
        this.classes = classes;
        this.amostrasRede = amostrasRede;
        this.parametroCorte = parametroCorte;
    }
    
    /**
     * Retorna a saída obtida.
     * @return 
     */
    public double[][] getSaidaObtida() {
        return valida();
    }

    /**
     * Retorna array das classes esperadas.
     * Classe nula significa INDECISO.
     *
     * @return 
     */
    public Classe[] getClasseEsperada() {
        if (classeEsperada == null) valida();
        return classeEsperada;
    }

    /**
     * Retorna array das classes obtidas.
     * Classe nula significa INDECISO.
     * @return
     */
    public Classe[] getClasseObtida() {
        if (classeObtida == null) valida();
        return classeObtida;
    }

    public Metricas getMetricas() {
        if (metricas == null) valida();
        return metricas;
    }

    private double[][] valida() {
        if (this.saidaObtida != null) {
            return this.saidaObtida;
        }

        double[][] entrada = amostrasRede.getEntrada();
        double[][] saidaEsperada = amostrasRede.getSaida();
        this.saidaObtida = new double[entrada.length][saidaEsperada[0].length];

        // calcula saídas da rede
        Neuronio[] neuronios = this.rede.getCamadaSaida().getNeuronios();
        for (int p = 0; p < entrada.length; p++) {
            this.rede.propaga(entrada[p]);
            for (int i = 0; i < neuronios.length; i++) {
                saidaObtida[p][i] = neuronios[i].getSaida();
            }
        }        

        // calcula classes esperadas e obtidas
        this.classeEsperada = Classe.calculaClasse(classes, saidaEsperada, parametroCorte);
        this.classeObtida = Classe.calculaClasse(classes, saidaObtida, parametroCorte);
        this.metricas = new Metricas(classes, classeEsperada, classeObtida);

        return this.saidaObtida;
    }

    public double[][] getSaidaDesejada() {
        return this.amostrasRede.getSaida();
    }

    public Rede getRede() {
        return rede;
    }

    public AmostrasRede getAmostrasRede() {
        return amostrasRede;
    }

    public double getParametroCorte() {
        return parametroCorte;
    }

}
