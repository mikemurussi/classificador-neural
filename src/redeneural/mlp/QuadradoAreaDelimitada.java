package redeneural.mlp;

import java.awt.Point;

/**
 *
 * @author Luiz Felipe Bertoldi de Oliveira
 */
public class QuadradoAreaDelimitada extends AreaDelimitada {
    public QuadradoAreaDelimitada() {
        this.setTipo(Tipo.CIRCULO);
    }
    
    @Override
    public Boolean isArea(int x, int y) {
        return this.pontoOrigem.x <= x && this.pontoFinal.x >= x && this.pontoOrigem.y <= y && this.pontoFinal.y >= y;
    }
}
