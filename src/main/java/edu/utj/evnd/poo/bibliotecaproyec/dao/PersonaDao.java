package edu.utj.evnd.poo.bibliotecaproyec.dao;

import edu.utj.evnd.poo.bibliotecaproyec.enums.EstadoAdministrador;
import edu.utj.evnd.poo.bibliotecaproyec.model.Administrador;
import edu.utj.evnd.poo.bibliotecaproyec.model.Lector;
import edu.utj.evnd.poo.bibliotecaproyec.model.Persona;
import edu.utj.evnd.poo.bibliotecaproyec.service.ContrasenaService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Acceso a las tablas {@code persona}, {@code administrador} y {@code lector}
 * como una sola unidad (herencia "una tabla por subclase"). Las escrituras se
 * hacen en una transacción para que persona y su rol se guarden juntos o no se
 * guarden.
 *
 * @author Joshua Odin Montoya Flores
 */
public class PersonaDao implements Dao<Persona> {

    private static final String SELECT = "SELECT p.id_persona, p.curp, p.rfc, p.nombre, p.apellido,"
            + " p.nacimiento, p.correo, p.telefono, p.url_imagen,"
            + " a.usuario, a.contrasena, e.nombre AS estado, a.rol, a.fecha_contratacion,"
            + " l.numero_credencial, l.multa_pendiente"
            + " FROM persona p"
            + " LEFT JOIN administrador a ON a.id_persona = p.id_persona"
            + " LEFT JOIN estado_administrador e ON e.id_estado = a.id_estado"
            + " LEFT JOIN lector l ON l.id_persona = p.id_persona";

    @Override
    public List<Persona> listar() throws SQLException {
        return consultar(SELECT + " ORDER BY p.id_persona");
    }

    @Override
    public List<Persona> buscar(String texto) throws SQLException {
        String like = "%" + texto.trim() + "%";
        return consultar(SELECT + " WHERE CAST(p.id_persona AS CHAR) LIKE ? OR p.curp LIKE ? OR p.nombre LIKE ?"
                + " OR p.apellido LIKE ? OR p.correo LIKE ? OR a.usuario LIKE ? OR l.numero_credencial LIKE ?"
                + " ORDER BY p.id_persona",
                like, like, like, like, like, like, like);
    }

    @Override
    public void insertar(Persona p) throws SQLException {
        try (Connection con = ConexionBD.obtener()) {
            con.setAutoCommit(false);
            try {
                String sql = "INSERT INTO persona (curp, rfc, nombre, apellido, nacimiento, correo, telefono,"
                        + " url_imagen, id_persona) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    asignarPersona(ps, p);
                    ps.executeUpdate();
                }
                if (p instanceof Administrador) {
                    Administrador a = (Administrador) p;
                    String sqlAdmin = "INSERT INTO administrador (usuario, contrasena, id_estado, rol,"
                            + " fecha_contratacion, id_persona) VALUES (?, ?,"
                            + " (SELECT id_estado FROM estado_administrador WHERE nombre = ?), ?, ?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(sqlAdmin)) {
                        asignarAdministrador(ps, a);
                        ps.executeUpdate();
                    }
                } else if (p instanceof Lector) {
                    Lector l = (Lector) p;
                    String sqlLector = "INSERT INTO lector (numero_credencial, multa_pendiente, id_persona)"
                            + " VALUES (?, ?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(sqlLector)) {
                        asignarLector(ps, l);
                        ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    @Override
    public void actualizar(Persona p) throws SQLException {
        try (Connection con = ConexionBD.obtener()) {
            con.setAutoCommit(false);
            try {
                String sql = "UPDATE persona SET curp = ?, rfc = ?, nombre = ?, apellido = ?, nacimiento = ?,"
                        + " correo = ?, telefono = ?, url_imagen = ? WHERE id_persona = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    asignarPersona(ps, p);
                    ps.executeUpdate();
                }
                if (p instanceof Administrador) {
                    Administrador a = (Administrador) p;
                    String sqlAdmin = "UPDATE administrador SET usuario = ?, contrasena = ?,"
                            + " id_estado = (SELECT id_estado FROM estado_administrador WHERE nombre = ?),"
                            + " rol = ?, fecha_contratacion = ? WHERE id_persona = ?";
                    try (PreparedStatement ps = con.prepareStatement(sqlAdmin)) {
                        asignarAdministrador(ps, a);
                        ps.executeUpdate();
                    }
                } else if (p instanceof Lector) {
                    Lector l = (Lector) p;
                    String sqlLector = "UPDATE lector SET numero_credencial = ?, multa_pendiente = ?"
                            + " WHERE id_persona = ?";
                    try (PreparedStatement ps = con.prepareStatement(sqlLector)) {
                        asignarLector(ps, l);
                        ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    /** Borra la persona; su registro de administrador o lector se borra en cascada. */
    @Override
    public void eliminar(Persona p) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("DELETE FROM persona WHERE id_persona = ?")) {
            ps.setInt(1, p.getId());
            ps.executeUpdate();
        }
    }

    // Los parámetros de persona siguen este orden en INSERT y UPDATE:
    // curp, rfc, nombre, apellido, nacimiento, correo, telefono, url_imagen, id_persona
    private void asignarPersona(PreparedStatement ps, Persona p) throws SQLException {
        ps.setString(1, p.getCurp());
        ps.setString(2, p.getRfc());
        ps.setString(3, p.getNombre());
        ps.setString(4, p.getApellido());
        ps.setDate(5, new java.sql.Date(p.getNacimiento().getTime()));
        ps.setString(6, p.getCorreo());
        ps.setString(7, p.getTelefono());
        ps.setString(8, ConexionBD.aNulo(p.getUrlImagen()));
        ps.setInt(9, p.getId());
    }

    // usuario, contrasena, estado (nombre), rol, fecha_contratacion, id_persona
    private void asignarAdministrador(PreparedStatement ps, Administrador a) throws SQLException {
        ps.setString(1, a.getUsuario());
        ps.setString(2, ContrasenaService.protegerSiNecesario(a.getContrasena())); // nunca se guarda en texto plano
        ps.setString(3, a.getEstado().name());
        ps.setString(4, a.getRol());
        ps.setDate(5, new java.sql.Date(a.getFechaContratacion().getTime()));
        ps.setInt(6, a.getId());
    }

    // numero_credencial, multa_pendiente, id_persona
    private void asignarLector(PreparedStatement ps, Lector l) throws SQLException {
        ps.setString(1, l.getNumeroCredencial());
        ps.setBigDecimal(2, BigDecimal.valueOf(l.getMultaPendiente()));
        ps.setInt(3, l.getId());
    }

    private List<Persona> consultar(String sql, String... parametros) throws SQLException {
        List<Persona> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setString(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Persona p = mapear(rs);
                    if (p != null) {
                        lista.add(p);
                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            throw new SQLException("Dato inválido en la base de datos: " + ex.getMessage(), ex);
        }
        return lista;
    }

    private Persona mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_persona");
        String curp = rs.getString("curp");
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        String usuario = rs.getString("usuario");
        String credencial = rs.getString("numero_credencial");

        Persona persona;
        if (usuario != null) {
            Administrador a = new Administrador(id, curp, nombre, apellido, usuario, rs.getString("contrasena"));
            a.setEstado(EstadoAdministrador.valueOf(rs.getString("estado")));
            a.setRol(rs.getString("rol"));
            java.sql.Date contratacion = rs.getDate("fecha_contratacion");
            if (contratacion != null) {
                a.setFechaContratacion(new Date(contratacion.getTime()));
            }
            persona = a;
        } else if (credencial != null) {
            Lector l = new Lector(id, curp, nombre, apellido, credencial);
            l.setMultaPendiente(rs.getBigDecimal("multa_pendiente").doubleValue());
            persona = l;
        } else {
            return null; // persona sin rol: no se muestra
        }
        persona.setRfc(rs.getString("rfc"));
        persona.setNacimiento(new Date(rs.getDate("nacimiento").getTime()));
        persona.setCorreo(rs.getString("correo"));
        persona.setTelefono(rs.getString("telefono"));
        persona.setUrlImagen(rs.getString("url_imagen"));
        return persona;
    }
}
