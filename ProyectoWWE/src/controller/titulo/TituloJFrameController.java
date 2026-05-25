package controller.titulo;

import model.Titulo;
import view.titulo.TituloJFrame;
import java.sql.Connection;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import main.OperacionsBD;

public class TituloJFrameController {

    private TituloJFrame vista;
    private OperacionsBD dao;
    private DefaultListModel<Titulo> modeloLista;
    private Titulo seleccionada;

    public TituloJFrameController(TituloJFrame vista, Connection conn) {
        this.vista = vista;
        this.dao = new OperacionsBD();
        this.modeloLista = new DefaultListModel<>();

        this.vista.getListaTitulos().setModel(modeloLista);

        initEvents();
        cargarLista();
    }

    private void initEvents() {
        vista.getListaTitulos().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionada = (Titulo) vista.getListaTitulos().getSelectedValue();
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
            List<Titulo> titulos = dao.tituloListar();
            for (Titulo t : titulos) {
                modeloLista.addElement(t);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarDetalles() {
        if (seleccionada != null) {
            String fechaFormateada = "";
            if (seleccionada.getFechaVigencia() != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                fechaFormateada = sdf.format(seleccionada.getFechaVigencia());
            }
            vista.setFormulario(
                seleccionada.getNombre(),
                seleccionada.getEstado(),
                fechaFormateada,
                seleccionada.getUrlFoto(),
                String.valueOf(seleccionada.getId_titulo()),
                seleccionada.isMaximo()
            );
        }
    }

    private void guardar() {
        try {
            if (vista.getTxtNome().getText().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El nombre del título es obligatorio");
                return;
            }

            Titulo t = (seleccionada == null) ? new Titulo() : seleccionada;
            t.setNombre(vista.getTxtNome().getText().trim());
            t.setEstado(vista.getComboEstado().getSelectedItem().toString());
            try {
                String fechaTexto = vista.getTxtData().getText().trim();
                java.text.SimpleDateFormat formateador = new java.text.SimpleDateFormat("dd/MM/yyyy");
                formateador.setLenient(false);
                java.util.Date utilDate = formateador.parse(fechaTexto);
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                t.setFechaVigencia(sqlDate);
            } catch (java.text.ParseException e) {
                JOptionPane.showMessageDialog(vista, "El formato de fecha no es válido. Usa dd/MM/yyyy (Ej: 25/05/2026)", "Error de Fecha", JOptionPane.ERROR_MESSAGE);
                return; 
            }
            
            t.setUrlFoto(vista.getTxtFotoUrl().getText().trim());
            t.setMaximo(vista.getChkMaximo().isSelected());

            dao.tituloGuardar(t);
            cargarLista();
            limpiar();
            JOptionPane.showMessageDialog(vista, "Título guardado con éxito");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al guardar: " + ex.getMessage());
        }
    }

    private void eliminar() {
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(vista, "Selecciona un título para eliminar");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista, "¿Eliminar " + seleccionada.getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.tituloEliminar(seleccionada.getId_titulo());
                cargarLista();
                limpiar();
                JOptionPane.showMessageDialog(vista, "Título eliminado correctamente");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al eliminar: " + ex.getMessage());
            }
        }
    }

    private void limpiar() {
        seleccionada = null;
        vista.limpiarFormulario();
    }
}