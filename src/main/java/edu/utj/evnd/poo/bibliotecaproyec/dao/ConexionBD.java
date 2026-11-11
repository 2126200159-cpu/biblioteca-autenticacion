package edu.utj.evnd.poo.bibliotecaproyec.dao;

import edu.utj.evnd.poo.bibliotecaproyec.service.AlmacenamientoService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Obtiene conexiones a la base de datos {@code biblioteca_db} (MariaDB/MySQL
 * de XAMPP). Los valores por defecto son los de XAMPP (localhost, root, sin
 * contraseña). Si tu servidor es otro, edita el archivo
 * {@code Documentos/SistemaBiblioteca/configuracion.properties}, que se crea
 * solo la primera vez; no hace falta recompilar.
 *
 * @author Joshua Odin Montoya Flores
 */
public final class ConexionBD {
    private static final String URL_DEFECTO = "jdbc:mariadb://localhost:3306/biblioteca_db";
    private static final String USUARIO_DEFECTO = "root";
    private static final String CLAVE_DEFECTO = "";
    private static final String ARCHIVO_CONFIG = "configuracion.properties";

    private static Properties config;

    private ConexionBD() {
    }

    /**
     * @return una conexión nueva (debe cerrarse con try-with-resources)
     * @throws SQLException si no se puede conectar
     */
    public static Connection obtener() throws SQLException {
        Properties p = configuracion();
        return DriverManager.getConnection(
                p.getProperty("db.url", URL_DEFECTO),
                p.getProperty("db.usuario", USUARIO_DEFECTO),
                p.getProperty("db.clave", CLAVE_DEFECTO));
    }

    /** Lee la configuración externa una sola vez; si no existe, la crea con los valores por defecto. */
    private static synchronized Properties configuracion() {
        if (config != null) {
            return config;
        }
        Properties p = new Properties();
        p.setProperty("db.url", URL_DEFECTO);
        p.setProperty("db.usuario", USUARIO_DEFECTO);
        p.setProperty("db.clave", CLAVE_DEFECTO);
        try {
            Path archivo = AlmacenamientoService.getCarpetaApp().resolve(ARCHIVO_CONFIG);
            if (Files.isRegularFile(archivo)) {
                try (InputStream in = Files.newInputStream(archivo)) {
                    p.load(in);
                }
            } else {
                try (OutputStream out = Files.newOutputStream(archivo)) {
                    p.store(out, "Conexion a la base de datos del Sistema de Biblioteca");
                }
            }
        } catch (IOException | RuntimeException ex) {
            System.err.println("No se pudo leer/crear " + ARCHIVO_CONFIG + ", se usan los valores por defecto: " + ex);
        }
        config = p;
        return config;
    }

    /**
     * Convierte un texto vacío en {@code null} para guardarlo como NULL.
     *
     * @param texto texto a convertir
     * @return {@code null} si es nulo o vacío; si no, el texto sin espacios
     */
    public static String aNulo(String texto) {
        return texto == null || texto.trim().isEmpty() ? null : texto.trim();
    }
}
