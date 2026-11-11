package edu.utj.evnd.poo.bibliotecaproyec.controller;

import edu.utj.evnd.poo.bibliotecaproyec.enums.Modulo;
import edu.utj.evnd.poo.bibliotecaproyec.dao.CategoriaDao;
import edu.utj.evnd.poo.bibliotecaproyec.dao.Dao;
import edu.utj.evnd.poo.bibliotecaproyec.dao.EditorialDao;
import edu.utj.evnd.poo.bibliotecaproyec.dao.LibroDao;
import edu.utj.evnd.poo.bibliotecaproyec.model.Categoria;
import edu.utj.evnd.poo.bibliotecaproyec.model.Editorial;
import edu.utj.evnd.poo.bibliotecaproyec.model.Libro;

import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

/**
 * Controlador de la vista de libros. Las listas de editoriales y categorías se
 * cargan desde la base de datos cada vez que se muestra la vista.
 *
 * @author Joshua Odin Montoya Flores
 */
public class LibroController extends CrudController<Libro> {

    @FXML private TextField txtId;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtIsbn;
    @FXML private ComboBox<Editorial> cbEditorial;
    @FXML private ComboBox<Categoria> cbCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtDisponibles;
    @FXML private TextField txtEnPedido;
    @FXML private TextField txtReorden;
    @FXML private CheckBox chkDescontinuado;

    @FXML private Label errTitulo;
    @FXML private Label errAutor;
    @FXML private Label errIsbn;
    @FXML private Label errEditorial;
    @FXML private Label errCategoria;
    @FXML private Label errPrecio;
    @FXML private Label errDisponibles;
    @FXML private Label errEnPedido;
    @FXML private Label errReorden;

    @FXML private TableColumn<Libro, Integer> colId;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colIsbn;
    @FXML private TableColumn<Libro, String> colCategoria;
    @FXML private TableColumn<Libro, String> colEditorial;
    @FXML private TableColumn<Libro, Double> colPrecio;
    @FXML private TableColumn<Libro, Integer> colDisponibles;
    @FXML private TableColumn<Libro, Boolean> colDescontinuado;

    private final LibroDao dao = new LibroDao();
    private final EditorialDao editorialDao = new EditorialDao();
    private final CategoriaDao categoriaDao = new CategoriaDao();

    @Override
    protected Dao<Libro> dao() {
        return dao;
    }

    @Override
    protected String nombreEntidad() {
        return "Libro";
    }

    @Override
    protected Modulo modulo() {
        return Modulo.LIBROS;
    }

    @Override
    protected Libro crearEntidad(Libro existente) {
        Libro l = new Libro(txtTitulo.getText(), txtAutor.getText(),
                cbEditorial.getValue() == null ? null : cbEditorial.getValue().getId(),
                cbCategoria.getValue() == null ? null : cbCategoria.getValue().getId());
        if (existente != null) {
            l.setId(existente.getId());
        }
        l.setIsbn(txtIsbn.getText());
        l.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
        l.setEjemplaresDisponibles(Integer.parseInt(txtDisponibles.getText().trim()));
        l.setEjemplaresEnPedido(Integer.parseInt(txtEnPedido.getText().trim()));
        l.setNivelReorden(Integer.parseInt(txtReorden.getText().trim()));
        l.setDescontinuado(chkDescontinuado.isSelected());
        return l;
    }

    @Override
    protected void registrarCampos() {
        filtrar(txtTitulo, ".{0,120}", false);
        filtrar(txtAutor, ".{0,100}", false);
        filtrar(txtIsbn, "[0-9Xx\\-]{0,17}", true);
        filtrar(txtPrecio, "\\d{0,6}(\\.\\d{0,2})?", false);
        filtrar(txtDisponibles, "\\d{0,6}", false);
        filtrar(txtEnPedido, "\\d{0,6}", false);
        filtrar(txtReorden, "\\d{0,6}", false);

        registrarCampo(txtTitulo, errTitulo, () ->
                txtTitulo.getText().trim().isEmpty() ? OBLIGATORIO : null);
        registrarCampo(txtAutor, errAutor, () ->
                txtAutor.getText().trim().isEmpty() ? OBLIGATORIO : null);
        registrarCampo(txtIsbn, errIsbn, () -> {
            String t = txtIsbn.getText().trim();
            if (!t.isEmpty() && t.length() < 10) {
                return "El ISBN debe tener al menos 10 caracteres.";
            }
            return null; // el ISBN es opcional
        });
        registrarCampo(cbEditorial, errEditorial, () ->
                cbEditorial.getValue() == null ? "Selecciona una editorial." : null);
        registrarCampo(cbCategoria, errCategoria, () ->
                cbCategoria.getValue() == null ? "Selecciona una categoría." : null);
        registrarCampo(txtPrecio, errPrecio, () -> {
            String t = txtPrecio.getText().trim();
            if (t.isEmpty()) {
                return OBLIGATORIO;
            }
            if (!t.matches("\\d+(\\.\\d{1,2})?")) {
                return "Formato decimal correcto, por ejemplo 350.00";
            }
            return null;
        });
        registrarCampo(txtDisponibles, errDisponibles, () ->
                txtDisponibles.getText().trim().isEmpty() ? OBLIGATORIO : null);
        registrarCampo(txtEnPedido, errEnPedido, () ->
                txtEnPedido.getText().trim().isEmpty() ? OBLIGATORIO : null);
        registrarCampo(txtReorden, errReorden, () ->
                txtReorden.getText().trim().isEmpty() ? OBLIGATORIO : null);
    }

    @Override
    protected void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("nombreCategoria"));
        colEditorial.setCellValueFactory(new PropertyValueFactory<>("nombreEditorial"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDisponibles.setCellValueFactory(new PropertyValueFactory<>("ejemplaresDisponibles"));
        colDescontinuado.setCellValueFactory(new PropertyValueFactory<>("descontinuado"));
    }

    @Override
    protected void prepararCampos() {
        txtId.setDisable(true); // ID automático (AUTO_INCREMENT)
        cbEditorial.setConverter(new StringConverter<Editorial>() {
            @Override
            public String toString(Editorial e) {
                return e == null ? "" : e.getNombre();
            }

            @Override
            public Editorial fromString(String texto) {
                return null;
            }
        });
        cbCategoria.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria c) {
                return c == null ? "" : c.getNombre();
            }

            @Override
            public Categoria fromString(String texto) {
                return null;
            }
        });
    }

    /** Recarga las editoriales y categorías desde la base de datos. */
    @Override
    protected void alMostrar() {
        try {
            cbEditorial.setItems(FXCollections.observableArrayList(editorialDao.listar()));
            cbCategoria.setItems(FXCollections.observableArrayList(categoriaDao.listar()));
        } catch (SQLException ex) {
            errorBD(ex);
        }
    }

    @Override
    protected void cargarCampos(Libro l) {
        txtId.setText(String.valueOf(l.getId()));
        txtTitulo.setText(l.getTitulo());
        txtAutor.setText(l.getAutor());
        txtIsbn.setText(l.getIsbn());
        cbEditorial.setValue(buscarEditorial(l.getIdEditorial()));
        cbCategoria.setValue(buscarCategoria(l.getIdCategoria()));
        txtPrecio.setText(String.format(java.util.Locale.US, "%.2f", l.getPrecio()));
        txtDisponibles.setText(String.valueOf(l.getEjemplaresDisponibles()));
        txtEnPedido.setText(String.valueOf(l.getEjemplaresEnPedido()));
        txtReorden.setText(String.valueOf(l.getNivelReorden()));
        chkDescontinuado.setSelected(l.isDescontinuado());
    }

    private Editorial buscarEditorial(Integer id) {
        for (Editorial e : cbEditorial.getItems()) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    private Categoria buscarCategoria(Integer id) {
        for (Categoria c : cbCategoria.getItems()) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    @Override
    protected void limpiarCampos() {
        txtId.clear();
        txtTitulo.clear();
        txtAutor.clear();
        txtIsbn.clear();
        cbEditorial.setValue(null);
        cbCategoria.setValue(null);
        txtPrecio.clear();
        txtDisponibles.setText("0");
        txtEnPedido.setText("0");
        txtReorden.setText("0");
        chkDescontinuado.setSelected(false);
    }

    @Override
    protected void habilitarCampos(boolean editable, boolean esNuevo) {
        TextField[] campos = {txtTitulo, txtAutor, txtIsbn, txtPrecio, txtDisponibles, txtEnPedido, txtReorden};
        for (TextField c : campos) {
            c.setDisable(!editable);
        }
        cbEditorial.setDisable(!editable);
        cbCategoria.setDisable(!editable);
        chkDescontinuado.setDisable(!editable);
    }
}
