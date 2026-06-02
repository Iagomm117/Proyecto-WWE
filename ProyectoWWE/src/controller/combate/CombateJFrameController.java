package controller.combate;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import main.OperacionsBD;
import model.Combate;
import model.Loitador;
import model.PPV;
import model.Titulo;
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
            if (isSelected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            return label;
        });

        cargarCombosIniciales();
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
        vista.getBtnAgregarLuchador().addActionListener(e -> agregarLoitadorATabla());
        vista.getBtnQuitarLuchador().addActionListener(e -> quitarLoitadorDeTabla());
    }

    private void cargarCombosIniciales() {
        try {
            vista.getComboGanador().removeAllItems();
            vista.getComboTitulo().removeAllItems();
            vista.getComboGanador().addItem(null); 
            vista.getComboTitulo().addItem(null);

            List<Loitador> loitadores = dao.luchadorListar(); 
            for (Loitador l : loitadores) {
                vista.getComboGanador().addItem(l);
            }

            List<Titulo> titulos = dao.tituloListar(); 
            for (Titulo t : titulos) {
                vista.getComboTitulo().addItem(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            if (seleccionada.getIdLoitadorGanador() == null) {
                vista.getComboGanador().setSelectedItem(null);
            } else {
                seleccionarGanadorPorId(seleccionada.getIdLoitadorGanador());
            }

            if (seleccionada.getIdTituloEnXogo() == null) {
                vista.getComboTitulo().setSelectedItem(null);
            } else {
                seleccionarTituloPorId(seleccionada.getIdTituloEnXogo());
            }

            cargarTablaParticipantes(seleccionada.getIdCombate());
        }
    }

    private void cargarTablaParticipantes(int idCombate) {
        DefaultTableModel modeloTabla = (DefaultTableModel) vista.getTablaParticipantes().getModel();
        modeloTabla.setRowCount(0); 
        try {
            List<Loitador> participantes = dao.listarParticipantesPorCombate(idCombate);
            for (Loitador l : participantes) {
                modeloTabla.addRow(new Object[]{l.getId_loitador(), l.getNome()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void agregarLoitadorATabla() {
        try {
            List<Loitador> todos = dao.luchadorListar();
            JComboBox<Loitador> comboSeleccion = new JComboBox<>();
            for (Loitador l : todos) {
                comboSeleccion.addItem(l);
            }

            int option = JOptionPane.showConfirmDialog(vista, comboSeleccion, "Selecciona un Loitador", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                Loitador seleccionado = (Loitador) comboSeleccion.getSelectedItem();
                if (seleccionado != null) {
                    DefaultTableModel modeloTabla = (DefaultTableModel) vista.getTablaParticipantes().getModel();
                    for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                        if ((int) modeloTabla.getValueAt(i, 0) == seleccionado.getId_loitador()) {
                            return;
                        }
                    }
                    modeloTabla.addRow(new Object[]{seleccionado.getId_loitador(), seleccionado.getNome()});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void quitarLoitadorDeTabla() {
        int fila = vista.getTablaParticipantes().getSelectedRow();
        if (fila != -1) {
            ((DefaultTableModel) vista.getTablaParticipantes().getModel()).removeRow(fila);
        }
    }

    private void guardar() {
        try {
            String tipo = vista.getTxtTipoCombate().getText().trim();
            if (tipo.isEmpty()) return;

            DefaultTableModel modeloTabla = (DefaultTableModel) vista.getTablaParticipantes().getModel();
            if (modeloTabla.getRowCount() < 2) return;

            Combate c = (seleccionada == null) ? new Combate() : seleccionada;
            c.setIdPpv(ppvContexto.getId_ppv()); 
            c.setTipoCombate(tipo);
            c.setOrdeNoPpv((Integer) vista.getSpinOrden().getValue());

            Loitador g = (Loitador) vista.getComboGanador().getSelectedItem();
            c.setIdLoitadorGanador(g == null ? null : g.getId_loitador());

            Titulo t = (Titulo) vista.getComboTitulo().getSelectedItem();
            c.setIdTituloEnXogo(t == null ? null : t.getId_titulo());

            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                ids.add((Integer) modeloTabla.getValueAt(i, 0));
            }

            if (dao.guardarCombateYParticipantes(c, ids)) {
                cargarLista();
                limpiar();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void eliminar() {
        if (seleccionada != null && dao.combateEliminar(seleccionada.getIdCombate())) {
            cargarLista();
            limpiar();
        }
    }

    private void limpiar() {
        seleccionada = null;
        vista.getLblId().setText("ID: Nuevo Registro");
        vista.getTxtTipoCombate().setText("");
        vista.getSpinOrden().setValue(1);
        vista.getComboGanador().setSelectedItem(null);
        vista.getComboTitulo().setSelectedItem(null);
        ((DefaultTableModel) vista.getTablaParticipantes().getModel()).setRowCount(0);
        vista.getListaCombates().clearSelection();
    }

    private void seleccionarGanadorPorId(int id) {
        for (int i = 0; i < vista.getComboGanador().getItemCount(); i++) {
            Loitador l = vista.getComboGanador().getItemAt(i);
            if (l != null && l.getId_loitador() == id) {
                vista.getComboGanador().setSelectedIndex(i);
                break;
            }
        }
    }

    private void seleccionarTituloPorId(int id) {
        for (int i = 0; i < vista.getComboTitulo().getItemCount(); i++) {
            Titulo t = vista.getComboTitulo().getItemAt(i);
            if (t != null && t.getId_titulo() == id) {
                vista.getComboTitulo().setSelectedIndex(i);
                break;
            }
        }
    }
}