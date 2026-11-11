package edu.utj.evnd.poo.bibliotecaproyec.controller;

import edu.utj.evnd.poo.bibliotecaproyec.enums.Modulo;
import edu.utj.evnd.poo.bibliotecaproyec.dao.Dao;
import edu.utj.evnd.poo.bibliotecaproyec.dao.EditorialDao;
import edu.utj.evnd.poo.bibliotecaproyec.model.Editorial;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la vista de editoriales.
 *
 * @author Joshua Odin Montoya Flores
 */
public class EditorialController extends CrudController<Editorial> {

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtContacto;
    @FXML private TextField txtCargo;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtRegion;
    @FXML private TextField txtCodigoPostal;
    @FXML private TextField txtPais;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtSitioWeb;

    @FXML private Label errNombre;
    @FXML private Label errTelefono;

    @FXML private TableColumn<Editorial, Integer> colId;
    @FXML private TableColumn<Editorial, String> colNombre;
    @FXML private TableColumn<Editorial, String> colContacto;
    @FXML private TableColumn<Editorial, String> colCiudad;
    @FXML private TableColumn<Editorial, String> colPais;
    @FXML private TableColumn<Editorial, String> colTelefono;
    @FXML private TableColumn<Editorial, String> colSitioWeb;

    private final EditorialDao dao = new EditorialDao();

    @Override
    protected Dao<Editorial> dao() {
        return dao;
    }

    @Override
    protected String nombreEntidad() {
        return "Editorial";
    }

    @Override
    protected Modulo modulo() {
        return Modulo.EDITORIALES;
    }

    @Override
    protected Editorial crearEntidad(Editorial existente) {
        Editorial e = new Editorial(txtNombre.getText());
        if (existente != null) {
            e.setId(existente.getId());
        }
        e.setNombreContacto(txtContacto.getText());
        e.setCargoContacto(txtCargo.getText());
        e.setDireccion(txtDireccion.getText());
        e.setCiudad(txtCiudad.getText());
        e.setRegion(txtRegion.getText());
        e.setCodigoPostal(txtCodigoPostal.getText());
        e.setPais(txtPais.getText());
        e.setTelefono(txtTelefono.getText());
        e.setSitioWeb(txtSitioWeb.getText());
        return e;
    }

    @Override
    protected void registrarCampos() {
        filtrar(txtNombre, ".{0,80}", false);
        filtrar(txtContacto, "[\\p{L} ]{0,60}", false);
        filtrar(txtCargo, "[\\p{L} ]{0,40}", false);
        filtrar(txtDireccion, ".{0,100}", false);
        filtrar(txtCiudad, "[\\p{L} ]{0,50}", false);
        filtrar(txtRegion, "[\\p{L} ]{0,50}", false);
        filtrar(txtCodigoPostal, "\\d{0,10}", false);
        filtrar(txtPais, "[\\p{L} ]{0,50}", false);
        filtrar(txtTelefono, "\\d{0,10}", false);
        filtrar(txtSitioWeb, "\\S{0,120}", false);

        registrarCampo(txtNombre, errNombre, () ->
                txtNombre.getText().trim().isEmpty() ? OBLIGATORIO : null);

        registrarCampo(txtTelefono, errTelefono, () -> {
            String t = txtTelefono.getText().trim();
            if (!t.isEmpty() && t.length() != 10) {
                return "El teléfono debe tener 10 dígitos.";
            }
            return null; // el teléfono es opcional
        });
    }

    @Override
    protected void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colContacto.setCellValueFactory(new PropertyValueFactory<>("nombreContacto"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        colPais.setCellValueFactory(new PropertyValueFactory<>("pais"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colSitioWeb.setCellValueFactory(new PropertyValueFactory<>("sitioWeb"));
    }

    @Override
    protected void prepararCampos() {
        txtId.setDisable(true); // ID automático (AUTO_INCREMENT)
    }

    @Override
    protected void cargarCampos(Editorial e) {
        txtId.setText(String.valueOf(e.getId()));
        txtNombre.setText(e.getNombre());
        txtContacto.setText(e.getNombreContacto());
        txtCargo.setText(e.getCargoContacto());
        txtDireccion.setText(e.getDireccion());
        txtCiudad.setText(e.getCiudad());
        txtRegion.setText(e.getRegion());
        txtCodigoPostal.setText(e.getCodigoPostal());
        txtPais.setText(e.getPais());
        txtTelefono.setText(e.getTelefono());
        txtSitioWeb.setText(e.getSitioWeb());
    }

    @Override
    protected void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtContacto.clear();
        txtCargo.clear();
        txtDireccion.clear();
        txtCiudad.clear();
        txtRegion.clear();
        txtCodigoPostal.clear();
        txtPais.clear();
        txtTelefono.clear();
        txtSitioWeb.clear();
    }

    @Override
    protected void habilitarCampos(boolean editable, boolean esNuevo) {
        TextField[] campos = {txtNombre, txtContacto, txtCargo, txtDireccion, txtCiudad, txtRegion,
                txtCodigoPostal, txtPais, txtTelefono, txtSitioWeb};
        for (TextField c : campos) {
            c.setDisable(!editable);
        }
    }
}
