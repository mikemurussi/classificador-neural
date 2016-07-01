package redeneural.mlp;

/**
 *
 * @author Luiz Felipe Bertoldi de Oliveira
 */
public class CirculoAreaDelimitada extends AreaDelimitada {
    public CirculoAreaDelimitada() {
        this.setTipo(Tipo.CIRCULO);
    }
    
    @Override
    public Boolean isArea(int x, int y) {
        return this.tamanho * this.tamanho >= (this.pontoOrigem.x - x) * (this.pontoOrigem.x - x) + (this.pontoOrigem.y - y) * (this.pontoOrigem.y - y);
    }
}
