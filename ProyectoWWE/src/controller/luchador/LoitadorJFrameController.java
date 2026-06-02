package controller.luchador;

import controller.qr.QrJDialogController;
import java.awt.Component;
import java.awt.Dimension;
import model.Loitador;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import main.OperacionsBD;
import service.ServicioQr;
import service.YoutubeService;
import view.luchador.LuchadorJFrame;
import view.qr.QrJDialog;

public class LoitadorJFrameController {

    private LuchadorJFrame view;
    private OperacionsBD dao;
    private List<Loitador> listaLocal;
    private List<Loitador> listaFiltrada;
    private Loitador seleccionado = null;

    public LoitadorJFrameController(LuchadorJFrame view, java.sql.Connection conn) {
        this.view = view;
        this.dao = new OperacionsBD();
        this.listaLocal = new ArrayList<>();
        this.listaFiltrada = new ArrayList<>();

        this.view.addListaListener(this.getListSelectionListener());
        this.view.addBtnGuardarListener(this.getSaveListener());
        this.view.addBtnEliminarListener(this.getDeleteListener());
        this.view.addBtnLimpiarListener(this.getClearListener());
        this.view.addBtnQRListener(this.getQRListener());

        this.view.getTxtBuscar().getDocument().addDocumentListener(this.getBuscarDocumentListener());

        this.inicializarComboBoxes();
        this.cargarDatos();
    }

    private void inicializarComboBoxes() {
        DefaultComboBoxModel<Object> modeloEstado = new DefaultComboBoxModel<>();
        modeloEstado.addElement("activo");
        modeloEstado.addElement("retirado");
        modeloEstado.addElement("lesionado");
        modeloEstado.addElement("indefinido");
        view.getCbEstado().setModel(modeloEstado);

        DefaultComboBoxModel<Object> modeloPeso = new DefaultComboBoxModel<>();
        modeloPeso.addElement("Heavyweight");
        modeloPeso.addElement("Cruiserweight");
        modeloPeso.addElement("Light Heavyweight");
        modeloPeso.addElement("Women's");
        view.getCbPeso().setModel(modeloPeso);
    }

    private void cargarDatos() {
        try {
            listaLocal = dao.luchadorListar();
            filtrarDatos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error al cargar: " + e.getMessage());
        }
    }

    private void filtrarDatos() {
        String texto = view.getTxtBuscar().getText().trim().toLowerCase();
        listaFiltrada = new ArrayList<>();

        for (Loitador l : listaLocal) {
            if (texto.isEmpty() || (l.getNome() != null && l.getNome().toLowerCase().contains(texto))) {
                listaFiltrada.add(l);
            }
        }

        List<String> nombres = listaFiltrada.stream().map(Loitador::getNome).toList();
        view.actualizarLista(nombres);
    }

    private DocumentListener getBuscarDocumentListener() {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarDatos();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarDatos();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarDatos();
            }
        };
    }

    private ListSelectionListener getListSelectionListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int index = view.getIndexSeleccionado();
                    if (index != -1 && index < listaFiltrada.size()) {
                        seleccionado = listaFiltrada.get(index);
                        try {
                            dao.incrementarVecesConsultado(seleccionado.getId_loitador());
                        } catch (SQLException ex) {
                            System.err.println("Error al incrementar consulta: " + ex.getMessage());
                        }
                        view.setFormulario(
                                seleccionado.getNome(),
                                seleccionado.getEstado(),
                                seleccionado.getCategoria_peso(),
                                seleccionado.getEntrada(),
                                String.valueOf(seleccionado.getId_loitador()),
                                seleccionado.getFoto_url()
                        );
                    }
                }
            }
        };
    }

    private ActionListener getQRListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String busqueda = view.getEntrada() + " WWE Song";

                YoutubeService yt = new YoutubeService();
                String urlEncontrada = yt.buscarVideo(busqueda);

                if (urlEncontrada != null) {
                    mostrarPopUpQR(urlEncontrada);
                } else {
                    JOptionPane.showMessageDialog(view, "Non se puido atopar o video.");
                }
            }
        };
    }

    private void mostrarPopUpQR(String url) {
        QrJDialog qrView = new QrJDialog(view, true);
        QrJDialogController qrController = new QrJDialogController(qrView, url);
        qrView.setVisible(true);
    }

    private ActionListener getSaveListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = view.getNome().trim();
                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "El nombre es obligatorio");
                    return;
                }

                if (seleccionado == null) {
                    seleccionado = new Loitador();
                    seleccionado.setId_loitador(0);
                }

                seleccionado.setNome(nombre);
                seleccionado.setEstado(view.getEstadoSeleccionado());
                seleccionado.setCategoria_peso(view.getPesoSeleccionado());
                seleccionado.setEntrada(view.getEntrada());
                seleccionado.setFoto_url(view.getFotoUrl());

                try {
                    dao.luchadorGuardar(seleccionado);
                    JOptionPane.showMessageDialog(view, "Operación realizada con éxito");
                    cargarDatos();
                    limpiar();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(view, "Error al guardar: " + ex.getMessage());
                }
            }
        };
    }

    private ActionListener getDeleteListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (seleccionado == null) {
                    JOptionPane.showMessageDialog(view, "Selecciona un luchador");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(view, "¿Borrar a " + seleccionado.getNome() + "?");
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        dao.luchadorEliminar(seleccionado.getId_loitador());
                        cargarDatos();
                        limpiar();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(view, "Error al eliminar: " + ex.getMessage());
                    }
                }
            }
        };
    }

    private ActionListener getClearListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiar();
            }
        };
    }

    private void limpiar() {
        seleccionado = null;
        view.limpiarFormulario();
    }
}
