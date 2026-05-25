package controller.estadisticas;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import main.OperacionsBD;
import view.estadisticas.EstadisticasJFrame;

/**
 * @author iagom
 */
public class EstadisticasJFrameController {

    private EstadisticasJFrame vista;
    private OperacionsBD dao;

    public EstadisticasJFrameController(EstadisticasJFrame vista, Connection conn) {
        this.vista = vista;
        this.dao = new OperacionsBD();

        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        try {
            vista.setLuchadorCombates(dao.getLoitadorMaisCombatesPPV());
            vista.setLuchadorConsultado(dao.getLoitadorMaisConsultado());
            vista.setPpvFechas(dao.getPPVConMaisFechas());
            vista.setMarcaLuchadores(dao.getMarcaConMaisLoitadores());

            List<String> loitadoresSemanales = dao.getLoitadoresUltimaSemana();
            vista.setLuchadoresSemanales(String.join("\n", loitadoresSemanales));

            List<String> titulosSemanales = dao.getTitulosUltimaSemana();
            vista.setTitulosSemanales(String.join("\n", titulosSemanales));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error ao cargar estatísticas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}