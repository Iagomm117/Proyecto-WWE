package main;

import controller.login.LoginController;
import view.login.LoginFrame;

/**
 *
 * @author iagom
 */
public class ProyectoWWE {

    public static void main(String[] args) {
        LoginFrame vista = new LoginFrame();
        LoginController controlador = new LoginController(vista);
        vista.setVisible(true);
    }

}
