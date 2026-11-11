package edu.utj.evnd.poo.bibliotecaproyec.dao;

import edu.utj.evnd.poo.bibliotecaproyec.model.Libro;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla {@code libro} (con los nombres de su editorial y categoría).
 *
 * @author Joshua Odin Montoya Flores
 */
public class LibroDao implements Dao<Libro> {

    private static final String SELECT = "SELECT l.id_libro, l.titulo, l.autor, l.isbn, l.id_editorial,"
            + " l.id_categoria, l.precio, l.ejemplares_disponibles, l.ejemplares_en_pedido, l.nivel_reorden,"
            + " l.descontinuado, l.url_imagen, e.nombre AS editorial, c.nombre AS categoria"
            + " FROM libro l"
            + " JOIN editorial e ON e.id_editorial = l.id_editorial"
            + " JOIN categoria c ON c.id_categoria = l.id_categoria";

    @Override
    public List<Libro> listar() throws SQLException {
        return consultar(SELECT + " ORDER BY l.id_libro");
    }

    @Override
    public List<Libro> buscar(String texto) throws SQLException {
        String like = "%" + texto.trim() + "%";
        return consultar(SELECT + " WHERE CAST(l.id_libro AS CHAR) LIKE ? OR l.titulo LIKE ? OR l.autor LIKE ?"
                + " OR l.isbn LIKE ? OR e.nombre LIKE ? OR c.nombre LIKE ? ORDER BY l.id_libro",
                like, like, like, like, like, like);
    }

    @Override
    public void insertar(Libro l) throws SQLException {
        String sql = "INSERT INTO libro (titulo, autor, isbn, id_editorial, id_categoria, precio,"
                + " ejemplares_disponibles, ejemplares_en_pedido, nivel_reorden, descontinuado, url_imagen)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            asignar(ps, l);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Libro l) throws SQLException {
        String sql = "UPDATE libro SET titulo = ?, autor = ?, isbn = ?, id_editorial = ?, id_categoria = ?,"
                + " precio = ?, ejemplares_disponibles = ?, ejemplares_en_pedido = ?, nivel_reorden = ?,"
                + " descontinuado = ?, url_imagen = ? WHERE id_libro = ?";
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            asignar(ps, l);
            ps.setInt(12, l.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(Libro l) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("DELETE FROM libro WHERE id_libro = ?")) {
            ps.setInt(1, l.getId());
            ps.executeUpdate();
        }
    }

    private void asignar(PreparedStatement ps, Libro l) throws SQLException {
        ps.setString(1, l.getTitulo());
        ps.setString(2, l.getAutor());
        ps.setString(3, ConexionBD.aNulo(l.getIsbn()));
        ps.setInt(4, l.getIdEditorial());
        ps.setInt(5, l.getIdCategoria());
        ps.setBigDecimal(6, BigDecimal.valueOf(l.getPrecio()));
        ps.setInt(7, l.getEjemplaresDisponibles());
        ps.setInt(8, l.getEjemplaresEnPedido());
        ps.setInt(9, l.getNivelReorden());
        ps.setBoolean(10, l.isDescontinuado());
        ps.setString(11, ConexionBD.aNulo(l.getUrlImagen()));
    }

    private List<Libro> consultar(String sql, String... parametros) throws SQLException {
        List<Libro> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setString(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Libro l = new Libro(rs.getString("titulo"), rs.getString("autor"),
                            rs.getInt("id_editorial"), rs.getInt("id_categoria"));
                    l.setId(rs.getInt("id_libro"));
                    l.setIsbn(rs.getString("isbn"));
                    l.setPrecio(rs.getBigDecimal("precio").doubleValue());
                    l.setEjemplaresDisponibles(rs.getInt("ejemplares_disponibles"));
                    l.setEjemplaresEnPedido(rs.getInt("ejemplares_en_pedido"));
                    l.setNivelReorden(rs.getInt("nivel_reorden"));
                    l.setDescontinuado(rs.getBoolean("descontinuado"));
                    l.setUrlImagen(rs.getString("url_imagen"));
                    l.setNombreEditorial(rs.getString("editorial"));
                    l.setNombreCategoria(rs.getString("categoria"));
                    lista.add(l);
                }
            }
        } catch (IllegalArgumentException ex) {
            throw new SQLException("Dato inválido en la base de datos: " + ex.getMessage(), ex);
        }
        return lista;
    }
}
