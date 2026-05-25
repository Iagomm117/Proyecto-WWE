package controller.luchador;


import java.awt.Component;
import java.awt.Dimension;
import model.Loitador;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
import java.sql.*;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import main.OperacionsBD;
import service.ServicioQr;
import service.YoutubeService;
import view.luchador.LuchadorJFrame;

/**
 *
 * @author iagom
 */
public class LoitadorJFrameController {

    private LuchadorJFrame view;
    private OperacionsBD dao;
    private List<Loitador> listaLocal;
    private Loitador seleccionado = null;

    public LoitadorJFrameController(LuchadorJFrame view, java.sql.Connection conn) {
        this.view = view;
        this.dao = new OperacionsBD();
        this.view.addListaListener(this.getListSelectionListener());
        this.view.addBtnGuardarListener(this.getSaveListener());
        this.view.addBtnEliminarListener(this.getDeleteListener());
        this.view.addBtnLimpiarListener(this.getClearListener());
        this.view.addBtnQRListener(this.getQRListener());
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
            List<String> nombres = listaLocal.stream().map(Loitador::getNome).toList();
            view.actualizarLista(nombres);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error al cargar: " + e.getMessage());
        }
    }

    private ListSelectionListener getListSelectionListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int index = view.getIndexSeleccionado();
                    if (index != -1) {
                        seleccionado = listaLocal.get(index);
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
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String busqueda = view.getEntrada() + " WWE Song";

                YoutubeService yt = new YoutubeService();
                String urlEncontrada = yt.buscarVideo(busqueda);

                if (urlEncontrada != null) {
                    ImageIcon qrImagen = ServicioQr.generarQRDeTexto(urlEncontrada, 250, 250);

                    mostrarPopUpQR(urlEncontrada);
                } else {
                    JOptionPane.showMessageDialog(view, "Non se puido atopar o video.");
                }
            }
        };
        return al;
    }

    private void mostrarPopUpQR(String url) {
        ImageIcon qrIcon = ServicioQr.generarQRDeTexto(url, 250, 250);

        if (qrIcon == null) {
            JOptionPane.showMessageDialog(null, "Error ao xerar o QR.");
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblQR = new JLabel(qrIcon);
        lblQR.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txtUrl = new JTextField(url);
        txtUrl.setEditable(false);
        txtUrl.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtUrl.setMaximumSize(new Dimension(300, 30));

        panel.add(lblQR);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(new JLabel("URL do vídeo:"));
        panel.add(txtUrl);

        JOptionPane.showMessageDialog(
                null,
                panel,
                "Código QR Xerado",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private ActionListener getSaveListener() {
        ActionListener al = new ActionListener() {
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
        return al;
    }

    private ActionListener getDeleteListener() {
        ActionListener al = new ActionListener() {
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
        return al;
    }

    private ActionListener getClearListener() {
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiar();
            }
        };
        return al;
    }

    private void limpiar() {
        seleccionado = null;
        view.limpiarFormulario();
    }
}
