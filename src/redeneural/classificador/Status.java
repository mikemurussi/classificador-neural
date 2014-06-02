package redeneural.classificador;

/**
 *
 * @author Michael Murussi
 */
public interface Status {

    void status(int pos);
    void done(Exception ex);

}
