package edu.utj.evnd.poo.bibliotecaproyec.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Obtiene conexiones a la base de datos {@code biblioteca_db} (MariaDB/MySQL
 * de XAMPP). Cambia los valores si tu servidor usa otro usuario o contraseña.
 *
 * @author Joshua Odin Montoya Flores
 */
public final class ConexionBD {
    private static final String URL = "jdbc:mariadb://localhost:3306/biblioteca_db";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    private ConexionBD() {
    }

    /**
     * @return una conexión nueva (debe cerrarse con try-with-resources)
     * @throws SQLException si no se puede conectar
     */
    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
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
