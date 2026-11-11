package edu.utj.evnd.poo.bibliotecaproyec.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Ubicación externa de los datos de la aplicación. Todo vive dentro de la
 * carpeta <b>Documentos</b> del usuario, para que la aplicación funcione igual
 * desde un JAR, un paquete o el IDE:
 * <pre>
 * Documentos/SistemaBiblioteca/            carpeta principal
 * Documentos/SistemaBiblioteca/imagenes/   imágenes de personas, libros, etc.
 * Documentos/SistemaBiblioteca/log_directorios.txt   bitácora de creación de carpetas
 * Documentos/SistemaBiblioteca/configuracion.properties   conexión a la base (la crea ConexionBD)
 * </pre>
 * Si las carpetas no existen se crean; si ya existen se continúa normalmente.
 * Cada verificación se anota en el log con fecha, hora y resultado.
 *
 * @author Joshua Odin Montoya Flores
 */
public final class AlmacenamientoService {

    /** Nombre de la carpeta principal dentro de Documentos. */
    public static final String CARPETA_APP = "SistemaBiblioteca";
    /** Nombre de la subcarpeta de imágenes. */
    public static final String CARPETA_IMAGENES = "imagenes";
    /** Nombre del archivo de log. */
    public static final String ARCHIVO_LOG = "log_directorios.txt";

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static Path carpetaApp;
    private static Path carpetaImagenes;

    private AlmacenamientoService() {
    }

    /**
     * Verifica (y crea si hace falta) la carpeta principal y la de imágenes, y
     * registra el resultado de cada una en el log. Se llama al iniciar la
     * aplicación; es seguro llamarla varias veces.
     */
    public static synchronized void inicializar() {
        Path documentos = localizarDocumentos();
        carpetaApp = documentos.resolve(CARPETA_APP);
        carpetaImagenes = carpetaApp.resolve(CARPETA_IMAGENES);
        verificar(true);
    }

    /** @return carpeta principal de la aplicación (dentro de Documentos) */
    public static synchronized Path getCarpetaApp() {
        if (carpetaApp == null) {
            inicializar();
        }
        return carpetaApp;
    }

    /**
     * Devuelve la carpeta de imágenes. Si alguien la borró mientras la
     * aplicación estaba abierta, la vuelve a crear (y lo anota en el log).
     *
     * @return carpeta de imágenes
     */
    public static synchronized Path getCarpetaImagenes() {
        if (carpetaImagenes == null) {
            inicializar();
        } else {
            verificar(false);
        }
        return carpetaImagenes;
    }

    /** @return ruta completa del archivo de log */
    public static Path getArchivoLog() {
        return getCarpetaApp().resolve(ARCHIVO_LOG);
    }

    /**
     * @param registrarExistentes {@code true} anota también las carpetas que ya
     *                            existían; {@code false} solo anota las que hubo que crear
     */
    private static void verificar(boolean registrarExistentes) {
        // Las anotaciones de la carpeta principal se escriben hasta que exista,
        // porque el log vive dentro de ella.
        List<String> pendientes = new ArrayList<>();
        asegurar(carpetaApp, pendientes, registrarExistentes);
        vaciar(pendientes);
        asegurar(carpetaImagenes, pendientes, registrarExistentes);
        vaciar(pendientes);
    }

    private static void asegurar(Path carpeta, List<String> pendientes, boolean registrarExistentes) {
        if (Files.isDirectory(carpeta)) {
            if (registrarExistentes) {
                pendientes.add(linea("VERIFICAR_DIRECTORIO", carpeta,
                        "EXITO - ya existe, se continua con el funcionamiento normal"));
            }
            return;
        }
        try {
            Files.createDirectories(carpeta);
            pendientes.add(linea("CREAR_DIRECTORIO", carpeta, "EXITO - directorio creado"));
        } catch (IOException | RuntimeException ex) {
            pendientes.add(linea("CREAR_DIRECTORIO", carpeta, "ERROR - " + ex));
        }
    }

    private static String linea(String operacion, Path carpeta, String resultado) {
        return LocalDateTime.now().format(FORMATO_FECHA) + " | " + operacion + " | "
                + carpeta + " | " + resultado;
    }

    /** Escribe las anotaciones pendientes en el log; si no se puede, las manda a la consola. */
    private static void vaciar(List<String> pendientes) {
        if (pendientes.isEmpty()) {
            return;
        }
        try {
            Files.write(carpetaApp.resolve(ARCHIVO_LOG), pendientes, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException | RuntimeException ex) {
            pendientes.forEach(System.err::println);
            System.err.println("No se pudo escribir el log: " + ex);
        }
        pendientes.clear();
    }

    // ------------------------------------------------------------------ Documentos

    /**
     * Localiza la carpeta Documentos del usuario. En Windows se consulta al
     * sistema (así se respeta OneDrive o una carpeta redirigida); en los demás
     * casos se prueban las ubicaciones habituales ({@code Documents},
     * {@code Documentos}).
     *
     * @return la carpeta Documentos (aunque aún no exista, {@code ~/Documents})
     */
    static Path localizarDocumentos() {
        Path home = Paths.get(System.getProperty("user.home"));
        if (System.getProperty("os.name", "").toLowerCase().contains("win")) {
            Path registro = documentosDesdeRegistroWindows();
            if (registro != null && Files.isDirectory(registro)) {
                return registro;
            }
        }
        List<Path> candidatos = new ArrayList<>();
        String onedrive = System.getenv("OneDrive");
        if (onedrive != null && !onedrive.isBlank()) {
            candidatos.add(Paths.get(onedrive, "Documents"));
            candidatos.add(Paths.get(onedrive, "Documentos"));
        }
        candidatos.add(home.resolve("Documents"));
        candidatos.add(home.resolve("Documentos"));
        for (Path candidato : candidatos) {
            if (Files.isDirectory(candidato)) {
                return candidato;
            }
        }
        return home.resolve("Documents");
    }

    private static Path documentosDesdeRegistroWindows() {
        try {
            Process proceso = new ProcessBuilder("reg", "query",
                    "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders",
                    "/v", "Personal").redirectErrorStream(true).start();
            String salida = new String(proceso.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (!proceso.waitFor(5, TimeUnit.SECONDS)) {
                proceso.destroyForcibly();
                return null;
            }
            return analizarSalidaRegistro(salida);
        } catch (IOException | InterruptedException | RuntimeException ex) {
            return null;
        }
    }

    /**
     * Extrae la ruta de la salida de {@code reg query ... /v Personal}, por
     * ejemplo {@code Personal    REG_EXPAND_SZ    %USERPROFILE%\Documents},
     * expandiendo las variables {@code %NOMBRE%}.
     *
     * @param salida texto devuelto por reg.exe
     * @return la ruta, o {@code null} si no se encontró
     */
    static Path analizarSalidaRegistro(String salida) {
        for (String linea : salida.split("\\R")) {
            String texto = linea.trim();
            if (!texto.startsWith("Personal")) {
                continue;
            }
            int tipo = texto.indexOf("REG_");
            if (tipo < 0) {
                continue;
            }
            int espacio = texto.indexOf(' ', tipo);
            if (espacio < 0) {
                continue;
            }
            String valor = expandirVariables(texto.substring(espacio).trim());
            if (!valor.isEmpty()) {
                return Paths.get(valor);
            }
        }
        return null;
    }

    private static String expandirVariables(String texto) {
        StringBuilder resultado = new StringBuilder();
        int i = 0;
        while (i < texto.length()) {
            int inicio = texto.indexOf('%', i);
            int fin = inicio < 0 ? -1 : texto.indexOf('%', inicio + 1);
            if (inicio < 0 || fin < 0) {
                resultado.append(texto.substring(i));
                break;
            }
            resultado.append(texto, i, inicio);
            String valor = System.getenv(texto.substring(inicio + 1, fin));
            resultado.append(valor != null ? valor : texto.substring(inicio, fin + 1));
            i = fin + 1;
        }
        return resultado.toString();
    }
}
