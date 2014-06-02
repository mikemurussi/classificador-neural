package redeneural.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import redeneural.classificador.validacao.Validacao;

/**
 *
 * @author Michael Murussi
 */
public class ValidacaoCsvWriter extends ValidacaoWriter {

    public ValidacaoCsvWriter(Validacao validacao) {
        super(validacao);
    }

    @Override
    public void write(File file) throws FileNotFoundException, IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            salva(new PrintStream(fos));
        }
    }

    private void salva(PrintStream printStream) {

        int linhas = getValidacao().getAmostrasRede().getEntrada().length;
        int colunasEntrada = getValidacao().getAmostrasRede().getNumeroParametros();
        // saída desejada e obtida possuem o mesmo número de colunas
        int colunasSaida = getValidacao().getAmostrasRede().getNumeroClasses();

        // cabeçalho
        printStream.print("\"P\"");
        for(int j = 0; j < colunasEntrada; j++) {
            printStream.printf(";\"E%d\"", j);
        }
        for(int j = 0; j < colunasSaida; j++) {
            printStream.printf(";\"Y%d\"", j);
        }
        for(int j = 0; j < colunasSaida; j++) {
            printStream.printf(";\"O%d\"", j);
        }
        printStream.println();

        // dados
        double[][] entrada = getValidacao().getAmostrasRede().getEntrada();
        double[][] saidaDesejada = getValidacao().getSaidaDesejada();
        double[][] saidaObtida = getValidacao().getSaidaObtida();

        for (int i = 0; i < linhas; i++) {
            printStream.print(i);
            for (int j = 0; j < colunasEntrada; j++) {
                printStream.printf(";%f", entrada[i][j]);
            }
            for (int j = 0; j < colunasSaida; j++) {
                printStream.printf(";%f", saidaDesejada[i][j]);
            }
            for (int j = 0; j < colunasSaida; j++) {
                printStream.printf(";%f", saidaObtida[i][j]);
            }
            printStream.println();
        }

        printStream.flush();
    }

}
