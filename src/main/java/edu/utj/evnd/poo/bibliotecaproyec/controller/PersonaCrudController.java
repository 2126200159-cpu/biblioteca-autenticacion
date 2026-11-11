package edu.utj.evnd.poo.bibliotecaproyec.controller;

import edu.utj.evnd.poo.bibliotecaproyec.model.Persona;
import edu.utj.evnd.poo.bibliotecaproyec.repository.PersonaRepository;
import edu.utj.evnd.poo.bibliotecaproyec.service.ImagenService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 * Controlador base de las vistas de {@link Persona}. Gestiona los eventos, las
 * validaciones en tiempo real (filtros de teclado, mensajes debajo de cada
 * control), la validación completa del formulario y el estado de los botones.
 * Cada subclase solo aporta lo propio de su entidad. Las reglas de negocio de
 * los datos siguen estando en el modelo; esta clase comprueba la entrada del
 * usuario antes de llegar a él.
 *
 * @param <T> tipo de persona que gestiona
 * @author Joshua Odin Montoya Flores
 */
public abstract class PersonaCrudController<T extends Persona> implements Initializable {

    /** Mensaje estándar para campos obligatorios vacíos. */
    protected static final String OBLIGATORIO = "Este campo es obligatorio.";

    /**
     * Si es {@code true} (actividad de validaciones), Editar y Eliminar solo
     * tienen comportamiento visual: no modifican ni borran registros reales.
     * Cámbialo a {@code false} para restaurar el CRUD completo.
     */
    private static final boolean SOLO_VISUAL = true;

    private static final PseudoClass INVALIDO = PseudoClass.getPseudoClass("invalido");

    private enum Modo { NINGUNO, NUEVO, EDITAR }

    @FXML protected TextField txtId;
    @FXML protected TextField txtCurp;
    @FXML protected TextField txtRfc;
    @FXML protected TextField txtNombre;
    @FXML protected TextField txtApellido;
    @FXML protected DatePicker dpNacimiento;
    @FXML protected TextField txtCorreo;
    @FXML protected TextField txtTelefono;
    @FXML protected TextField txtBuscar;
    @FXML protected ImageView imgPreview;

    @FXML protected Label errId;
    @FXML protected Label errCurp;
    @FXML protected Label errRfc;
    @FXML protected Label errNombre;
    @FXML protected Label errApellido;
    @FXML protected Label errNacimiento;
    @FXML protected Label errCorreo;
    @FXML protected Label errTelefono;

    @FXML protected Button btnImagen;
    @FXML protected Button btnNuevo;
    @FXML protected Button btnGuardar;
    @FXML protected Button btnEditar;
    @FXML protected Button btnEliminar;
    @FXML protected Button btnValidar;
    @FXML protected Button btnLimpiar;
    @FXML protected Button btnBuscar;
    @FXML protected Button btnTodos;

    @FXML protected TableView<T> tabla;
    @FXML protected TableColumn<T, Integer> colId;
    @FXML protected TableColumn<T, String> colCurp;
    @FXML protected TableColumn<T, String> colNombre;
    @FXML protected TableColumn<T, String> colApellido;
    @FXML protected TableColumn<T, String> colCorreo;

    private final ImagenService imagenService = new ImagenService();
    private final ObservableList<T> datos = FXCollections.observableArrayList();
    /** Regla de cada campo: devuelve el mensaje de error o {@code null} si es válido. */
    private final Map<Control, Supplier<String>> reglas = new LinkedHashMap<>();
    /** Label donde se muestra el error de cada campo (independiente por campo). */
    private final Map<Control, Label> errores = new LinkedHashMap<>();
    private Modo modo = Modo.NINGUNO;
    private T seleccionado;
    private File imagenNueva;
    private boolean silenciar;

    // ===== Puntos de extensión que implementa cada entidad =====

    /** @return repositorio donde se guardan los registros */
    protected abstract PersonaRepository<T> repositorio();

    /** @return nombre de la entidad para los mensajes (p. ej. "Administrador") */
    protected abstract String nombreEntidad();

    /**
     * Construye la entidad con los datos del formulario (ya validados).
     *
     * @param id identificador del formulario
     * @return la entidad
     * @throws IllegalArgumentException si el modelo rechaza algún dato
     */
    protected abstract T crearEntidad(int id);

    /**
     * Registra los filtros de teclado y las reglas de los campos propios de
     * la entidad usando {@link #filtrar} y {@link #registrarCampo}.
     */
    protected abstract void registrarCamposEspecificos();

    /** Enlaza las columnas propias de la entidad. */
    protected abstract void configurarColumnasEspecificas();

    /** Prepara los controles propios (p. ej. cargar un ComboBox). */
    protected abstract void prepararCamposEspecificos();

    /** @param entidad registro cuyos datos propios se muestran en el formulario */
    protected abstract void cargarCamposEspecificos(T entidad);

    /** Deja los controles propios en su valor inicial. */
    protected abstract void limpiarCamposEspecificos();

    /** @param habilitar {@code true} para permitir editar los controles propios */
    protected abstract void habilitarCamposEspecificos(boolean habilitar);

    /**
     * Edad mínima en años exigida a la entidad. Las subclases pueden
     * sobrescribirlo; por defecto no se exige edad.
     *
     * @return edad mínima en años
     */
    protected int edadMinima() {
        return 0;
    }

    // ===== Inicialización =====

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tabla.setEditable(false); // la tabla es solo de lectura
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCurp.setCellValueFactory(new PropertyValueFactory<>("curp"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        configurarColumnasEspecificas();
        prepararCamposEspecificos();

        SortedList<T> ordenados = new SortedList<>(datos);
        ordenados.comparatorProperty().bind(tabla.comparatorProperty());
        tabla.setItems(ordenados);

        configurarValidaciones();
        registrarCamposEspecificos();

        txtBuscar.setOnAction(e -> onBuscar());

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> {
            if (nuevo != null) {
                seleccionado = nuevo;
                modo = Modo.NINGUNO;
                imagenNueva = null;
                mostrar(nuevo);
                habilitar(false, false);
                actualizarBotones();
            }
        });

        cancelar();
    }

    // ===== Validaciones en tiempo real =====

    /** Define los filtros de teclado y las reglas de los campos comunes. */
    private void configurarValidaciones() {
        filtrar(txtId, "\\d{0,9}", false);
        filtrar(txtCurp, "[A-Za-z0-9]{0,18}", true);
        filtrar(txtRfc, "[A-Za-z0-9]{0,13}", true);
        filtrar(txtNombre, "[\\p{L} ]{0,50}", false);
        filtrar(txtApellido, "[\\p{L} ]{0,50}", false);
        filtrar(txtCorreo, "\\S{0,80}", false);
        filtrar(txtTelefono, "\\d{0,10}", false);
        dpNacimiento.getEditor().setEditable(false); // la fecha se elige con el calendario

        registrarCampo(txtId, errId, () -> {
            String t = txtId.getText().trim();
            if (t.isEmpty()) {
                return OBLIGATORIO;
            }
            int id = Integer.parseInt(t); // el filtro garantiza solo dígitos (máx. 9)
            if (id <= 0) {
                return "El ID debe ser mayor que 0.";
            }
            if (modo == Modo.NUEVO && repositorio().buscarPorId(id) != null) {
                return "Ya existe un registro con ese ID.";
            }
            return null;
        });

        registrarCampo(txtCurp, errCurp, () -> {
            String t = txtCurp.getText().trim();
            if (t.isEmpty()) {
                return OBLIGATORIO;
            }
            if (t.length() != 18) {
                return "La CURP debe tener 18 letras o números.";
            }
            Integer exceptoId = modo == Modo.NUEVO ? null : idFormulario();
            if (repositorio().existeCurp(t, exceptoId)) {
                return "La CURP ya pertenece a otro registro.";
            }
            return null;
        });

        registrarCampo(txtRfc, errRfc, () -> {
            String t = txtRfc.getText().trim();
            if (t.isEmpty()) {
                return OBLIGATORIO;
            }
            if (t.length() < 12) {
                return "El RFC debe tener 12 o 13 caracteres.";
            }
            return null;
        });

        registrarCampo(txtNombre, errNombre, () ->
                txtNombre.getText().trim().isEmpty() ? OBLIGATORIO : null);

        registrarCampo(txtApellido, errApellido, () ->
                txtApellido.getText().trim().isEmpty() ? OBLIGATORIO : null);

        registrarCampo(dpNacimiento, errNacimiento, () -> {
            LocalDate fecha = dpNacimiento.getValue();
            if (fecha == null) {
                return OBLIGATORIO;
            }
            if (!fecha.isBefore(LocalDate.now())) {
                return "La fecha debe ser anterior a hoy.";
            }
            if (Period.between(fecha, LocalDate.now()).getYears() < edadMinima()) {
                return "Debe tener al menos " + edadMinima() + " años.";
            }
            return null;
        });

        registrarCampo(txtCorreo, errCorreo, () -> {
            String t = txtCorreo.getText().trim();
            if (t.isEmpty()) {
                return OBLIGATORIO;
            }
            if (!t.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                return "Formato correcto: usuario@dominio.com";
            }
            return null;
        });

        registrarCampo(txtTelefono, errTelefono, () -> {
            String t = txtTelefono.getText().trim();
            if (t.isEmpty()) {
                return OBLIGATORIO;
            }
            if (!t.matches("\\d{10}")) {
                return "El teléfono debe tener 10 dígitos.";
            }
            return null;
        });
    }

    /**
     * Restringe los caracteres que se pueden escribir o pegar en un campo
     * mediante un {@link TextFormatter}. Si el texto resultante no cumple la
     * expresión regular, el cambio se descarta.
     *
     * @param campo      campo de texto a restringir
     * @param regex      expresión que debe cumplir el texto completo
     * @param mayusculas {@code true} para convertir lo escrito a mayúsculas
     */
    protected void filtrar(TextField campo, String regex, boolean mayusculas) {
        Pattern patron = Pattern.compile(regex);
        campo.setTextFormatter(new TextFormatter<String>(cambio -> {
            if (mayusculas) {
                cambio.setText(cambio.getText().toUpperCase());
            }
            if (patron.matcher(cambio.getControlNewText()).matches()) {
                return cambio;
            }
            // Carácter rechazado: se avisa debajo del campo
            Label etiqueta = errores.get(campo);
            if (etiqueta != null && cambio.isContentChange()) {
                etiqueta.setText("Carácter no permitido o longitud máxima alcanzada.");
                campo.pseudoClassStateChanged(INVALIDO, true);
            }
            return null;
        }));
    }

    /**
     * Asocia una regla y un Label de error a un control, y vuelve a validarlo
     * cada vez que su valor cambia (al escribir, borrar, pegar o elegir).
     *
     * @param campo    control a validar
     * @param etiqueta Label que muestra el error debajo del control
     * @param regla    devuelve el mensaje de error, o {@code null} si es válido
     */
    protected void registrarCampo(Control campo, Label etiqueta, Supplier<String> regla) {
        reglas.put(campo, regla);
        errores.put(campo, etiqueta);
        if (campo instanceof TextField) {
            ((TextField) campo).textProperty().addListener((o, a, b) -> campoCambiado(campo));
        } else if (campo instanceof DatePicker) {
            ((DatePicker) campo).valueProperty().addListener((o, a, b) -> campoCambiado(campo));
        } else if (campo instanceof ComboBox) {
            ((ComboBox<?>) campo).valueProperty().addListener((o, a, b) -> campoCambiado(campo));
        }
    }

    private void campoCambiado(Control campo) {
        if (silenciar) {
            return;
        }
        mostrarError(campo);
        actualizarBotones();
    }

    /** Evalúa un campo y muestra o borra su mensaje. @return {@code true} si hay error */
    private boolean mostrarError(Control campo) {
        String mensaje = reglas.get(campo).get();
        errores.get(campo).setText(mensaje == null ? "" : mensaje);
        campo.pseudoClassStateChanged(INVALIDO, mensaje != null);
        return mensaje != null;
    }

    /** @return {@code true} si todos los campos cumplen sus reglas (no muestra mensajes) */
    private boolean formularioValido() {
        for (Supplier<String> regla : reglas.values()) {
            if (regla.get() != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validación completa: revisa todos los campos, muestra los mensajes de
     * error debajo de cada control y enfoca el primero con error.
     *
     * @return {@code true} si todos son válidos
     */
    private boolean validarTodo() {
        Control primero = null;
        for (Control campo : reglas.keySet()) {
            if (mostrarError(campo) && primero == null) {
                primero = campo;
            }
        }
        if (primero != null) {
            primero.requestFocus();
        }
        return primero == null;
    }

    private void limpiarMensajes() {
        for (Control campo : errores.keySet()) {
            errores.get(campo).setText("");
            campo.pseudoClassStateChanged(INVALIDO, false);
        }
    }

    private Integer idFormulario() {
        try {
            return Integer.valueOf(txtId.getText().trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    // ===== Acciones de los botones =====

    @FXML
    protected void onNuevo() {
        cancelar(); // limpia campos y mensajes
        modo = Modo.NUEVO;
        habilitar(true, true);
        actualizarBotones();
        txtId.requestFocus();
    }

    /** Comprueba todos los campos; no guarda, modifica ni elimina nada. */
    @FXML
    protected void onValidar() {
        if (validarTodo()) {
            alerta(Alert.AlertType.INFORMATION, "Validación correcta",
                    "Todos los datos cumplen las validaciones.");
        }
    }

    @FXML
    protected void onBuscar() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            alerta(Alert.AlertType.WARNING, "Búsqueda", "Escribe un ID, CURP, nombre o correo para buscar.");
            txtBuscar.requestFocus();
            return;
        }
        List<T> resultado = repositorio().buscar(texto);
        if (resultado.isEmpty()) {
            alerta(Alert.AlertType.ERROR, "No encontrado", "No existe ningún " + nombreEntidad().toLowerCase()
                    + " que coincida con la búsqueda.");
            txtBuscar.requestFocus();
            return;
        }
        reiniciarFormulario();
        datos.setAll(resultado);
        tabla.getSelectionModel().selectFirst();
        actualizarBotones();
    }

    @FXML
    protected void onMostrarTodos() {
        cancelar();
    }

    @FXML
    protected void onEditar() {
        if (seleccionado == null) {
            return;
        }
        modo = Modo.EDITAR;
        habilitar(true, false); // el id no se puede cambiar al editar
        actualizarBotones();
    }

    @FXML
    protected void onGuardar() {
        if (modo == Modo.NINGUNO || !validarTodo()) {
            return;
        }
        boolean esNuevo = modo == Modo.NUEVO;
        if (!esNuevo && SOLO_VISUAL) {
            cancelar();
            alerta(Alert.AlertType.INFORMATION, "Actualizar",
                    "Acción solo visual: el registro no se modificó.");
            return;
        }
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            T entidad = crearEntidad(id);
            entidad.setNacimiento(Date.from(dpNacimiento.getValue()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            entidad.setRfc(txtRfc.getText());
            entidad.setCorreo(txtCorreo.getText());
            entidad.setTelefono(txtTelefono.getText());

            String rutaAnterior = seleccionado != null ? seleccionado.getUrlImagen() : "";
            String ruta = rutaAnterior;
            if (imagenNueva != null) {
                ruta = imagenService.guardar(imagenNueva, id);
            }
            entidad.setUrlImagen(ruta);

            boolean ok = esNuevo ? repositorio().agregar(entidad) : repositorio().actualizar(entidad);
            if (!ok) {
                if (imagenNueva != null) {
                    imagenService.eliminar(ruta);
                }
                alerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el registro.");
                return;
            }
            if (imagenNueva != null && !esNuevo) {
                imagenService.eliminar(rutaAnterior);
            }
            cancelar();
            alerta(Alert.AlertType.INFORMATION, "Éxito", nombreEntidad()
                    + (esNuevo ? " guardado correctamente." : " actualizado correctamente."));
        } catch (IllegalArgumentException ex) {
            alerta(Alert.AlertType.WARNING, "Datos inválidos", ex.getMessage());
        } catch (IOException ex) {
            alerta(Alert.AlertType.ERROR, "Error con la imagen", ex.getMessage());
        }
    }

    @FXML
    protected void onEliminar() {
        if (seleccionado == null) {
            return;
        }
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de eliminar el registro seleccionado?\n" + seleccionado.getNombreCompleto(),
                ButtonType.OK, ButtonType.CANCEL);
        confirmar.setTitle("Confirmar eliminación");
        confirmar.setHeaderText(null);
        Optional<ButtonType> respuesta = confirmar.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }
        if (SOLO_VISUAL) {
            cancelar();
            alerta(Alert.AlertType.INFORMATION, "Eliminar",
                    "Acción solo visual: el registro no se eliminó.");
            return;
        }
        if (repositorio().eliminar(seleccionado.getId())) {
            imagenService.eliminar(seleccionado.getUrlImagen());
            cancelar();
            alerta(Alert.AlertType.INFORMATION, "Eliminado", nombreEntidad() + " eliminado correctamente.");
        } else {
            alerta(Alert.AlertType.ERROR, "Error", "No se encontró el registro para eliminar.");
        }
    }

    @FXML
    protected void onLimpiar() {
        cancelar();
    }

    @FXML
    protected void onSeleccionarImagen() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar imagen");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File archivo = selector.showOpenDialog(imgPreview.getScene().getWindow());
        if (archivo != null) {
            imagenNueva = archivo;
            imgPreview.setImage(cargarImagen(archivo));
        }
    }

    // ===== Apoyo interno =====

    /** Estado inicial: sin búsqueda, sin selección, formulario y mensajes limpios. */
    private void cancelar() {
        reiniciarFormulario();
        txtBuscar.clear();
        datos.setAll(repositorio().obtenerTodos());
        actualizarBotones();
    }

    private void reiniciarFormulario() {
        modo = Modo.NINGUNO;
        seleccionado = null;
        imagenNueva = null;
        tabla.getSelectionModel().clearSelection();
        silenciar = true;
        try {
            limpiarFormulario();
        } finally {
            silenciar = false;
        }
        limpiarMensajes();
        habilitar(false, false);
        actualizarBotones();
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtCurp.clear();
        txtRfc.clear();
        txtNombre.clear();
        txtApellido.clear();
        dpNacimiento.setValue(null);
        txtCorreo.clear();
        txtTelefono.clear();
        imgPreview.setImage(null);
        limpiarCamposEspecificos();
    }

    private void mostrar(T e) {
        silenciar = true;
        try {
            txtId.setText(String.valueOf(e.getId()));
            txtCurp.setText(e.getCurp());
            txtRfc.setText(e.getRfc());
            txtNombre.setText(e.getNombre());
            txtApellido.setText(e.getApellido());
            dpNacimiento.setValue(e.getNacimiento().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
            txtCorreo.setText(e.getCorreo());
            txtTelefono.setText(e.getTelefono());
            File imagen = imagenService.resolver(e.getUrlImagen());
            imgPreview.setImage(imagen != null ? cargarImagen(imagen) : null);
            cargarCamposEspecificos(e);
        } finally {
            silenciar = false;
        }
        limpiarMensajes();
    }

    private Image cargarImagen(File archivo) {
        return new Image(archivo.toURI().toString(), 140, 140, true, true);
    }

    private void habilitar(boolean editable, boolean idEditable) {
        txtId.setDisable(!(editable && idEditable));
        Control[] campos = {txtCurp, txtRfc, txtNombre, txtApellido, dpNacimiento,
                txtCorreo, txtTelefono, btnImagen};
        for (Control c : campos) {
            c.setDisable(!editable);
        }
        habilitarCamposEspecificos(editable);
    }

    /**
     * Estado de los botones: Nuevo y Validar siempre activos; Guardar solo si
     * se está capturando y todos los campos son válidos; Editar y Eliminar solo
     * si hay un registro seleccionado en la tabla.
     */
    private void actualizarBotones() {
        btnGuardar.setText(modo == Modo.EDITAR ? "Actualizar" : "Guardar");
        btnGuardar.setDisable(modo == Modo.NINGUNO || !formularioValido());
        btnEditar.setDisable(seleccionado == null || modo != Modo.NINGUNO);
        btnEliminar.setDisable(seleccionado == null);
    }

    /**
     * Muestra una ventana de mensaje al usuario.
     *
     * @param tipo    tipo de alerta
     * @param titulo  título de la ventana
     * @param mensaje texto a mostrar
     */
    protected void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
