package controller.marca;

import model.Marca;
import view.marca.MarcaJFrame;
import java.sql.Connection;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import main.OperacionsBD;

public class MarcaJFrameController {
    private MarcaJFrame vista;
    private OperacionsBD dao;
    private DefaultListModel<Marca> modeloLista;
    private Marca seleccionada;

    public MarcaJFrameController(MarcaJFrame vista, Connection conn) {
        this.vista = vista;
        this.dao = new OperacionsBD();
        this.modeloLista = new DefaultListModel<>();
        
        this.vista.getListaMarcas().setModel((DefaultListModel)modeloLista);
        
        initEvents();
        cargarLista();
    }

    private void initEvents() {
        vista.getListaMarcas().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionada = vista.getListaMarcas().getSelectedValue();
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
            List<Marca> marcas = dao.marcaListar();
            for (Marca m : marcas) {
                modeloLista.addElement(m);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarDetalles() {
        if (seleccionada != null) {
            vista.getLblId().setText("ID: " + seleccionada.getId_marca());
            vista.getTxtNome().setText(seleccionada.getNome_marca());
            vista.getTxtLogoUrl().setText(seleccionada.getLogo_url());
            vista.actualizarLogo(seleccionada.getLogo_url());
        }
    }

    private void guardar() {
        try {
            if (vista.getTxtNome().getText().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El nombre es obligatorio");
                return;
            }

            Marca m = (seleccionada == null) ? new Marca() : seleccionada;
            m.setNome_marca(vista.getTxtNome().getText());
            m.setLogo_url(vista.getTxtLogoUrl().getText());

            dao.marcaGuardar(m);
            cargarLista();
            limpiar();
            JOptionPane.showMessageDialog(vista, "Marca guardada con éxito");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al guardar: " + ex.getMessage());
        }
    }

    private void eliminar() {
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(vista, "Selecciona una marca para eliminar");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista, "¿Eliminar " + seleccionada.getNome_marca() + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.marcaEliminar(seleccionada.getId_marca());
                cargarLista();
                limpiar();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error: No se puede eliminar una marca con luchadores asociados.");
            }
        }
    }

    private void limpiar() {
        seleccionada = null;
        vista.getLblId().setText("ID: Nuevo Registro");
        vista.getTxtNome().setText("");
        vista.getTxtLogoUrl().setText("");
        vista.getLblLogo().setIcon(null);
        vista.getListaMarcas().clearSelection();
    }
}
