package redeneural.mlp;

import java.awt.Point;
import java.io.Serializable;

/**
 *
 * @author Luiz Felipe Bertoldi de Oliveira
 */
public class AreaDelimitada implements Serializable {
    public enum Tipo { 
        CIRCULO, RETANGULO
    };

    private Tipo tipo;
    protected Point pontoOrigem;
    protected Point pontoFinal;
    protected int tamanho;
    
    public AreaDelimitada() {
        this.pontoOrigem = new Point();
        this.pontoFinal = new Point();
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
    
    public Tipo getTipo() {
        return tipo;
    }

    public Point getPontoOrigem() {
        return pontoOrigem;
    }

    public void setPontoOrigem(Point pontoOrigem) {
        this.pontoOrigem = pontoOrigem;
    }
    
    public Point getPontoFinal() {
        return pontoFinal;
    }

    public void setPontoFinal(Point pontoFinal) {
        this.setPontos(this.pontoOrigem, pontoFinal);
    }
    
    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }
    
    public void setPontos(Point pontoOrigem, Point pontoFinal) {
        this.pontoOrigem = pontoOrigem;
        this.pontoFinal = pontoFinal;
        /*
        if(pontoOrigem.x < pontoFinal.x) {
            this.pontoOrigem.x = pontoOrigem.x;
            this.pontoFinal.x = pontoFinal.x;
        } else {
            this.pontoOrigem.x = pontoFinal.x;
            this.pontoFinal.x = pontoOrigem.x;
        }
        if(pontoOrigem.y < pontoFinal.y) {
            this.pontoOrigem.y = pontoOrigem.y;
            this.pontoFinal.y = pontoFinal.y;
        } else {
            this.pontoOrigem.y = pontoFinal.y;
            this.pontoFinal.y = pontoOrigem.y;
        }
        if(this.tipo.equals(AreaDelimitada.Tipo.CIRCULO)) {
            this.tamanho = (this.pontoOrigem.x - this.pontoFinal.x) * (this.pontoOrigem.x - this.pontoFinal.x) + (this.pontoOrigem.y - this.pontoFinal.y) * (this.pontoOrigem.y - this.pontoFinal.y);
        }
        */
    }
    
    public Boolean isArea(int x, int y) {
        if(this.tipo.equals(Tipo.CIRCULO)) {
            return this.tamanho >= (this.pontoOrigem.x - x) * (this.pontoOrigem.x - x) + (this.pontoOrigem.y - y) * (this.pontoOrigem.y - y);
        }
        
        // Tipo.QUADRADO
        return this.pontoOrigem.x <= x && this.pontoFinal.x >= x && this.pontoOrigem.y <= y && this.pontoFinal.y >= y;
    }
}
