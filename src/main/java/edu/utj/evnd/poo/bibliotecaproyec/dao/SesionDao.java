package edu.utj.evnd.poo.bibliotecaproyec.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Registra en la bitácora {@code sesion_log} cuándo un usuario inicia y cierra
 * sesión.
 *
 * @author Joshua Odin Montoya Flores
 */
public class SesionDao {

    /**
     * @param idPersona id del usuario que acaba de autenticarse
     * @return id del registro de sesión creado
     * @throws SQLException si falla la inserción
     */
    public int registrarInicio(int idPersona) throws SQLException {
        String sql = "INSERT INTO sesion_log (id_persona) VALUES (?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idPersona);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Marca la hora de cierre de una sesión abierta.
     *
     * @param idSesion id devuelto por {@link #registrarInicio(int)}
     * @throws SQLException si falla la actualización
     */
    public void registrarCierre(int idSesion) throws SQLException {
        String sql = "UPDATE sesion_log SET fin = CURRENT_TIMESTAMP WHERE id_sesion = ? AND fin IS NULL";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSesion);
            ps.executeUpdate();
        }
    }
}
