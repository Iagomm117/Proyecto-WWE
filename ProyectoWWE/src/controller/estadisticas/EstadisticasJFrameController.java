package controller.estadisticas;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import main.OperacionsBD;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
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
        generarReporte("loitadorPPV.jrxml", "Reporte: Luchador/Combates");
    }

    private void ejecutarReporteLuchadorConsultas() {
        generarReporte("loitadorConsultado.jrxml", "Reporte: Luchador/Consultas");
    }

    private void ejecutarReportePpvEdiciones() {
        generarReporte("edicionesPPV.jrxml", "Reporte: PPV/Ediciones");
    }

    private void ejecutarReporteMarcaLuchadores() {
        generarReporte("loitadoresMarca.jrxml", "Reporte: Marca/Luchadores");
    }

    private void ejecutarReporteLuchadoresModificados() {
        generarReporte("loitadorModificado.jrxml", "Auditoría: Luchadores Modificados");
    }

    private void ejecutarReporteTitulosModificados() {
        generarReporte("tituloModificado.jrxml", "Auditoría: Títulos Modificados");
    }

    private void generarReporte(String nombreArchivo, String titulo) {
        try {
            String ruta = "/reports/" + nombreArchivo.replace(".jrxml", ".jasper");
            InputStream reporteStream = getClass().getResourceAsStream(ruta);

            if (reporteStream == null) {
                throw new Exception("No se encontró el archivo: " + ruta + " dentro del JAR.");
            }

            JasperPrint impresion = JasperFillManager.fillReport(reporteStream, new HashMap<>(), conn);

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
