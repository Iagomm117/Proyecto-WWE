package controller.login;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import view.login.LoginFrame;
import javax.swing.JOptionPane;
import main.OperacionsBD;
import service.Seguridad;
import util.TemaUtil;
import view.login.RegisterFrame;

public class LoginController {

    private LoginFrame view;
    private Image icon = java.awt.Toolkit.getDefaultToolkit().getImage(LoginFrame.class.getResource("/res/wwe-64.png"));

    public LoginController(LoginFrame view) {
        this.view = view;
        this.view.setIconImage(icon);
        this.view.addLoginButtonActionListener(this.getLoginListener());
        this.view.addRegisterButtonActionListener(this.getRegisterListener());
        configurarBtnTema();

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

    private void configurarBtnTema() {
        int tamano = 32;
        ImageIcon sunIcon = getIconoRedimensionado("/res/sol.png", tamano, tamano);
        ImageIcon moonIcon = getIconoRedimensionado("/res/luna.png", tamano, tamano);

        view.getThemeButton().setIcon(TemaUtil.esModoOscuro() ? sunIcon : moonIcon);

        view.getThemeButton().addActionListener(e -> {
            TemaUtil.alternarTema();
            view.getThemeButton().setIcon(TemaUtil.esModoOscuro() ? sunIcon : moonIcon);
        });
    }

    private ImageIcon getIconoRedimensionado(String path, int ancho, int alto) {
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(path));
        Image image = originalIcon.getImage();
        Image newimg = image.getScaledInstance(ancho, alto, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
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
