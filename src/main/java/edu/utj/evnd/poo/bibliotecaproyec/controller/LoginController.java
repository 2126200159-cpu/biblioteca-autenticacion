package edu.utj.evnd.poo.bibliotecaproyec.controller;

import edu.utj.evnd.poo.bibliotecaproyec.App;
import edu.utj.evnd.poo.bibliotecaproyec.dao.AutenticacionDao;
import edu.utj.evnd.poo.bibliotecaproyec.dao.AutenticacionException;
import edu.utj.evnd.poo.bibliotecaproyec.dao.SesionDao;
import edu.utj.evnd.poo.bibliotecaproyec.model.UsuarioSesion;
import edu.utj.evnd.poo.bibliotecaproyec.service.SesionActual;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Ventana de inicio de sesión (la primera que se ejecuta). Pide el ID o correo
 * y la contraseña, los valida con {@link AutenticacionDao} y, si la cuenta está
 * activa, abre la ventana principal. No existe registro de cuentas.
 *
 * @author Joshua Odin Montoya Flores
 */
public class LoginController implements Initializable {

    private static final int INTENTOS_MAXIMOS = 5;
    private static final int SEGUNDOS_BLOQUEO = 15;

    @FXML private TextField txtIdentificador;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;
    @FXML private Button btnIngresar;

    private final AutenticacionDao autenticacionDao = new AutenticacionDao();
    private final SesionDao sesionDao = new SesionDao();
    private int intentosFallidos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblError.setText("");
        // El botón solo se activa con ambos campos llenos
        btnIngresar.disableProperty().bind(
                txtIdentificador.textProperty().isEmpty().or(txtContrasena.textProperty().isEmpty()));
        txtIdentificador.setOnAction(e -> txtContrasena.requestFocus());
        txtContrasena.setOnAction(e -> onIngresar());
    }

    @FXML
    private void onIngresar() {
        String identificador = txtIdentificador.getText().trim();
        String clave = txtContrasena.getText();
        if (identificador.isEmpty() || clave.isEmpty()) {
            mostrarError("Escribe tu ID o correo y tu contraseña.");
            return;
        }
        try {
            UsuarioSesion usuario = autenticacionDao.autenticar(identificador, clave);
            usuario.setIdSesion(sesionDao.registrarInicio(usuario.getIdPersona()));
            SesionActual.iniciar(usuario);
            txtContrasena.clear();
            try {
                App.mostrarPrincipal((Stage) txtContrasena.getScene().getWindow());
            } catch (IOException | UncheckedIOException ex) {
                App.registrarCierre(); // no se pudo abrir la ventana principal: no dejar la sesión abierta
                mostrarError("No se pudo abrir la ventana principal: " + ex.getMessage());
            }
        } catch (AutenticacionException ex) {
            intentosFallidos++;
            txtContrasena.clear();
            mostrarError(ex.getMessage());
            if (intentosFallidos >= INTENTOS_MAXIMOS) {
                bloquearTemporalmente();
            } else {
                txtContrasena.requestFocus();
            }
        } catch (SQLException ex) {
            mostrarError("No se pudo conectar con la base de datos. Revisa que MySQL/MariaDB esté iniciado.");
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }

    /** Tras varios intentos fallidos seguidos, bloquea el formulario unos segundos. */
    private void bloquearTemporalmente() {
        txtIdentificador.setDisable(true);
        txtContrasena.setDisable(true);
        mostrarError("Demasiados intentos fallidos. Espera " + SEGUNDOS_BLOQUEO + " segundos.");
        PauseTransition pausa = new PauseTransition(Duration.seconds(SEGUNDOS_BLOQUEO));
        pausa.setOnFinished(e -> {
            intentosFallidos = 0;
            txtIdentificador.setDisable(false);
            txtContrasena.setDisable(false);
            lblError.setText("");
            txtContrasena.requestFocus();
        });
        pausa.play();
    }
}
