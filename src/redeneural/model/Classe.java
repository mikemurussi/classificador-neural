package redeneural.model;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import redeneural.mlp.Neuronio;

/**
 *
 * @author Michael Murussi
 */
public class Classe implements Serializable {

    private int neuronio;
    private String nome;
    private Color cor = Color.white;
    private final Set<Point> selecaoAmostras;

    public Classe(String nome) {
        this.nome = nome;
        this.selecaoAmostras = new HashSet<>(100);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Color getCor() {
        return cor;
    }

    public void setCor(Color cor) {
        this.cor = cor;
    }

    public int getNeuronio() {
        return neuronio;
    }

    public void setNeuronio(int neuronio) {
        this.neuronio = neuronio;
    }

    public Set<Point> getSelecaoAmostras() {
        return selecaoAmostras;
    }

    public void addAmostra(Point p) {
        this.selecaoAmostras.add(p);
    }

    public void removeAmostra(Point p) {
        this.selecaoAmostras.remove(p);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Classe other = (Classe) obj;
        return Objects.equals(this.nome, other.nome);
    }

    @Override
    public int hashCode() {
        return this.nome.hashCode();
    }

    public static int getNeuronioVencedor(Neuronio[] neuronios, double parametro) {
        int c = -1;
        for (int i = 0; i < neuronios.length; i++) {
            if (neuronios[i].getSaida() >= parametro) {
                if (c == -1) {
                    c = i;
                } else {
                    c = -1;
                    break;
                }
            }
        }
        return c;
    }

    public static int getNeuronioVencedor(double[] saidaNeuronios, double parametro) {
        int c = -1;
        for (int i = 0; i < saidaNeuronios.length; i++) {
            if (saidaNeuronios[i] >= parametro) {
                if (c == -1) {
                    c = i;
                } else {
                    c = -1;
                    break;
                }
            }
        }
        return c;
    }

    public static Classe[] geraMapeamentoNeuronioClasse(List<Classe> classes) {
        Classe[] m = new Classe[classes.size()];
        for (Classe c : classes) {
            m[c.getNeuronio()] = c;
        }
        return m;
    }

    /**
     * Calcula a classe para cada padrão apresentado em {@code saidaRede}.
     * Cada neurônio da camada de saída corresponde a uma classe.
     * Um neurônio está ativo se o seu valor for maior ou igual ao parâmetro
     * de corte {@code  parametroCorte}.
     * Se mais de um neurônio estiver ativo na camada de saída, o resultado é
     * considerado como "indeciso"
     * 
     * @param classes Classes
     * @param saidaRede Camada de saída para cada padrão p
     * @param parametroCorte parâmetro de corte
     * @return 
     */
    public static Classe[] calculaClasse(List<Classe> classes, double[][] saidaRede, double parametroCorte) {

        Classe[] classeSaida = new Classe[saidaRede.length];

        Classe[] mapeamentoNeuronioClasse = Classe.geraMapeamentoNeuronioClasse(classes);
        for (int p = 0; p < saidaRede.length; p++) {
            int neuronioVencedor = Classe.getNeuronioVencedor(saidaRede[p], parametroCorte);

            if (neuronioVencedor > -1) {
                classeSaida[p] = mapeamentoNeuronioClasse[neuronioVencedor];
            } else {
                classeSaida[p] = null;
            }
        }

        return classeSaida;
        
    }

}
