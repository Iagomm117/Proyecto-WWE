package controller.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import view.login.LoginFrame;
import javax.swing.JOptionPane;
import main.OperacionsBD;
import service.Seguridad;
import view.usuarios.RegisterFrame;

public class LoginController {

    private LoginFrame view;

    public LoginController(LoginFrame view) {
        this.view = view;
        this.view.addLoginButtonActionListener(this.getLoginListener());
        this.view.addRegisterButtonActionListener(this.getRegisterListener());
    }

    private ActionListener getLoginListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = view.getTextNombreTextField().trim();
                String pass = view.getTextPasswordField();

                String passHash = Seguridad.encryptSHA256(pass);

                try (Connection con = OperacionsBD.getConexion()) {
                    String sql = "SELECT * FROM usuario WHERE nome_usuario = ? AND contrasinal_hash = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, usuario);
                    ps.setString(2, passHash);

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        view.dispose();
                        view.MainJFrame mainView = new view.MainJFrame();
                        new controller.MainController(mainView);
                        mainView.setVisible(true);
                    } else {
                        view.setTextMensajeLabel("Credenciales incorrectas");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(view, "Error de BD: " + ex.getMessage());
                }
            }
        };
    }

    private ActionListener getRegisterListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.dispose();
                RegisterFrame rf = new RegisterFrame();
                new RegisterController(rf);
                rf.setVisible(true);
                rf.setLocationRelativeTo(null);
            }
        };
    }
}
