package edu.utj.evnd.poo.bibliotecaproyec.dao;

import edu.utj.evnd.poo.bibliotecaproyec.enums.Permiso;
import edu.utj.evnd.poo.bibliotecaproyec.model.UsuarioSesion;
import edu.utj.evnd.poo.bibliotecaproyec.service.ContrasenaService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * Valida las credenciales del login contra la base de datos: busca la cuenta
 * por ID o correo, comprueba la contraseña y que la cuenta esté ACTIVA, y carga
 * los permisos del rol.
 *
 * @author Joshua Odin Montoya Flores
 */
public class AutenticacionDao {

    private static final String MENSAJE_INCORRECTO = "ID/correo o contraseña incorrectos.";
    /** Hash de relleno: así el tiempo de respuesta no revela si la cuenta existe. */
    private static final String HASH_RELLENO = ContrasenaService.hash("relleno-sin-uso");

    private final PermisoDao permisoDao = new PermisoDao();

    /**
     * @param identificador ID numérico o correo electrónico
     * @param contrasena    contraseña escrita
     * @return el usuario autenticado, con su rol y permisos
     * @throws AutenticacionException si las credenciales no son válidas o la cuenta no está activa
     * @throws SQLException           si falla la base de datos
     */
    public UsuarioSesion autenticar(String identificador, String contrasena)
            throws AutenticacionException, SQLException {
        String sql = "SELECT p.id_persona, p.nombre, p.apellido, p.correo, a.usuario, a.contrasena,"
                + " e.nombre AS estado, a.rol"
                + " FROM administrador a"
                + " JOIN persona p ON p.id_persona = a.id_persona"
                + " JOIN estado_administrador e ON e.id_estado = a.id_estado"
                + " WHERE p.correo = ? OR p.id_persona = ?";
        String id = identificador.trim();
        int idNumerico = id.matches("\\d{1,9}") ? Integer.parseInt(id) : -1;

        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setInt(2, idNumerico);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    ContrasenaService.verificar(contrasena, HASH_RELLENO);
                    throw new AutenticacionException(MENSAJE_INCORRECTO);
                }
                String guardada = rs.getString("contrasena");
                if (!ContrasenaService.verificar(contrasena, guardada)) {
                    throw new AutenticacionException(MENSAJE_INCORRECTO);
                }
                // Contraseña correcta: ahora sí se informa del estado de la cuenta
                String estado = rs.getString("estado");
                if (!"ACTIVO".equals(estado)) {
                    throw new AutenticacionException("La cuenta está " + estado
                            + " y no puede iniciar sesión. Contacta a un administrador.");
                }
                int idPersona = rs.getInt("id_persona");
                String rol = rs.getString("rol");
                Set<Permiso> permisos = permisoDao.permisosDeRol(rol);
                if (permisos.isEmpty()) {
                    throw new AutenticacionException("Tu rol (" + rol + ") no tiene permisos asignados.");
                }
                if (!ContrasenaService.esHash(guardada)) {
                    actualizarHash(con, idPersona, contrasena); // contraseña antigua en texto plano
                }
                return new UsuarioSesion(idPersona,
                        rs.getString("nombre") + " " + rs.getString("apellido"),
                        rs.getString("correo"), rs.getString("usuario"), rol, permisos);
            }
        }
    }

    private void actualizarHash(Connection con, int idPersona, String contrasena) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE administrador SET contrasena = ? WHERE id_persona = ?")) {
            ps.setString(1, ContrasenaService.hash(contrasena));
            ps.setInt(2, idPersona);
            ps.executeUpdate();
        }
    }
}
