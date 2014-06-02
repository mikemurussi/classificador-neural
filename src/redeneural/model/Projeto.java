package redeneural.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import redeneural.mlp.Rede;

/**
 *
 * @author Michael Murussi
 */
public class Projeto implements Serializable {

    private Rede rede;
    private final List<Classe> classes;
    private double parametroCorte;
    private Color corIndeciso;
    private int vizinhos;
    private final boolean[] canaisAtivos;

    public Projeto() {
        this.classes = new ArrayList<>();
        this.parametroCorte = 0.5;
        this.corIndeciso = new Color(255, 0, 0);
        this.vizinhos = 1;
        this.canaisAtivos = new boolean[]{false, false, true};
    }

    public Rede getRede() {
        return rede;
    }

    public void setRede(Rede rede) {
        this.rede = rede;
    }

    public double getParametroCorte() {
        return parametroCorte;
    }

    public void setParametroCorte(double parametroCorte) {
        this.parametroCorte = parametroCorte;
    }

    public Color getCorIndeciso() {
        return corIndeciso;
    }

    public void setCorIndeciso(Color corIndeciso) {
        this.corIndeciso = corIndeciso;
    }

    public int getVizinhos() {
        return vizinhos;
    }

    public void setVizinhos(int vizinhos) {
        this.vizinhos = vizinhos;
    }

    public List<Classe> getClasses() {
        return classes;
    }

    public boolean[] getCanaisAtivos() {
        return canaisAtivos;
    }

    public void setCanalAtivo(int canal, boolean ativo) {
        this.canaisAtivos[canal] = ativo;
    }

    public int getQuantidadeCanaisAtivos() {
        int i = 0;
        for (boolean b: canaisAtivos) {
            if (b) i++;
        }
        return i;
    }

}
