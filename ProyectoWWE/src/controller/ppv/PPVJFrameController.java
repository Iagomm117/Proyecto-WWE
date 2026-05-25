package controller.ppv;

import controller.combate.CombateJFrameController;
import model.PPV;
import model.GrupoPPV;
import view.ppv.PpvJFrame;
import view.combate.CombateJFrame;

import java.sql.Connection;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import main.OperacionsBD;

public class PPVJFrameController {

    private PpvJFrame vista;
    private Connection conn;
    private OperacionsBD dao;
    private DefaultListModel<PPV> modeloLista;
    private PPV seleccionada;

    public PPVJFrameController(PpvJFrame vista, Connection conn) {
        this.vista = vista;
        this.conn = conn;
        this.dao = new OperacionsBD();
        this.modeloLista = new DefaultListModel<>();

        this.vista.getListaPPVs().setModel((DefaultListModel) modeloLista);

        initEvents();
        cargarGrupos();
        cargarLista();
    }

    private void initEvents() {
        vista.getListaPPVs().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionada = vista.getListaPPVs().getSelectedValue();
                mostrarDetalles();
            }
        });

        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getBtnNuevo().addActionListener(e -> limpiar());
        vista.getBtnEliminar().addActionListener(e -> eliminar());

        vista.getBtnNuevoGrupo().addActionListener(e -> crearNuevoGrupo());
        vista.getBtnVerCombates().addActionListener(e -> abrirVentanaCombates());
    }

    private void cargarLista() {
        try {
            modeloLista.clear();
            List<PPV> ppvs = dao.PPVListar();
            for (PPV p : ppvs) {
                modeloLista.addElement(p);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarGrupos() {
        try {
            DefaultComboBoxModel<Object> modeloCombo = new DefaultComboBoxModel<>();
            List<GrupoPPV> grupos = dao.grupoPPVListar();
            for (GrupoPPV g : grupos) {
                modeloCombo.addElement(g);
            }
            vista.getCbGrupoPPV().setModel(modeloCombo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarDetalles() {
        if (seleccionada != null) {
            vista.getLblId().setText("ID: " + seleccionada.getId_ppv());
            vista.getTxtNome().setText(seleccionada.getNombre());
            vista.getTxtPosterUrl().setText(seleccionada.getUrlPoster());

            if (seleccionada.getGrupoPPV() != null) {
                boolean encontrado = false;
                for (int i = 0; i < vista.getCbGrupoPPV().getItemCount(); i++) {
                    GrupoPPV g = (GrupoPPV) vista.getCbGrupoPPV().getItemAt(i);

                    if (g != null && g.getIdGrupoPpv() == seleccionada.getGrupoPPV().getIdGrupoPpv()) {
                        vista.getCbGrupoPPV().setSelectedItem(g);
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    vista.getCbGrupoPPV().setSelectedIndex(-1);
                }
            } else {
                vista.getCbGrupoPPV().setSelectedIndex(-1);
            }

            vista.actualizarPoster(seleccionada.getUrlPoster());
        }
    }

    private void guardar() {
        try {
            if (vista.getTxtNome().getText().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El nombre del evento es obligatorio");
                return;
            }

            PPV p = (seleccionada == null) ? new PPV() : seleccionada;
            p.setNombre(vista.getTxtNome().getText().trim());
            p.setUrlPoster(vista.getTxtPosterUrl().getText().trim());

            GrupoPPV grupoSeleccionado = (GrupoPPV) vista.getCbGrupoPPV().getSelectedItem();
            p.setGrupoPPV(grupoSeleccionado);

            dao.PPVGuardar(p);
            cargarLista();
            limpiar();
            JOptionPane.showMessageDialog(vista, "PPV guardado con éxito");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al guardar: " + ex.getMessage());
        }
    }

    private void crearNuevoGrupo() {
        String nombreGrupo = JOptionPane.showInputDialog(vista, "Nombre del nuevo Grupo de PPV (ej. Big Four, Minor PPV):", "Crear Grupo", JOptionPane.QUESTION_MESSAGE);

        if (nombreGrupo != null && !nombreGrupo.trim().isEmpty()) {
            try {
                GrupoPPV nuevoGrupo = new GrupoPPV();
                nuevoGrupo.setNomeGrupo(nombreGrupo.trim());

                dao.grupoPPVInsertar(nuevoGrupo);
                JOptionPane.showMessageDialog(vista, "Grupo '" + nombreGrupo + "' creado con éxito");

                cargarGrupos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al crear el grupo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirVentanaCombates() {
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un PPV de la lista para ver sus combates.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            CombateJFrame vistaCombate = new CombateJFrame();
            CombateJFrameController controllerCombate = new CombateJFrameController(vistaCombate, conn, seleccionada);

            vistaCombate.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al abrir la ventana de combates: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(vista, "Selecciona un PPV para eliminar");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista, "¿Eliminar " + seleccionada.getNombre() + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.PPVEliminar(seleccionada.getId_ppv());
                cargarLista();
                limpiar();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error al eliminar: " + ex.getMessage());
            }
        }
    }

    private void limpiar() {
        seleccionada = null;
        vista.getLblId().setText("ID: Nuevo Registro");
        vista.getTxtNome().setText("");
        vista.getTxtPosterUrl().setText("");
        if (vista.getCbGrupoPPV().getItemCount() > 0) {
            vista.getCbGrupoPPV().setSelectedIndex(0);
        }
        vista.getLblPoster().setIcon(null);
        vista.getListaPPVs().clearSelection();
    }
}
