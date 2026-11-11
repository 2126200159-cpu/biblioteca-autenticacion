package edu.utj.evnd.poo.bibliotecaproyec.controller;

import edu.utj.evnd.poo.bibliotecaproyec.App;
import edu.utj.evnd.poo.bibliotecaproyec.enums.Permiso;
import edu.utj.evnd.poo.bibliotecaproyec.model.UsuarioSesion;
import edu.utj.evnd.poo.bibliotecaproyec.service.SesionActual;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Controlador del menú principal: muestra al usuario con sesión, deja entrar
 * solo a los módulos que su rol puede consultar (los demás botones se ocultan y
 * sus vistas ni se cargan), y permite cerrar la sesión.
 *
 * @author Joshua Odin Montoya Flores
 */
public class MenuController implements Initializable {

    @FXML private StackPane contenedor;
    @FXML private Button btnPersonas;
    @FXML private Button btnLibros;
    @FXML private Button btnCategorias;
    @FXML private Button btnEditoriales;
    @FXML private Label lblUsuario;
    @FXML private Label lblRol;

    private Parent vistaPersonas;
    private Parent vistaLibros;
    private Parent vistaCategorias;
    private Parent vistaEditoriales;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UsuarioSesion usuario = SesionActual.getUsuario();
        if (usuario != null) {
            lblUsuario.setText(usuario.getNombreCompleto());
            lblRol.setText(usuario.getRol());
        }
        try {
            if (SesionActual.tiene(Permiso.PERSONAS_CONSULTAR)) {
                vistaPersonas = App.loadFXML("persona");
            }
            if (SesionActual.tiene(Permiso.LIBROS_CONSULTAR)) {
                vistaLibros = App.loadFXML("libro");
            }
            if (SesionActual.tiene(Permiso.CATEGORIAS_CONSULTAR)) {
                vistaCategorias = App.loadFXML("categoria");
            }
            if (SesionActual.tiene(Permiso.EDITORIALES_CONSULTAR)) {
                vistaEditoriales = App.loadFXML("editorial");
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        configurarBoton(btnPersonas, vistaPersonas);
        configurarBoton(btnLibros, vistaLibros);
        configurarBoton(btnCategorias, vistaCategorias);
        configurarBoton(btnEditoriales, vistaEditoriales);

        // Se abre el primer módulo al que el rol tiene acceso
        if (vistaPersonas != null) {
            onPersonas();
        } else if (vistaLibros != null) {
            onLibros();
        } else if (vistaCategorias != null) {
            onCategorias();
        } else if (vistaEditoriales != null) {
            onEditoriales();
        } else {
            Label aviso = new Label("Tu rol no tiene acceso a ningún módulo.");
            aviso.getStyleClass().add("aviso");
            contenedor.getChildren().setAll(aviso);
        }
    }

    /** Un módulo sin permiso de consulta no aparece en el menú. */
    private void configurarBoton(Button boton, Parent vista) {
        boolean permitido = vista != null;
        boton.setVisible(permitido);
        boton.setManaged(permitido);
    }

    @FXML
    private void onPersonas() {
        mostrar(vistaPersonas, btnPersonas);
    }

    @FXML
    private void onLibros() {
        mostrar(vistaLibros, btnLibros);
    }

    @FXML
    private void onCategorias() {
        mostrar(vistaCategorias, btnCategorias);
    }

    @FXML
    private void onEditoriales() {
        mostrar(vistaEditoriales, btnEditoriales);
    }

    /** Cierra la sesión, cierra esta ventana y regresa al login. */
    @FXML
    private void onCerrarSesion() {
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Deseas cerrar tu sesión?", ButtonType.OK, ButtonType.CANCEL);
        confirmar.setTitle("Cerrar sesión");
        confirmar.setHeaderText(null);
        Optional<ButtonType> respuesta = confirmar.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            App.cerrarSesion();
        }
    }

    @FXML
    private void onSalir() {
        App.registrarCierre();
        Platform.exit();
    }

    private void mostrar(Parent vista, Button activo) {
        if (vista == null) {
            return; // el rol no tiene acceso a este módulo
        }
        contenedor.getChildren().setAll(vista);
        for (Button b : new Button[]{btnPersonas, btnLibros, btnCategorias, btnEditoriales}) {
            b.getStyleClass().remove("activo");
        }
        activo.getStyleClass().add("activo");
    }
}
