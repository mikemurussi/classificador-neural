package redeneural.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import redeneural.classificador.validacao.Validacao;

/**
 *
 * @author Michael Murussi
 */
public abstract class ValidacaoWriter {

    private final Validacao validacao;

    public ValidacaoWriter(Validacao validacao) {
        this.validacao = validacao;
    }

    public abstract void write(File file) throws FileNotFoundException, IOException;

    public Validacao getValidacao() {
        return validacao;
    }

}
