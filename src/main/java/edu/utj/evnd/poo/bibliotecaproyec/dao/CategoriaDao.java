package edu.utj.evnd.poo.bibliotecaproyec.dao;

import edu.utj.evnd.poo.bibliotecaproyec.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla {@code categoria}.
 *
 * @author Joshua Odin Montoya Flores
 */
public class CategoriaDao implements Dao<Categoria> {

    private static final String SELECT =
            "SELECT id_categoria, nombre, descripcion, url_imagen FROM categoria";

    @Override
    public List<Categoria> listar() throws SQLException {
        return consultar(SELECT + " ORDER BY id_categoria");
    }

    @Override
    public List<Categoria> buscar(String texto) throws SQLException {
        String like = "%" + texto.trim() + "%";
        return consultar(SELECT + " WHERE CAST(id_categoria AS CHAR) LIKE ? OR nombre LIKE ?"
                + " OR descripcion LIKE ? ORDER BY id_categoria", like, like, like);
    }

    @Override
    public void insertar(Categoria c) throws SQLException {
        String sql = "INSERT INTO categoria (nombre, descripcion, url_imagen) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, ConexionBD.aNulo(c.getDescripcion()));
            ps.setString(3, ConexionBD.aNulo(c.getUrlImagen()));
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Categoria c) throws SQLException {
        String sql = "UPDATE categoria SET nombre = ?, descripcion = ?, url_imagen = ? WHERE id_categoria = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, ConexionBD.aNulo(c.getDescripcion()));
            ps.setString(3, ConexionBD.aNulo(c.getUrlImagen()));
            ps.setInt(4, c.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Categoria c) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("DELETE FROM categoria WHERE id_categoria = ?")) {
            ps.setInt(1, c.getId());
            ps.executeUpdate();
        }
    }

    private List<Categoria> consultar(String sql, String... parametros) throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setString(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categoria c = new Categoria(rs.getString("nombre"));
                    c.setId(rs.getInt("id_categoria"));
                    c.setDescripcion(rs.getString("descripcion"));
                    c.setUrlImagen(rs.getString("url_imagen"));
                    lista.add(c);
                }
            }
        } catch (IllegalArgumentException ex) {
            throw new SQLException("Dato inválido en la base de datos: " + ex.getMessage(), ex);
        }
        return lista;
    }
}
