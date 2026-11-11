package edu.utj.evnd.poo.bibliotecaproyec.controller;

import edu.utj.evnd.poo.bibliotecaproyec.model.Lector;
import edu.utj.evnd.poo.bibliotecaproyec.repository.LectorRepository;
import edu.utj.evnd.poo.bibliotecaproyec.repository.PersonaRepository;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la vista de lectores. Aporta los campos propios
 * (credencial y multa pendiente) y sus validaciones en tiempo real.
 *
 * @author Joshua Odin Montoya Flores
 */
public class LectorController extends PersonaCrudController<Lector> {

    @FXML private TextField txtCredencial;
    @FXML private TextField txtMulta;

    @FXML private Label errCredencial;
    @FXML private Label errMulta;

    @FXML private TableColumn<Lector, String> colCredencial;
    @FXML private TableColumn<Lector, Double> colMulta;

    private final LectorRepository repository = new LectorRepository();

    @Override
    protected PersonaRepository<Lector> repositorio() {
        return repository;
    }

    @Override
    protected String nombreEntidad() {
        return "Lector";
    }

    @Override
    protected Lector crearEntidad(int id) {
        Lector lector = new Lector(id, txtCurp.getText(), txtNombre.getText(),
                txtApellido.getText(), txtCredencial.getText());
        lector.setMultaPendiente(Double.parseDouble(txtMulta.getText().trim()));
        return lector;
    }

    @Override
    protected void registrarCamposEspecificos() {
        filtrar(txtCredencial, "[A-Za-z0-9\\-]{0,15}", true);
        filtrar(txtMulta, "\\d{0,6}(\\.\\d{0,2})?", false); // decimal con hasta 2 decimales

        registrarCampo(txtCredencial, errCredencial, () -> {
            String t = txtCredencial.getText().trim();
            if (t.isEmpty()) {
                return OBLIGATORIO;
            }
            if (t.length() < 4) {
                return "La credencial debe tener al menos 4 caracteres.";
            }
            return null;
        });

        registrarCampo(txtMulta, errMulta, () -> {
            String t = txtMulta.getText().trim();
            if (t.isEmpty()) {
                return OBLIGATORIO;
            }
            if (!t.matches("\\d+(\\.\\d{1,2})?")) {
                return "Formato decimal correcto, por ejemplo 15.50";
            }
            return null;
        });
    }

    @Override
    protected void configurarColumnasEspecificas() {
        colCredencial.setCellValueFactory(new PropertyValueFactory<>("numeroCredencial"));
        colMulta.setCellValueFactory(new PropertyValueFactory<>("multaPendiente"));
    }

    @Override
    protected void prepararCamposEspecificos() {
        // Este módulo no tiene controles que preparar.
    }

    @Override
    protected void cargarCamposEspecificos(Lector l) {
        txtCredencial.setText(l.getNumeroCredencial());
        txtMulta.setText(String.valueOf(l.getMultaPendiente()));
    }

    @Override
    protected void limpiarCamposEspecificos() {
        txtCredencial.clear();
        txtMulta.setText("0");
    }

    @Override
    protected void habilitarCamposEspecificos(boolean habilitar) {
        txtCredencial.setDisable(!habilitar);
        txtMulta.setDisable(!habilitar);
    }
}
