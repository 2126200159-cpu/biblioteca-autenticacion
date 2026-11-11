package edu.utj.evnd.poo.bibliotecaproyec.dao;

import edu.utj.evnd.poo.bibliotecaproyec.enums.Permiso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Consulta los roles y los permisos que la base de datos asigna a cada rol
 * (tablas {@code rol}, {@code permiso} y {@code rol_permiso}).
 *
 * @author Joshua Odin Montoya Flores
 */
public class PermisoDao {

    /**
     * @param rol nombre del rol
     * @return permisos del rol (vacío si no existe o no tiene)
     * @throws SQLException si falla la consulta
     */
    public Set<Permiso> permisosDeRol(String rol) throws SQLException {
        Set<Permiso> permisos = EnumSet.noneOf(Permiso.class);
        String sql = "SELECT permiso FROM rol_permiso WHERE rol = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        permisos.add(Permiso.valueOf(rs.getString("permiso")));
                    } catch (IllegalArgumentException ignorado) {
                        // permiso de la base que esta versión del programa no conoce
                    }
                }
            }
        }
        return permisos;
    }

    /**
     * Validación de un permiso directamente contra la base de datos.
     *
     * @param rol     nombre del rol
     * @param permiso permiso a validar
     * @return {@code true} si el rol lo tiene asignado
     * @throws SQLException si falla la consulta
     */
    public boolean tienePermiso(String rol, Permiso permiso) throws SQLException {
        String sql = "SELECT 1 FROM rol_permiso WHERE rol = ? AND permiso = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rol);
            ps.setString(2, permiso.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * @return nombres de los roles existentes (para el combo del formulario de administradores)
     * @throws SQLException si falla la consulta
     */
    public List<String> listarRoles() throws SQLException {
        List<String> roles = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT nombre FROM rol ORDER BY nombre")) {
            while (rs.next()) {
                roles.add(rs.getString("nombre"));
            }
        }
        return roles;
    }
}
