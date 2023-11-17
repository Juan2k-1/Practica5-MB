package App;

import Controlador.ControladorLogin;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 *
 * @author juald
 */
public class Practica4_MB {

    public static void main(String[] args) {
        try
        {
            UIManager.setLookAndFeel(new NimbusLookAndFeel()); 
        } catch (UnsupportedLookAndFeelException ex)
        {
            Logger.getLogger(Practica4_MB.class.getName()).log(Level.SEVERE, null, ex);
        }
        ControladorLogin c = new ControladorLogin();
    }
}