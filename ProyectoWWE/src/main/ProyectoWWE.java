package main;

import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme;
import controller.login.LoginController;
import java.awt.Image;
import javax.swing.UnsupportedLookAndFeelException;
import view.login.LoginFrame;

/**
 *
 * @author iagom
 */
public class ProyectoWWE {

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        FlatLightFlatIJTheme.setup();
        Image icon = java.awt.Toolkit.getDefaultToolkit().getImage(LoginFrame.class.getResource("/res/wwe-64.png"));
        LoginFrame vista = new LoginFrame();
        vista.setIconImage(icon);
        LoginController controlador = new LoginController(vista);
        vista.setVisible(true);
    }

}
