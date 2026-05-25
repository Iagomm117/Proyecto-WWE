package controller.login;

import controller.login.LoginController;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import main.OperacionsBD;
import service.Seguridad;
import view.login.LoginFrame;
import view.usuarios.RegisterFrame;

/**
 *
 * @author iagom
 */
public class RegisterController {
    private RegisterFrame view;

    public RegisterController(RegisterFrame view) {
        this.view = view;
        this.view.addRegisterButtonActionListener(getRegistrarListener());
        this.view.addLoginButtonActionListener(getVolverLoginListener());
    }

    private ActionListener getRegistrarListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreUsuario = view.getUserTextField().trim();
                String email = view.getEmailTextField().trim();
                String pass = view.getTextPasswordField();

                if (nombreUsuario.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                    view.setTextMensajeLabel("Error: Faltan datos.");
                    return;
                }
                
                String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
                if (!email.matches(emailRegex)) {
                    view.setTextMensajeLabel("Error: El email no es válido.");
                    return;
                }
                
                if (pass.length() < 4) {
                    view.setTextMensajeLabel("Clave demasiado corta.");
                    return;
                }

                String passHash = Seguridad.encryptSHA256(pass);

                String sql = "INSERT INTO usuario (nome_usuario, email, contrasinal_hash) VALUES (?, ?, ?)";
                
                try (Connection con = OperacionsBD.getConexion()) {
                    if (con == null) return;
                    
                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setString(1, nombreUsuario);
                        ps.setString(2, email);
                        ps.setString(3, passHash);

                        ps.executeUpdate();
                        
                        JOptionPane.showMessageDialog(view, "¡Usuario " + nombreUsuario + " registrado!");
                        volverAlLogin();
                    }
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == 1062) {
                        view.setTextMensajeLabel("El usuario o email ya existe.");
                    } else {
                        view.setTextMensajeLabel("Error BD: " + ex.getMessage());
                    }
                }
            }
        };
    }

    private ActionListener getVolverLoginListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                volverAlLogin();
            }
        };
    }

    private void volverAlLogin() {
        view.dispose();
        LoginFrame lf = new LoginFrame();
        new LoginController(lf); 
        lf.setVisible(true);
        lf.setLocationRelativeTo(null);
    }
}