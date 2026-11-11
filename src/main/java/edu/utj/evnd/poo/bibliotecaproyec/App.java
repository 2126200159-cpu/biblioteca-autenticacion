package edu.utj.evnd.poo.bibliotecaproyec;

import edu.utj.evnd.poo.bibliotecaproyec.dao.SesionDao;
import edu.utj.evnd.poo.bibliotecaproyec.model.UsuarioSesion;
import edu.utj.evnd.poo.bibliotecaproyec.service.AlmacenamientoService;
import edu.utj.evnd.poo.bibliotecaproyec.service.SesionActual;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.sql.SQLException;

/**
 * Aplicación JavaFX del sistema de biblioteca. Muestra primero el inicio de
 * sesión, luego el menú principal, y aplica una hoja de estilos común a todas las vistas.
 *
 * @author Joshua Odin Montoya Flores
 */
public class App extends Application {

    private static Scene scene;
    private static Stage ventanaPrincipal;

    /** La primera ventana que se muestra es el inicio de sesión. */
    @Override
    public void start(Stage stage) throws IOException {
        // Crea Documentos/SistemaBiblioteca y su subcarpeta de imágenes (si faltan) y anota el log
        AlmacenamientoService.inicializar();
        mostrarLogin(stage);
    }

    /**
     * Muestra la ventana de inicio de sesión.
     *
     * @param stage ventana a usar, o {@code null} para crear una nueva
     * @throws IOException si no se puede cargar login.fxml
     */
    public static void mostrarLogin(Stage stage) throws IOException {
        Stage login = stage != null ? stage : new Stage();
        Scene escena = new Scene(loadFXML("inicio_sesion"), 420, 500);
        aplicarEstilos(escena);
        login.setTitle("Iniciar sesión - Sistema de Biblioteca");
        login.setResizable(false);
        login.setScene(escena);
        login.show();
    }

    /**
     * Abre la ventana principal (menú). Solo se llama después de un login
     * correcto, con el usuario ya guardado en {@code SesionActual}.
     *
     * @param login ventana de login que se cierra al abrir la principal
     * @throws IOException si no se puede cargar menu.fxml
     */
    public static void mostrarPrincipal(Stage login) throws IOException {
        Stage principal = new Stage();
        scene = new Scene(loadFXML("menu"), 1020, 720);
        aplicarEstilos(scene);
        principal.setTitle("Sistema de Biblioteca");
        principal.setMinWidth(900);
        principal.setMinHeight(640);
        principal.setScene(scene);
        // Cerrar la ventana con la X también termina la sesión en la bitácora
        principal.setOnHidden(e -> registrarCierre());
        ventanaPrincipal = principal;
        principal.show();
        if (login != null) {
            login.close();
        }
    }

    /**
     * Cierra la sesión: la registra en la base, olvida al usuario, cierra la
     * ventana principal y vuelve al login.
     */
    public static void cerrarSesion() {
        Stage principal = ventanaPrincipal;
        ventanaPrincipal = null;
        registrarCierre();
        try {
            mostrarLogin(null); // el login se abre antes de cerrar la principal para no terminar la aplicación
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } finally {
            if (principal != null) {
                principal.close();
            }
        }
    }

    /** Anota el cierre en {@code sesion_log} y borra la sesión en memoria (si hay una abierta). */
    public static void registrarCierre() {
        UsuarioSesion usuario = SesionActual.getUsuario();
        SesionActual.cerrar();
        if (usuario != null) {
            try {
                new SesionDao().registrarCierre(usuario.getIdSesion());
            } catch (SQLException ignorado) {
                // el cierre local ya se hizo; la bitácora queda con la sesión abierta
            }
        }
    }

    private static void aplicarEstilos(Scene escena) {
        URL css = App.class.getResource("/styles/app.css");
        if (css != null) {
            escena.getStylesheets().add(css.toExternalForm());
        }
    }

    /**
     * Cambia la vista raíz de la ventana principal.
     *
     * @param fxml nombre del archivo FXML (con o sin extensión)
     * @throws IOException si no se encuentra o no se puede cargar
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Carga un archivo FXML desde los recursos del proyecto.
     *
     * @param fxml nombre del archivo FXML (con o sin extensión)
     * @return nodo raíz de la vista
     * @throws IOException si no se encuentra o no se puede cargar
     */
    public static Parent loadFXML(String fxml) throws IOException {
        String fileName = fxml.endsWith(".fxml") ? fxml : fxml + ".fxml";
        FXMLLoader fxmlLoader = new FXMLLoader();

        String[] rutas = {
            "/" + fileName,
            "/edu/utj/evnd/poo/bibliotecaproyec/" + fileName,
            fileName
        };

        InputStream is = null;
        for (String ruta : rutas) {
            is = App.class.getResourceAsStream(ruta);
            if (is != null) {
                fxmlLoader.setLocation(App.class.getResource(ruta));
                break;
            }
        }
        if (is == null) {
            throw new IOException("El archivo " + fileName + " no está en src/main/resources.");
        }
        return fxmlLoader.load(is);
    }

    public static void main(String[] args) {
        launch();
    }
}
