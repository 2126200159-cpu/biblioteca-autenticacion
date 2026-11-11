package edu.utj.evnd.poo.bibliotecaproyec.controller;

import edu.utj.evnd.poo.bibliotecaproyec.dao.Dao;
import edu.utj.evnd.poo.bibliotecaproyec.dao.PermisoDao;
import edu.utj.evnd.poo.bibliotecaproyec.dao.PersonaDao;
import edu.utj.evnd.poo.bibliotecaproyec.enums.Accion;
import edu.utj.evnd.poo.bibliotecaproyec.enums.EstadoAdministrador;
import edu.utj.evnd.poo.bibliotecaproyec.enums.Modulo;
import edu.utj.evnd.poo.bibliotecaproyec.enums.Permiso;
import edu.utj.evnd.poo.bibliotecaproyec.model.Administrador;
import edu.utj.evnd.poo.bibliotecaproyec.model.Lector;
import edu.utj.evnd.poo.bibliotecaproyec.model.Persona;
import edu.utj.evnd.poo.bibliotecaproyec.model.UsuarioSesion;
import edu.utj.evnd.poo.bibliotecaproyec.service.SesionActual;

import java.sql.SQLException;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

/**
 * Controlador de la vista unificada de personas: Persona, Administrador y
 * Lector en una sola pantalla. El combo "Tipo de persona" decide qué campos
 * propios se muestran y qué registro se crea (Administrador o Lector).
 *
 * @author Joshua Odin Montoya Flores
 */
public class PersonaController extends CrudController<Persona> {

    private static final String ADMINISTRADOR = "Administrador";
    private static final String LECTOR = "Lector";

    @FXML private TextField txtId;
    @FXML private TextField txtCurp;
    @FXML private TextField txtRfc;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private DatePicker dpNacimiento;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> cbTipo;

    @FXML private GridPane panelAdministrador;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private ComboBox<EstadoAdministrador> cbEstado;
    @FXML private ComboBox<String> cbRol;
    @FXML private DatePicker dpContratacion;

    @FXML private GridPane panelLector;
    @FXML private TextField txtCredencial;
    @FXML private TextField txtMulta;

    @FXML private Label errId;
    @FXML private Label errCurp;
    @FXML private Label errRfc;
    @FXML private Label errNombre;
    @FXML private Label errApellido;
    @FXML private Label errNacimiento;
    @FXML private Label errCorreo;
    @FXML private Label errTelefono;
    @FXML private Label errTipo;
    @FXML private Label errUsuario;
    @FXML private Label errContrasena;
    @FXML private Label errEstado;
    @FXML private Label errRol;
    @FXML private Label errContratacion;
    @FXML private Label errCredencial;
    @FXML private Label errMulta;

    @FXML private TableColumn<Persona, Integer> colId;
    @FXML private TableColumn<Persona, String> colCurp;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colApellido;
    @FXML private TableColumn<Persona, String> colTipo;
    @FXML private TableColumn<Persona, String> colCorreo;
    @FXML private TableColumn<Persona, String> colTelefono;

    private final PersonaDao dao = new PersonaDao();
    private final PermisoDao permisoDao = new PermisoDao();

    @Override
    protected Dao<Persona> dao() {
        return dao;
    }

    @Override
    protected String nombreEntidad() {
        return "Persona";
    }

    @Override
    protected Modulo modulo() {
        return Modulo.PERSONAS;
    }

    /** Las cuentas de administrador solo las gestiona quien tiene PERSONAS_GESTIONAR_ADMIN. */
    @Override
    protected boolean permitido(Accion accion, Persona entidad) {
        if (!super.permitido(accion, entidad)) {
            return false;
        }
        if (accion != Accion.CONSULTAR && entidad instanceof Administrador) {
            return SesionActual.tiene(Permiso.PERSONAS_GESTIONAR_ADMIN);
        }
        return true;
    }

    /** Evita que un administrador se bloquee o se elimine a sí mismo con la sesión abierta. */
    @Override
    protected String restriccion(Accion accion, Persona entidad) {
        UsuarioSesion yo = SesionActual.getUsuario();
        if (yo == null || entidad.getId() != yo.getIdPersona()) {
            return null;
        }
        if (accion == Accion.ELIMINAR) {
            return "No puedes eliminar tu propia cuenta mientras tienes la sesión iniciada.";
        }
        if (accion == Accion.EDITAR && entidad instanceof Administrador) {
            Administrador a = (Administrador) entidad;
            if (a.getEstado() != EstadoAdministrador.ACTIVO || !a.getRol().equals(yo.getRol())) {
                return "No puedes bloquear tu cuenta ni cambiar tu propio rol con la sesión iniciada.";
            }
        }
        return null;
    }

    private String tipoPorDefecto() {
        return SesionActual.tiene(Permiso.PERSONAS_GESTIONAR_ADMIN) ? ADMINISTRADOR : LECTOR;
    }

    @Override
    protected String prefijoImagen() {
        return "persona_" + txtId.getText().trim();
    }

    private boolean esAdministrador() {
        return ADMINISTRADOR.equals(cbTipo.getValue());
    }

    private boolean esLector() {
        return LECTOR.equals(cbTipo.getValue());
    }

    private static LocalDate aLocalDate(Date fecha) {
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static Date aDate(LocalDate fecha) {
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    protected Persona crearEntidad(Persona existente) {
        int id = Integer.parseInt(txtId.getText().trim());
        Persona persona;
        if (esAdministrador()) {
            // Al editar, una contraseña vacía conserva la que ya está guardada (hash)
            String clave = txtContrasena.getText();
            if (clave.isEmpty() && existente instanceof Administrador) {
                clave = ((Administrador) existente).getContrasena();
            }
            Administrador a = new Administrador(id, txtCurp.getText(), txtNombre.getText(),
                    txtApellido.getText(), txtUsuario.getText(), clave);
            a.setEstado(cbEstado.getValue());
            a.setRol(cbRol.getValue());
            a.setFechaContratacion(aDate(dpContratacion.getValue()));
            persona = a;
        } else {
            Lector l = new Lector(id, txtCurp.getText(), txtNombre.getText(),
                    txtApellido.getText(), txtCredencial.getText());
            l.setMultaPendiente(Double.parseDouble(txtMulta.getText().trim()));
            persona = l;
        }
        persona.setNacimiento(aDate(dpNacimiento.getValue()));
        persona.setRfc(txtRfc.getText());
        persona.setCorreo(txtCorreo.getText());
        persona.setTelefono(txtTelefono.getText());
        return persona;
    }

    @Override
    protected void registrarCampos() {
        filtrar(txtId, "\\d{0,9}", false);
        filtrar(txtCurp, "[A-Za-z0-9]{0,18}", true);
        filtrar(txtRfc, "[A-Za-z0-9]{0,13}", true);
        filtrar(txtNombre, "[\\p{L} ]{0,50}", false);
        filtrar(txtApellido, "[\\p{L} ]{0,50}", false);
        filtrar(txtCorreo, "\\S{0,80}", false);
        filtrar(txtTelefono, "\\d{0,10}", false);
        filtrar(txtUsuario, "[\\p{L}\\p{N}_.\\-]{0,20}", false);
        filtrar(txtContrasena, "\\S{0,30}", false);
        filtrar(txtCredencial, "[A-Za-z0-9\\-]{0,15}", true);
        filtrar(txtMulta, "\\d{0,6}(\\.\\d{0,2})?", false);
        dpNacimiento.getEditor().setEditable(false); // las fechas se eligen en el calendario
        dpContratacion.getEditor().setEditable(false);

        registrarCampo(txtId, errId, () -> {
            String t = txtId.getText().trim();
            if (t.isEmpty()) {
                return OBLIGATORIO;
            }
            if (Integer.parseInt(t) <= 0) {
                return "El ID debe ser mayor que 0.";
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
            if (esAdministrador() && Period.between(fecha, LocalDate.now()).getYears() < 18) {
                return "Un administrador debe tener al menos 18 años.";
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

        registrarCampo(cbTipo, errTipo, () -> {
            if (cbTipo.getValue() == null) {
                return "Selecciona el tipo de persona.";
            }
            if (esNuevo() && esAdministrador() && !SesionActual.tiene(Permiso.PERSONAS_GESTIONAR_ADMIN)) {
                return "Tu rol solo puede registrar lectores.";
            }
            return null;
        });

        // ----- Campos propios del administrador (solo se validan si el tipo es Administrador) -----
        registrarCampo(txtUsuario, errUsuario, () -> {
            if (!esAdministrador()) {
                return null;
            }
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
            if (!esAdministrador()) {
                return null;
            }
            String t = txtContrasena.getText();
            if (t.isEmpty()) {
                return esNuevo() ? OBLIGATORIO : null; // al editar, vacío = conservar la actual
            }
            if (t.length() < 4) {
                return "La contraseña debe tener al menos 4 caracteres.";
            }
            return null;
        });

        registrarCampo(cbEstado, errEstado, () ->
                esAdministrador() && cbEstado.getValue() == null ? "Selecciona un estado." : null);

        registrarCampo(cbRol, errRol, () ->
                esAdministrador() && cbRol.getValue() == null ? "Selecciona un rol." : null);

        registrarCampo(dpContratacion, errContratacion, () -> {
            if (!esAdministrador()) {
                return null;
            }
            LocalDate fecha = dpContratacion.getValue();
            if (fecha == null) {
                return OBLIGATORIO;
            }
            if (fecha.isAfter(LocalDate.now())) {
                return "La fecha de contratación no puede ser futura.";
            }
            return null;
        });

        // ----- Campos propios del lector (solo se validan si el tipo es Lector) -----
        registrarCampo(txtCredencial, errCredencial, () -> {
            if (!esLector()) {
                return null;
            }
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
            if (!esLector()) {
                return null;
            }
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
    protected void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCurp.setCellValueFactory(new PropertyValueFactory<>("curp"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
    }

    @Override
    protected void prepararCampos() {
        cbTipo.setItems(FXCollections.observableArrayList(ADMINISTRADOR, LECTOR));
        cbEstado.setItems(FXCollections.observableArrayList(EstadoAdministrador.values()));
        try {
            cbRol.setItems(FXCollections.observableArrayList(permisoDao.listarRoles()));
        } catch (SQLException ex) {
            errorBD(ex);
        }
        // Muestra solo los campos del tipo elegido
        cbTipo.valueProperty().addListener((obs, anterior, nuevo) -> mostrarPanel());
        mostrarPanel();
    }

    private void mostrarPanel() {
        boolean admin = esAdministrador();
        boolean lector = esLector();
        panelAdministrador.setVisible(admin);
        panelAdministrador.setManaged(admin);
        panelLector.setVisible(lector);
        panelLector.setManaged(lector);
    }

    @Override
    protected void cargarCampos(Persona p) {
        txtId.setText(String.valueOf(p.getId()));
        txtCurp.setText(p.getCurp());
        txtRfc.setText(p.getRfc());
        txtNombre.setText(p.getNombre());
        txtApellido.setText(p.getApellido());
        dpNacimiento.setValue(aLocalDate(p.getNacimiento()));
        txtCorreo.setText(p.getCorreo());
        txtTelefono.setText(p.getTelefono());
        if (p instanceof Administrador) {
            Administrador a = (Administrador) p;
            cbTipo.setValue(ADMINISTRADOR);
            txtUsuario.setText(a.getUsuario());
            txtContrasena.clear(); // la contraseña guardada es un hash: no se muestra
            cbEstado.setValue(a.getEstado());
            cbRol.setValue(a.getRol());
            dpContratacion.setValue(aLocalDate(a.getFechaContratacion()));
        } else if (p instanceof Lector) {
            Lector l = (Lector) p;
            cbTipo.setValue(LECTOR);
            txtCredencial.setText(l.getNumeroCredencial());
            txtMulta.setText(String.format(java.util.Locale.US, "%.2f", l.getMultaPendiente()));
        }
    }

    @Override
    protected void limpiarCampos() {
        txtId.clear();
        txtCurp.clear();
        txtRfc.clear();
        txtNombre.clear();
        txtApellido.clear();
        dpNacimiento.setValue(null);
        txtCorreo.clear();
        txtTelefono.clear();
        cbTipo.setValue(tipoPorDefecto());
        txtUsuario.clear();
        txtContrasena.clear();
        cbEstado.setValue(EstadoAdministrador.ACTIVO);
        cbRol.setValue(null);
        dpContratacion.setValue(LocalDate.now());
        txtCredencial.clear();
        txtMulta.setText("0");
    }

    @Override
    protected void habilitarCampos(boolean editable, boolean esNuevo) {
        // El ID lo captura el usuario solo al crear; el tipo no se puede cambiar al editar
        txtId.setDisable(!(editable && esNuevo));
        cbTipo.setDisable(!(editable && esNuevo));
        TextField[] textos = {txtCurp, txtRfc, txtNombre, txtApellido, txtCorreo, txtTelefono,
                txtUsuario, txtContrasena, txtCredencial, txtMulta};
        for (TextField t : textos) {
            t.setDisable(!editable);
        }
        dpNacimiento.setDisable(!editable);
        dpContratacion.setDisable(!editable);
        cbEstado.setDisable(!editable);
        cbRol.setDisable(!editable);
    }
}
