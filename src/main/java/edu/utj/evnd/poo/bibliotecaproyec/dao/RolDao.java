package edu.utj.evnd.poo.bibliotecaproyec.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Consulta los roles disponibles (tabla {@code rol}).
 *
 * @author Joshua Odin Montoya Flores
 */
public class RolDao {

    /**
     * @return nombres de los roles (ADMINISTRADOR, BIBLIOTECARIO, CONSULTA)
     * @throws SQLException si falla la consulta
     */
    public List<String> listarNombres() throws SQLException {
        List<String> nombres = new ArrayList<>();
        try (Connection con = ConexionBD.obtener(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT nombre FROM rol ORDER BY id_rol")) {
            while (rs.next()) {
                nombres.add(rs.getString("nombre"));
            }
        }
        return nombres;
    }
}
