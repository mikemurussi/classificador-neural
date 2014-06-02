package redeneural.mlp;

/**
 *
 * @author Michael Murussi
 */
public interface BackpropagationStatus {

    void start(int max);
    void status(int pos, double emqTreinamento, double emqValidacao);
    void done();
    void error(String message);

}
