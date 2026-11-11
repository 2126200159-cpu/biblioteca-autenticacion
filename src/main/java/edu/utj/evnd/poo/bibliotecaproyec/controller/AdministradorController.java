package edu.utj.evnd.poo.bibliotecaproyec.controller;

import edu.utj.evnd.poo.bibliotecaproyec.enums.EstadoAdministrador;
import edu.utj.evnd.poo.bibliotecaproyec.model.Administrador;
import edu.utj.evnd.poo.bibliotecaproyec.repository.AdministradorRepository;
import edu.utj.evnd.poo.bibliotecaproyec.repository.PersonaRepository;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la vista de administradores. Aporta los campos propios
 * (usuario, contraseña, estado y rol) y sus validaciones en tiempo real.
 *
 * @author Joshua Odin Montoya Flores
 */
public class AdministradorController extends PersonaCrudController<Administrador> {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private ComboBox<EstadoAdministrador> cbEstado;
    @FXML private TextField txtRol;

    @FXML private Label errUsuario;
    @FXML private Label errContrasena;
    @FXML private Label errEstado;
    @FXML private Label errRol;

    @FXML private TableColumn<Administrador, String> colUsuario;
    @FXML private TableColumn<Administrador, EstadoAdministrador> colEstado;
    @FXML private TableColumn<Administrador, String> colRol;

    private final AdministradorRepository repository = new AdministradorRepository();

    @Override
    protected PersonaRepository<Administrador> repositorio() {
        return repository;
    }

    @Override
    protected String nombreEntidad() {
        return "Administrador";
    }

    @Override
    protected int edadMinima() {
        return 18;
    }

    @Override
    protected Administrador crearEntidad(int id) {
        Administrador admin = new Administrador(id, txtCurp.getText(), txtNombre.getText(),
                txtApellido.getText(), txtUsuario.getText(), txtContrasena.getText());
        admin.setEstado(cbEstado.getValue());
        admin.setRol(txtRol.getText());
        return admin;
    }

    @Override
    protected void registrarCamposEspecificos() {
        filtrar(txtUsuario, "[\\p{L}\\p{N}_.\\-]{0,20}", false);
        filtrar(txtContrasena, "\\S{0,30}", false);
        filtrar(txtRol, "[\\p{L} ]{0,30}", false);

        registrarCampo(txtUsuario, errUsuario, () -> {
            String t = txtUsuario.getText().trim();
            if (t.isEmpty()) {
                return OBLIGATORIO;
            }
            if (t.length() < 3) {
                return "El usuario debe tener al menos 3 caracteres.";
            }
            return null;
        });

        registrarCampo(txtContrasena, errContrasena, () -> {
            String t = txtContrasena.getText();
            if (t.isEmpty()) {
                return OBLIGATORIO;
            }
            if (t.length() < 4) {
                return "La contraseña debe tener al menos 4 caracteres.";
            }
            return null;
        });

        registrarCampo(cbEstado, errEstado, () ->
                cbEstado.getValue() == null ? "Selecciona un estado." : null);

        registrarCampo(txtRol, errRol, () -> {
            String t = txtRol.getText().trim();
            if (t.isEmpty()) {
                return OBLIGATORIO;
            }
            if (t.length() < 3) {
                return "El rol debe tener al menos 3 letras.";
            }
            return null;
        });
    }

    @Override
    protected void configurarColumnasEspecificas() {
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
    }

    @Override
    protected void prepararCamposEspecificos() {
        // Solo se pueden elegir los valores definidos en el enum del modelo.
        cbEstado.setItems(FXCollections.observableArrayList(EstadoAdministrador.values()));
    }

    @Override
    protected void cargarCamposEspecificos(Administrador a) {
        txtUsuario.setText(a.getUsuario());
        txtContrasena.setText(a.getContrasena());
        cbEstado.setValue(a.getEstado());
        txtRol.setText(a.getRol());
    }

    @Override
    protected void limpiarCamposEspecificos() {
        txtUsuario.clear();
        txtContrasena.clear();
        cbEstado.setValue(EstadoAdministrador.ACTIVO);
        txtRol.clear();
    }

    @Override
    protected void habilitarCamposEspecificos(boolean habilitar) {
        txtUsuario.setDisable(!habilitar);
        txtContrasena.setDisable(!habilitar);
        cbEstado.setDisable(!habilitar);
        txtRol.setDisable(!habilitar);
    }
}
