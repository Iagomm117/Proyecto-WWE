package controller.estadisticas;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import main.OperacionsBD;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import view.estadisticas.EstadisticasJFrame;

public class EstadisticasJFrameController {

    private EstadisticasJFrame vista;
    private OperacionsBD dao;
    private Connection conn;

    public EstadisticasJFrameController(EstadisticasJFrame vista, Connection conn) {
        this.vista = vista;
        this.conn = conn;
        this.dao = new OperacionsBD();

        initEventos();
        cargarEstadisticas();
    }

    private void initEventos() {
        vista.getBtnRpt1().addActionListener(e -> ejecutarReporteLuchadorCombates());
        vista.getBtnRpt2().addActionListener(e -> ejecutarReporteLuchadorConsultas());
        vista.getBtnRpt3().addActionListener(e -> ejecutarReportePpvEdiciones());
        vista.getBtnRpt4().addActionListener(e -> ejecutarReporteMarcaLuchadores());

        vista.getBtnReporteLuchadores().addActionListener(e -> ejecutarReporteLuchadoresModificados());
        vista.getBtnReporteTitulos().addActionListener(e -> ejecutarReporteTitulosModificados());
    }

    private void ejecutarReporteLuchadorCombates() {
        generarReporte("src/reports/loitadorPPV.jrxml", "Reporte: Luchador/Combates");
    }

    private void ejecutarReporteLuchadorConsultas() {
        generarReporte("src/reports/loitadorConsultado.jrxml", "Reporte: Luchador/Consultas");
    }

    private void ejecutarReportePpvEdiciones() {
        generarReporte("src/reports/edicionesPPV.jrxml", "Reporte: PPV/Ediciones");
    }

    private void ejecutarReporteMarcaLuchadores() {
        generarReporte("src/reports/loitadoresMarca.jrxml", "Reporte: Marca/Luchadores");
    }

    private void ejecutarReporteLuchadoresModificados() {
        generarReporte("src/reports/loitadorModificado.jrxml", "Auditoría: Luchadores Modificados");
    }

    private void ejecutarReporteTitulosModificados() {
        generarReporte("src/reports/loitadorModificado.jrxml", "Auditoría: Títulos Modificados");
    }

    private void generarReporte(String rutaJrxml, String titulo) {
        try {
            JasperReport reporte = JasperCompileManager.compileReport(rutaJrxml);
            JasperPrint impresion = JasperFillManager.fillReport(reporte, new HashMap<>(), conn);
            JasperViewer visor = new JasperViewer(impresion, false);
            visor.setTitle(titulo);
            visor.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al generar el informe: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
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
