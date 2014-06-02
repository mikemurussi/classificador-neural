package redeneural;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import redeneural.gui.MainJFrame;
import redeneural.util.ImageUtil;

/**
 *
 * @author Michael Murussi
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ImageUtil.registerServiceProviders();

        String laf = UIManager.getSystemLookAndFeelClassName();
        try {
            /*
            for (UIManager.LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    laf = info.getClassName();
                    break;
                }
            }
            */
            UIManager.setLookAndFeel(laf);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainJFrame().setVisible(true);
            }
        });

    }

}
