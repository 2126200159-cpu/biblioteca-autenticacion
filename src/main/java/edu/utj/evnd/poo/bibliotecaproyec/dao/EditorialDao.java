package edu.utj.evnd.poo.bibliotecaproyec.dao;

import edu.utj.evnd.poo.bibliotecaproyec.model.Editorial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla {@code editorial}.
 *
 * @author Joshua Odin Montoya Flores
 */
public class EditorialDao implements Dao<Editorial> {

    private static final String SELECT = "SELECT id_editorial, nombre, nombre_contacto, cargo_contacto,"
            + " direccion, ciudad, region, codigo_postal, pais, telefono, sitio_web, url_imagen FROM editorial";

    @Override
    public List<Editorial> listar() throws SQLException {
        return consultar(SELECT + " ORDER BY id_editorial");
    }

    @Override
    public List<Editorial> buscar(String texto) throws SQLException {
        String like = "%" + texto.trim() + "%";
        return consultar(SELECT + " WHERE CAST(id_editorial AS CHAR) LIKE ? OR nombre LIKE ?"
                + " OR nombre_contacto LIKE ? OR ciudad LIKE ? OR pais LIKE ? ORDER BY id_editorial",
                like, like, like, like, like);
    }

    @Override
    public void insertar(Editorial e) throws SQLException {
        String sql = "INSERT INTO editorial (nombre, nombre_contacto, cargo_contacto, direccion, ciudad,"
                + " region, codigo_postal, pais, telefono, sitio_web, url_imagen)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            asignar(ps, e);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Editorial e) throws SQLException {
        String sql = "UPDATE editorial SET nombre = ?, nombre_contacto = ?, cargo_contacto = ?, direccion = ?,"
                + " ciudad = ?, region = ?, codigo_postal = ?, pais = ?, telefono = ?, sitio_web = ?,"
                + " url_imagen = ? WHERE id_editorial = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            asignar(ps, e);
            ps.setInt(12, e.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Editorial e) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("DELETE FROM editorial WHERE id_editorial = ?")) {
            ps.setInt(1, e.getId());
            ps.executeUpdate();
        }
    }

    private void asignar(PreparedStatement ps, Editorial e) throws SQLException {
        ps.setString(1, e.getNombre());
        ps.setString(2, ConexionBD.aNulo(e.getNombreContacto()));
        ps.setString(3, ConexionBD.aNulo(e.getCargoContacto()));
        ps.setString(4, ConexionBD.aNulo(e.getDireccion()));
        ps.setString(5, ConexionBD.aNulo(e.getCiudad()));
        ps.setString(6, ConexionBD.aNulo(e.getRegion()));
        ps.setString(7, ConexionBD.aNulo(e.getCodigoPostal()));
        ps.setString(8, ConexionBD.aNulo(e.getPais()));
        ps.setString(9, ConexionBD.aNulo(e.getTelefono()));
        ps.setString(10, ConexionBD.aNulo(e.getSitioWeb()));
        ps.setString(11, ConexionBD.aNulo(e.getUrlImagen()));
    }

    private List<Editorial> consultar(String sql, String... parametros) throws SQLException {
        List<Editorial> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setString(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Editorial e = new Editorial(rs.getString("nombre"));
                    e.setId(rs.getInt("id_editorial"));
                    e.setNombreContacto(rs.getString("nombre_contacto"));
                    e.setCargoContacto(rs.getString("cargo_contacto"));
                    e.setDireccion(rs.getString("direccion"));
                    e.setCiudad(rs.getString("ciudad"));
                    e.setRegion(rs.getString("region"));
                    e.setCodigoPostal(rs.getString("codigo_postal"));
                    e.setPais(rs.getString("pais"));
                    e.setTelefono(rs.getString("telefono"));
                    e.setSitioWeb(rs.getString("sitio_web"));
                    e.setUrlImagen(rs.getString("url_imagen"));
                    lista.add(e);
                }
            }
        } catch (IllegalArgumentException ex) {
            throw new SQLException("Dato inválido en la base de datos: " + ex.getMessage(), ex);
        }
        return lista;
    }
}
