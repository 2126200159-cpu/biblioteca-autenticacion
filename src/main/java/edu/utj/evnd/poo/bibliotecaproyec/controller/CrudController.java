package edu.utj.evnd.poo.bibliotecaproyec.controller;

import edu.utj.evnd.poo.bibliotecaproyec.dao.Dao;
import edu.utj.evnd.poo.bibliotecaproyec.dao.PermisoDao;
import edu.utj.evnd.poo.bibliotecaproyec.enums.Accion;
import edu.utj.evnd.poo.bibliotecaproyec.enums.Modulo;
import edu.utj.evnd.poo.bibliotecaproyec.enums.Permiso;
import edu.utj.evnd.poo.bibliotecaproyec.model.Imagenable;
import edu.utj.evnd.poo.bibliotecaproyec.model.UsuarioSesion;
import edu.utj.evnd.poo.bibliotecaproyec.service.ImagenService;
import edu.utj.evnd.poo.bibliotecaproyec.service.SesionActual;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javafx.application.Platform;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 * Controlador base del CRUD con base de datos. Contiene lo común a todas las
 * vistas: modos Nuevo/Editar, búsqueda, tabla de solo lectura, validaciones en
 * tiempo real y gestión de la imagen (elegir con FileChooser, vista previa,
 * copiar al proyecto, guardar la ruta, reemplazar al actualizar y borrar al
 * eliminar el registro). Cada subclase aporta lo propio de su entidad.
 *
 * @param <T> tipo de entidad que gestiona
 * @author Joshua Odin Montoya Flores
 */
public abstract class CrudController<T extends Imagenable> implements Initializable {

    /** Mensaje estándar para campos obligatorios vacíos. */
    protected static final String OBLIGATORIO = "Este campo es obligatorio.";

    private static final PseudoClass INVALIDO = PseudoClass.getPseudoClass("invalido");

    private enum Modo { NINGUNO, NUEVO, EDITAR }

    @FXML protected VBox raiz;
    @FXML protected TextField txtBuscar;
    @FXML protected ImageView imgPreview;

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

    private final ImagenService imagenService = new ImagenService();
    private final PermisoDao permisoDao = new PermisoDao();
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

    /** @return DAO con el que se accede a la base de datos */
    protected abstract Dao<T> dao();

    /** @return nombre de la entidad para los mensajes (p. ej. "Libro") */
    protected abstract String nombreEntidad();

    /** @return módulo al que pertenece la vista (para aplicar los permisos del rol) */
    protected abstract Modulo modulo();

    /**
     * Construye la entidad con los datos del formulario.
     *
     * @param existente registro que se está editando, o {@code null} si es nuevo
     * @return la entidad
     * @throws IllegalArgumentException si el modelo rechaza algún dato
     */
    protected abstract T crearEntidad(T existente);

    /** Registra los filtros de teclado y las reglas de los campos con {@link #filtrar} y {@link #registrarCampo}. */
    protected abstract void registrarCampos();

    /** Enlaza las columnas de la tabla. */
    protected abstract void configurarColumnas();

    /** Prepara los controles (p. ej. cargar los ComboBox con valores fijos). */
    protected abstract void prepararCampos();

    /** @param entidad registro que se muestra en el formulario */
    protected abstract void cargarCampos(T entidad);

    /** Deja los campos en su valor inicial. */
    protected abstract void limpiarCampos();

    /**
     * @param editable {@code true} para permitir escribir en los campos
     * @param esNuevo  {@code true} si se está creando un registro (p. ej. el id manual solo se escribe al crear)
     */
    protected abstract void habilitarCampos(boolean editable, boolean esNuevo);

    /** Se ejecuta cada vez que la vista se muestra (p. ej. para recargar combos). */
    protected void alMostrar() {
    }

    // ===== Autorización =====

    /**
     * Indica si el rol del usuario con sesión puede hacer la acción. Las
     * subclases pueden añadir reglas más finas (p. ej. según el tipo de registro).
     *
     * @param accion  acción que se quiere hacer
     * @param entidad registro sobre el que se hace, o {@code null} si es uno nuevo todavía sin construir
     * @return {@code true} si está permitido
     */
    protected boolean permitido(Accion accion, T entidad) {
        return SesionActual.tiene(Permiso.de(modulo(), accion));
    }

    /**
     * Motivo por el que una acción concreta no debe hacerse aunque el rol la
     * permita (p. ej. eliminar la propia cuenta).
     *
     * @return mensaje para el usuario, o {@code null} si no hay restricción
     */
    protected String restriccion(Accion accion, T entidad) {
        return null;
    }

    /** @return {@code true} si se está capturando un registro nuevo */
    protected boolean esNuevo() {
        return modo == Modo.NUEVO;
    }

    /**
     * Comprobación previa a escribir en la base: permiso en memoria y, además,
     * validado otra vez contra la base de datos con {@link PermisoDao}.
     */
    private boolean autorizado(Accion accion, T entidad) {
        if (!permitido(accion, entidad)) {
            return false;
        }
        UsuarioSesion usuario = SesionActual.getUsuario();
        if (usuario == null) {
            return false;
        }
        try {
            return permisoDao.tienePermiso(usuario.getRol(), Permiso.de(modulo(), accion));
        } catch (SQLException ex) {
            return false;
        }
    }

    private void denegar(Accion accion) {
        UsuarioSesion usuario = SesionActual.getUsuario();
        String rol = usuario == null ? "sin sesión" : usuario.getRol();
        alerta(Alert.AlertType.WARNING, "Acción no autorizada",
                "Tu rol (" + rol + ") no tiene permiso para " + accion.getVerbo() + " en este módulo.");
    }

    /** Oculta los botones que el rol nunca puede usar; el resto se habilita según el estado del formulario. */
    private void aplicarPermisosVisibles() {
        boolean crear = SesionActual.tiene(Permiso.de(modulo(), Accion.CREAR));
        boolean editar = SesionActual.tiene(Permiso.de(modulo(), Accion.EDITAR));
        boolean eliminar = SesionActual.tiene(Permiso.de(modulo(), Accion.ELIMINAR));
        mostrarBoton(btnNuevo, crear);
        mostrarBoton(btnEditar, editar);
        mostrarBoton(btnEliminar, eliminar);
        mostrarBoton(btnGuardar, crear || editar);
        mostrarBoton(btnImagen, crear || editar);
        mostrarBoton(btnValidar, crear || editar);
    }

    private static void mostrarBoton(Button boton, boolean visible) {
        boton.setVisible(visible);
        boton.setManaged(visible);
    }

    /** @return texto con el que empieza el nombre del archivo de imagen */
    protected String prefijoImagen() {
        return nombreEntidad().toLowerCase();
    }

    // ===== Inicialización =====

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tabla.setEditable(false); // la tabla es solo de lectura
        configurarColumnas();
        prepararCampos();

        SortedList<T> ordenados = new SortedList<>(datos);
        ordenados.comparatorProperty().bind(tabla.comparatorProperty());
        tabla.setItems(ordenados);

        registrarCampos();
        txtBuscar.setOnAction(e -> onBuscar());
        aplicarPermisosVisibles();

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

        // Cada vez que la vista se muestra, se recarga desde la base de datos
        raiz.parentProperty().addListener((obs, anterior, nuevo) -> {
            if (nuevo != null) {
                alMostrar();
                cancelar(); // estado inicial y tabla recargada desde la base de datos
            }
        });

        cancelar();
    }

    // ===== Validaciones en tiempo real =====

    /**
     * Restringe los caracteres que se pueden escribir o pegar en un campo
     * mediante un {@link TextFormatter}. Si el texto resultante no cumple la
     * expresión regular, el cambio se descarta y se avisa debajo del campo.
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
     * Validación completa: revisa todos los campos, muestra los errores debajo
     * de cada control y enfoca el primero con error.
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

    // ===== Acciones de los botones =====

    @FXML
    protected void onNuevo() {
        if (!permitido(Accion.CREAR, null)) {
            denegar(Accion.CREAR);
            return;
        }
        cancelar();
        modo = Modo.NUEVO;
        habilitar(true, true);
        actualizarBotones();
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
            alerta(Alert.AlertType.WARNING, "Búsqueda", "Escribe un texto para buscar.");
            txtBuscar.requestFocus();
            return;
        }
        try {
            List<T> resultado = dao().buscar(texto);
            if (resultado.isEmpty()) {
                alerta(Alert.AlertType.ERROR, "No encontrado", "No hay registros que coincidan con la búsqueda.");
                txtBuscar.requestFocus();
                return;
            }
            reiniciarFormulario();
            datos.setAll(resultado);
            tabla.getSelectionModel().selectFirst();
            actualizarBotones();
        } catch (SQLException ex) {
            alerta(Alert.AlertType.ERROR, "Base de datos", mensajeSql(ex));
        }
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
        if (!permitido(Accion.EDITAR, seleccionado)) {
            denegar(Accion.EDITAR);
            return;
        }
        modo = Modo.EDITAR;
        habilitar(true, false);
        actualizarBotones();
    }

    /**
     * Guarda (INSERT) o actualiza (UPDATE) el registro. Si se eligió una imagen
     * nueva, la copia al proyecto y guarda su ruta; al actualizar borra la imagen
     * anterior. Si la base de datos rechaza la operación, borra la copia nueva.
     */
    @FXML
    protected void onGuardar() {
        if (modo == Modo.NINGUNO || !validarTodo()) {
            return;
        }
        boolean esNuevo = modo == Modo.NUEVO;
        String rutaNueva = null;
        try {
            T entidad = crearEntidad(esNuevo ? null : seleccionado);
            Accion accion = esNuevo ? Accion.CREAR : Accion.EDITAR;
            if (!autorizado(accion, entidad)) {
                denegar(accion);
                return;
            }
            String motivo = restriccion(accion, entidad);
            if (motivo != null) {
                alerta(Alert.AlertType.WARNING, "Acción no permitida", motivo);
                return;
            }
            String rutaAnterior = seleccionado != null ? seleccionado.getUrlImagen() : "";
            entidad.setUrlImagen(rutaAnterior);
            if (imagenNueva != null) {
                rutaNueva = imagenService.guardar(imagenNueva, prefijoImagen());
                entidad.setUrlImagen(rutaNueva);
            }
            if (esNuevo) {
                dao().insertar(entidad);
            } else {
                dao().actualizar(entidad);
                if (rutaNueva != null) {
                    imagenService.eliminar(rutaAnterior); // reemplazo: borra la imagen vieja
                }
            }
            cancelar();
            alerta(Alert.AlertType.INFORMATION, "Éxito", nombreEntidad()
                    + (esNuevo ? " guardado correctamente." : " actualizado correctamente."));
        } catch (IllegalArgumentException ex) {
            alerta(Alert.AlertType.WARNING, "Datos inválidos", ex.getMessage());
        } catch (IOException ex) {
            alerta(Alert.AlertType.ERROR, "Error con la imagen", ex.getMessage());
        } catch (SQLException ex) {
            imagenService.eliminar(rutaNueva); // la base rechazó el guardado: no dejar la copia huérfana
            alerta(Alert.AlertType.ERROR, "Base de datos", mensajeSql(ex));
        }
    }

    /**
     * Elimina el registro de la base de datos y, solo si se eliminó realmente,
     * borra también su imagen del proyecto.
     */
    @FXML
    protected void onEliminar() {
        if (seleccionado == null) {
            return;
        }
        if (!autorizado(Accion.ELIMINAR, seleccionado)) {
            denegar(Accion.ELIMINAR);
            return;
        }
        String motivo = restriccion(Accion.ELIMINAR, seleccionado);
        if (motivo != null) {
            alerta(Alert.AlertType.WARNING, "Acción no permitida", motivo);
            return;
        }
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de eliminar el registro seleccionado?", ButtonType.OK, ButtonType.CANCEL);
        confirmar.setTitle("Confirmar eliminación");
        confirmar.setHeaderText(null);
        Optional<ButtonType> respuesta = confirmar.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }
        try {
            dao().eliminar(seleccionado);
            imagenService.eliminar(seleccionado.getUrlImagen());
            cancelar();
            alerta(Alert.AlertType.INFORMATION, "Eliminado", nombreEntidad() + " eliminado correctamente.");
        } catch (SQLException ex) {
            alerta(Alert.AlertType.ERROR, "Base de datos", mensajeSql(ex));
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

    /** Estado inicial: sin búsqueda, sin selección, formulario y mensajes limpios y tabla completa. */
    private void cancelar() {
        reiniciarFormulario();
        txtBuscar.clear();
        refrescarTabla();
        actualizarBotones();
    }

    private void reiniciarFormulario() {
        modo = Modo.NINGUNO;
        seleccionado = null;
        imagenNueva = null;
        tabla.getSelectionModel().clearSelection();
        silenciar = true;
        try {
            imgPreview.setImage(null);
            limpiarCampos();
        } finally {
            silenciar = false;
        }
        limpiarMensajes();
        habilitar(false, false);
        actualizarBotones();
    }

    /** Vuelve a cargar todos los registros de la base de datos en la tabla. */
    protected void refrescarTabla() {
        try {
            datos.setAll(dao().listar());
        } catch (SQLException ex) {
            errorBD(ex);
        }
    }

    private void mostrar(T e) {
        silenciar = true;
        try {
            File imagen = imagenService.resolver(e.getUrlImagen());
            imgPreview.setImage(imagen != null ? cargarImagen(imagen) : null);
            cargarCampos(e);
        } finally {
            silenciar = false;
        }
        limpiarMensajes();
    }

    private Image cargarImagen(File archivo) {
        return new Image(archivo.toURI().toString(), 140, 140, true, true);
    }

    private void habilitar(boolean editable, boolean esNuevo) {
        btnImagen.setDisable(!editable);
        habilitarCampos(editable, esNuevo);
    }

    /**
     * Estado de los botones: Validar siempre activo; Nuevo, Guardar, Editar y
     * Eliminar además requieren el permiso correspondiente del rol. Guardar solo
     * si se está capturando y todos los campos son válidos; Editar y Eliminar
     * solo si hay un registro seleccionado en la tabla.
     */
    private void actualizarBotones() {
        btnGuardar.setText(modo == Modo.EDITAR ? "Actualizar" : "Guardar");
        Accion accionGuardar = modo == Modo.EDITAR ? Accion.EDITAR : Accion.CREAR;
        btnNuevo.setDisable(!permitido(Accion.CREAR, null));
        btnGuardar.setDisable(modo == Modo.NINGUNO || !formularioValido()
                || !permitido(accionGuardar, modo == Modo.EDITAR ? seleccionado : null));
        btnEditar.setDisable(seleccionado == null || modo != Modo.NINGUNO
                || !permitido(Accion.EDITAR, seleccionado));
        btnEliminar.setDisable(seleccionado == null || !permitido(Accion.ELIMINAR, seleccionado));
    }

    /**
     * Traduce un error de la base de datos a un mensaje entendible.
     *
     * @param ex excepción SQL
     * @return mensaje para el usuario
     */
    protected String mensajeSql(SQLException ex) {
        switch (ex.getErrorCode()) {
            case 1062:
                return "Ya existe un registro con ese valor único (ID, CURP, usuario, credencial, nombre o ISBN).";
            case 1451:
                return "No se puede eliminar: el registro está relacionado con otros datos.";
            case 1452:
                return "Dato relacionado inexistente (revisa editorial, categoría o estado).";
            default:
                return "Error de base de datos: " + ex.getMessage();
        }
    }

    /**
     * Avisa de un error de base de datos sin bloquear la carga de la ventana.
     *
     * @param ex excepción SQL
     */
    protected void errorBD(SQLException ex) {
        String mensaje = mensajeSql(ex);
        Platform.runLater(() -> alerta(Alert.AlertType.ERROR, "Base de datos", mensaje));
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
