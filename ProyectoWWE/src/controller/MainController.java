package controller;

import controller.equipo.EquipoJFrameController;
import controller.estadisticas.EstadisticasJFrameController;
import controller.luchador.LoitadorJFrameController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import controller.marca.MarcaJFrameController;
import controller.ppv.PPVJFrameController;
import controller.titulo.TituloJFrameController;
import java.awt.Image;
import java.sql.SQLException;
import main.OperacionsBD;
import view.MainJFrame;
import view.equipo.EquipoJFrame;
import view.estadisticas.EstadisticasJFrame;
import view.login.LoginFrame;
import view.luchador.LuchadorJFrame;
import view.marca.MarcaJFrame;
import view.ppv.PpvJFrame;
import view.titulo.TituloJFrame;

/**
 *
 * @author iagom
 */
public class MainController {
   private MainJFrame view;
   private Image icon = java.awt.Toolkit.getDefaultToolkit().getImage(LoginFrame.class.getResource("/res/wwe-64.png"));

    public MainController(MainJFrame view) {
        this.view = view;
        this.view.setIconImage(icon);
        this.view.addMarcaListener(getMenuListener("Marca"));
        this.view.addEquipoListener(getMenuListener("Equipo"));
        this.view.addLuchadorListener(getMenuListener("Luchador"));
        this.view.addPPVListener(getMenuListener("PPV"));
        this.view.addTituloListener(getMenuListener("Titulo"));
        this.view.addEstadisticasListener(getMenuListener("Estadisticas"));
        this.view.addQuitListener(getQuitListener());
    }

    private ActionListener getMenuListener(String opcion) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (opcion) {
                    case "Marca":
                        MarcaJFrame mf = new MarcaJFrame();
                        new MarcaJFrameController(mf, OperacionsBD.getConexion());
                        mf.setVisible(true);
                        break;
                    case "Equipo":
                        EquipoJFrame ef = new EquipoJFrame();
                        new EquipoJFrameController(ef);
                        ef.setVisible(true);
                        break;
                    case "Luchador":
                        LuchadorJFrame lv = new LuchadorJFrame();
                        new LoitadorJFrameController(lv, OperacionsBD.getConexion());
                        lv.setVisible(true);
                        break;
                    case "PPV":
                        PpvJFrame pf = new PpvJFrame();
                        new PPVJFrameController(pf, OperacionsBD.getConexion());
                        pf.setVisible(true);
                        break;
                    case "Titulo":
                        TituloJFrame tf = new TituloJFrame();
                        new TituloJFrameController(tf, OperacionsBD.getConexion());
                        tf.setVisible(true);
                        break;
                    case "Estadisticas":
                        EstadisticasJFrame esf = new EstadisticasJFrame();
                        new EstadisticasJFrameController(esf, OperacionsBD.getConexion());
                        esf.setVisible(true);
                        break;
                }
            }
        };
    }

    private ActionListener getQuitListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmar = JOptionPane.showConfirmDialog(view, 
                        "¿Estás seguro de que quieres salir?", 
                        "Salir del sistema", 
                        JOptionPane.YES_NO_OPTION);
                
                if (confirmar == JOptionPane.YES_OPTION) {
                    try {
                        OperacionsBD.cerrarConexion();
                        System.exit(0);
                    } catch (SQLException ex) {
                        System.getLogger(MainController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    }
                }
            }
        };
    }
}
