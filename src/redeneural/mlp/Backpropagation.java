package redeneural.mlp;

/**
 *
 * @author Michael Murussi
 */
public class Backpropagation implements Runnable {

    private BackpropagationStatus status;
    private final Rede rede;
    private final double coeficienteAprendizado;
    private final int limiteIteracoes;
    private final double limiteErro;
    private boolean interrompido;

    AmostrasRede amostrasTreinamento;
    AmostrasRede amostrasValidacao;

    // resultados do treinamento
    private int numIteracoes;
    private double erroMedioQuadrado;

    public Backpropagation(Rede rede, double coeficienteAprendizado, int limiteIteracoes, double limiteErro) {
        this.rede = rede;
        this.coeficienteAprendizado = coeficienteAprendizado;
        this.limiteIteracoes = limiteIteracoes;
        this.limiteErro = limiteErro;
    }

    @Override
    public void run() {

        this.interrompido = false;
        
        double[][] entrada = amostrasTreinamento.getEntrada();
        double[][] saida = amostrasTreinamento.getSaida();

        double[][] entradaValidacao = amostrasValidacao != null ? amostrasValidacao.getEntrada() : null;
        double[][] saidaValidacao = amostrasValidacao != null ? amostrasValidacao.getSaida() : null;

        double emq = -1.0;

        if (status != null) status.start(limiteIteracoes);
        try {
            int i = 0;
            while (!interrompido && (i < limiteIteracoes) && ((emq == -1.0) || (emq > limiteErro))) {

                /* treinamento */
                double erro = 0.0;

                for (int p = 0; p < entrada.length; p++) {

                    // submete entrada à rede
                    rede.propaga(entrada[p]);

                    // retropropagação
                    retropropagacao(saida[p]);

                    // calcula e soma erro instantâneo
                    erro += calculaErroInstantaneo(rede, saida[p]);

                }

                // calcula erro médio quadrado
                emq = erro / entrada.length;

                /* validação */
                double emqValidacao = 0.0;
                if (entradaValidacao != null && saidaValidacao != null) {
                    erro = 0.0;

                    for (int p = 0; p < entradaValidacao.length; p++) {

                        // submete entrada à rede
                        rede.propaga(entradaValidacao[p]);

                        // calcula e soma erro instantâneo
                        erro += calculaErroInstantaneo(rede, saidaValidacao[p]);

                    }

                    // calcula erro médio quadrado
                    emqValidacao = (erro / entradaValidacao.length);
                }

                i++;
                if (status != null) status.status(i, emq, emqValidacao);
            }

            this.numIteracoes = i;
            this.erroMedioQuadrado = emq;
            
        } catch (Exception ex) {
            status.error(ex.toString());
        } finally {
            if (status != null) status.done();
        }
    }

    public double calculaErroInstantaneo(Rede rede, double[] saida) {
        Neuronio[] neuronios = rede.getCamadaSaida().getNeuronios();
        double e = 0.0;
        for (int j = 0; j < neuronios.length; j++) {
            e += Math.pow(saida[j] - neuronios[j].getSaida(), 2);
        }
        return (e / 2.0);
    }

    public void retropropagacao(double[] saida) {

        Camada[] camadas = rede.getCamadas();
        double[][] erro = new double[camadas.length][];

        // calcula os termos de erro
        for (int h = camadas.length - 1; h > 0; h--) {
            erro[h] = new double[camadas[h].getNeuronios().length];

            Neuronio[] neuronios = camadas[h].getNeuronios();

            // se for a camada de saída
            if (camadas[h] == rede.getCamadaSaida()) {
                for (int k = 0; k < neuronios.length; k++) {
                    erro[h][k] = (saida[k] - neuronios[k].getSaida()) * neuronios[k].getFuncao().derivada(neuronios[k].getSaida());
                }
            } else {
                // neurônios da camada posterior
                Neuronio[] nn = camadas[h + 1].getNeuronios();

                for (int j = 0; j < neuronios.length; j++) {
                    double s = 0.0;
                    for (int k = 0; k < nn.length; k++) {
                        s += erro[h + 1][k] * nn[k].getPesos()[j];
                    }
                    erro[h][j] = neuronios[j].getFuncao().derivada(neuronios[j].getSaida()) * s;
                }
            }
        }

        // atualiza os pesos
        for (int h = camadas.length - 1; h > 0; h--) {
            Neuronio[] neuronios = camadas[h].getNeuronios();
            for (int j = 0; j < neuronios.length; j++) {
                double pesos[] = neuronios[j].getPesos();
                for (int i = 0; i < pesos.length; i++) {
                    pesos[i] += coeficienteAprendizado * erro[h][j] * neuronios[j].getEntrada(i);
                }
            }            
        }

    }

    public double getErroMedioQuadrado() {
        return erroMedioQuadrado;
    }

    public int getNumIteracoes() {
        return numIteracoes;
    }

    public void setAmostrasTreinamento(AmostrasRede amostrasTreinamento) {
        this.amostrasTreinamento = amostrasTreinamento;
    }

    public void setAmostrasValidacao(AmostrasRede amostrasValidacao) {
        this.amostrasValidacao = amostrasValidacao;
    }

    public void setStatus(BackpropagationStatus status) {
        this.status = status;
    }

    public void interromper() {
        interrompido = true;
    }
}
