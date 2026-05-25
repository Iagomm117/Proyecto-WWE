package controller.equipo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.Equipo;
import model.Marca; 
import main.OperacionsBD;
import view.equipo.EquipoJFrame;

public class EquipoJFrameController implements ActionListener, ListSelectionListener {

    private final EquipoJFrame vista;
    private final OperacionsBD dao;
    private List<Equipo> listaLocal; 

    public EquipoJFrameController(EquipoJFrame vista) {
        this.vista = vista;
        this.dao = new OperacionsBD();
        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnNuevo().addActionListener(this);
        this.vista.getBtnEliminar().addActionListener(this);
        this.vista.getListaEquipos().addListSelectionListener(this);
        
        cargarComboMarcas();
        refrescarLista();
    }

    private void refrescarLista() {
        listaLocal = dao.equipoListar();
        DefaultListModel<String> modeloLista = new DefaultListModel<>();
        for (Equipo eq : listaLocal) {
            modeloLista.addElement(eq.getNome_equipo());
        }
        ((javax.swing.JList) vista.getListaEquipos()).setModel(modeloLista);
    }

    private void cargarComboMarcas() {
        DefaultComboBoxModel<Object> modeloCombo = new DefaultComboBoxModel<>();
        Marca m1 = new Marca(); m1.setId_marca(1); m1.setNome_marca("Raw");
        Marca m2 = new Marca(); m2.setId_marca(2); m2.setNome_marca("SmackDown");
        Marca m3 = new Marca(); m3.setId_marca(3); m3.setNome_marca("NXT");
        
        modeloCombo.addElement(m1);
        modeloCombo.addElement(m2);
        modeloCombo.addElement(m3);
        
        vista.getCbMarca().setModel(modeloCombo);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnNuevo()) {
            vista.limpiarFormulario();
        } 

        else if (e.getSource() == vista.getBtnGuardar()) {
            String nombre = vista.getTxtNome().getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El nombre del equipo es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Equipo eq = new Equipo();
            eq.setNome_equipo(nombre);
            eq.setDescripcion(vista.getTxtDescripcion().getText().trim());
            eq.setFoto_url(vista.getTxtFotoUrl().getText().trim());

            Marca marcaSeleccionada = (Marca) vista.getCbMarca().getSelectedItem();
            eq.setMarca(marcaSeleccionada);

            String idTexto = vista.getLblId().getText();

            if (idTexto.equals("ID: Nuevo Registro")) {
                if (dao.equipoInsertar(eq)) {
                    JOptionPane.showMessageDialog(vista, "¡Equipo registrado con éxito!");
                    vista.limpiarFormulario();
                    refrescarLista();
                } else {
                    JOptionPane.showMessageDialog(vista, "Error al insertar el equipo en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                int id = Integer.parseInt(idTexto.replace("ID: ", ""));
                eq.setId_equipo(id);

                if (dao.equipoActualizar(eq)) {
                    JOptionPane.showMessageDialog(vista, "¡Equipo actualizado correctamente!");
                    vista.limpiarFormulario();
                    refrescarLista();
                } else {
                    JOptionPane.showMessageDialog(vista, "Error al actualizar el equipo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } 
        
        else if (e.getSource() == vista.getBtnEliminar()) {
            String idTexto = vista.getLblId().getText();
            if (idTexto.equals("ID: Nuevo Registro")) {
                JOptionPane.showMessageDialog(vista, "Selecciona un equipo de la lista para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = Integer.parseInt(idTexto.replace("ID: ", ""));
            int confirmar = JOptionPane.showConfirmDialog(vista, "¿Seguro que deseas eliminar este equipo?", "Confirmar", JOptionPane.YES_NO_OPTION);
            
            if (confirmar == JOptionPane.YES_OPTION) {
                if (dao.equipoEliminar(id)) {
                    JOptionPane.showMessageDialog(vista, "Equipo eliminado correctamente.");
                    vista.limpiarFormulario();
                    refrescarLista();
                } else {
                    JOptionPane.showMessageDialog(vista, "Error al eliminar el equipo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int filaSeleccionada = vista.getListaEquipos().getSelectedIndex();
            
            if (filaSeleccionada != -1 && filaSeleccionada < listaLocal.size()) {
                Equipo seleccionado = listaLocal.get(filaSeleccionada);
                
                Marca marcaMatch = null;
                if (seleccionado.getMarca() != null) {
                    for (int i = 0; i < vista.getCbMarca().getItemCount(); i++) {
                        Marca m = (Marca) vista.getCbMarca().getItemAt(i);
                        if (m.getId_marca() == seleccionado.getMarca().getId_marca()) {
                            marcaMatch = m;
                            break;
                        }
                    }
                }

                vista.setFormulario(
                    seleccionado.getNome_equipo(),
                    marcaMatch,
                    seleccionado.getDescripcion(),
                    seleccionado.getFoto_url(),
                    String.valueOf(seleccionado.getId_equipo())
                );
            }
        }
    }
}