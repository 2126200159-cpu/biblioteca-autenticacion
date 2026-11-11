package edu.utj.evnd.poo.bibliotecaproyec.controller;

import edu.utj.evnd.poo.bibliotecaproyec.enums.Modulo;
import edu.utj.evnd.poo.bibliotecaproyec.dao.CategoriaDao;
import edu.utj.evnd.poo.bibliotecaproyec.dao.Dao;
import edu.utj.evnd.poo.bibliotecaproyec.model.Categoria;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la vista de categorías de libros.
 *
 * @author Joshua Odin Montoya Flores
 */
public class CategoriaController extends CrudController<Categoria> {

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;

    @FXML private Label errNombre;

    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, String> colDescripcion;

    private final CategoriaDao dao = new CategoriaDao();

    @Override
    protected Dao<Categoria> dao() {
        return dao;
    }

    @Override
    protected String nombreEntidad() {
        return "Categoría";
    }

    @Override
    protected Modulo modulo() {
        return Modulo.CATEGORIAS;
    }

    @Override
    protected String prefijoImagen() {
        return "categoria";
    }

    @Override
    protected Categoria crearEntidad(Categoria existente) {
        Categoria c = new Categoria(txtNombre.getText());
        if (existente != null) {
            c.setId(existente.getId());
        }
        c.setDescripcion(txtDescripcion.getText());
        return c;
    }

    @Override
    protected void registrarCampos() {
        filtrar(txtNombre, "[\\p{L}\\p{N} ]{0,50}", false);
        filtrar(txtDescripcion, ".{0,255}", false);

        registrarCampo(txtNombre, errNombre, () ->
                txtNombre.getText().trim().isEmpty() ? OBLIGATORIO : null);
    }

    @Override
    protected void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }

    @Override
    protected void prepararCampos() {
        // El ID es automático (AUTO_INCREMENT): el campo solo muestra el valor.
        txtId.setDisable(true);
    }

    @Override
    protected void cargarCampos(Categoria c) {
        txtId.setText(String.valueOf(c.getId()));
        txtNombre.setText(c.getNombre());
        txtDescripcion.setText(c.getDescripcion());
    }

    @Override
    protected void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtDescripcion.clear();
    }

    @Override
    protected void habilitarCampos(boolean editable, boolean esNuevo) {
        txtNombre.setDisable(!editable);
        txtDescripcion.setDisable(!editable);
    }
}
