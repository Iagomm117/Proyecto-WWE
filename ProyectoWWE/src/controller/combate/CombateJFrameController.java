/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.combate;

/**
 *
 * @author iagom
 */
import java.awt.Color;
import java.sql.Connection;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import main.OperacionsBD;
import model.Combate;
import model.PPV;
import view.combate.CombateJFrame;

public class CombateJFrameController {

    private final CombateJFrame vista;
    private final OperacionsBD dao;
    private final PPV ppvContexto; 
    private final DefaultListModel<Combate> modeloLista;
    private Combate seleccionada;

    public CombateJFrameController(CombateJFrame vista, Connection conn, PPV ppvContexto) {
        this.vista = vista;
        this.ppvContexto = ppvContexto;
        this.dao = new OperacionsBD();
        this.modeloLista = new DefaultListModel<>();
        this.vista.getLblPpvContexto().setText("Combates del PPV: " + ppvContexto.getNombre());
        this.vista.getListaCombates().setModel(modeloLista);

        this.vista.getListaCombates().setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            javax.swing.JLabel label = new javax.swing.JLabel("#" + value.getOrdeNoPpv() + " - " + value.getTipoCombate());
            label.setOpaque(true);
            label.setForeground(Color.WHITE);
            if (isSelected) {
                label.setBackground(list.getSelectionBackground());
            } else {
                label.setBackground(list.getBackground());
            }
            return label;
        });

        initEvents();
        cargarLista();
    }

    private void initEvents() {
        vista.getListaCombates().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionada = vista.getListaCombates().getSelectedValue();
                mostrarDetalles();
            }
        });

        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getBtnNuevo().addActionListener(e -> limpiar());
        vista.getBtnEliminar().addActionListener(e -> eliminar());
    }

    private void cargarLista() {
        try {
            modeloLista.clear();
            List<Combate> combates = dao.listarPorPpv(ppvContexto.getId_ppv());
            for (Combate c : combates) {
                modeloLista.addElement(c);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarDetalles() {
        if (seleccionada != null) {
            vista.getLblId().setText("ID: " + seleccionada.getIdCombate());
            vista.getTxtTipoCombate().setText(seleccionada.getTipoCombate());
            vista.getSpinOrden().setValue(seleccionada.getOrdeNoPpv());

            vista.getTxtIdGanador().setText(seleccionada.getIdLoitadorGanador() == null ? "" : String.valueOf(seleccionada.getIdLoitadorGanador()));
            vista.getTxtIdTitulo().setText(seleccionada.getIdTituloEnXogo() == null ? "" : String.valueOf(seleccionada.getIdTituloEnXogo()));
        }
    }

    private void guardar() {
        try {
            String tipo = vista.getTxtTipoCombate().getText().trim();
            if (tipo.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El tipo de combate es obligatorio.");
                return;
            }

            Combate c = (seleccionada == null) ? new Combate() : seleccionada;
            c.setIdPpv(ppvContexto.getId_ppv()); 
            c.setTipoCombate(tipo);
            c.setOrdeNoPpv((Integer) vista.getSpinOrden().getValue());

            String txtGanador = vista.getTxtIdGanador().getText().trim();
            if (txtGanador.isEmpty()) {
                c.setIdLoitadorGanador(null);
            } else {
                c.setIdLoitadorGanador(Integer.parseInt(txtGanador));
            }

            String txtTitulo = vista.getTxtIdTitulo().getText().trim();
            if (txtTitulo.isEmpty()) {
                c.setIdTituloEnXogo(null);
            } else {
                c.setIdTituloEnXogo(Integer.parseInt(txtTitulo));
            }

            if (dao.guardar(c)) {
                JOptionPane.showMessageDialog(vista, "Combate guardado correctamente");
                cargarLista();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(vista, "No se pudo guardar en la base de datos.");
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(vista, "Los campos ID de Ganador e ID de Título deben ser numéricos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void eliminar() {
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(vista, "Selecciona un combate de la lista para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista, "¿Deseas eliminar este combate de la cartelera?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (dao.combateEliminar(seleccionada.getIdCombate())) {
                    cargarLista();
                    limpiar();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al eliminar: " + ex.getMessage());
            }
        }
    }

    private void limpiar() {
        seleccionada = null;
        vista.getLblId().setText("ID: Nuevo Registro");
        vista.getTxtTipoCombate().setText("");
        vista.getSpinOrden().setValue(1);
        vista.getTxtIdGanador().setText("");
        vista.getTxtIdTitulo().setText("");
        vista.getListaCombates().clearSelection();
    }
}
