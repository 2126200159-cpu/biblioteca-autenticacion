package edu.utj.evnd.poo.bibliotecaproyec.service;

import edu.utj.evnd.poo.bibliotecaproyec.dao.SesionDao;
import edu.utj.evnd.poo.bibliotecaproyec.model.UsuarioSesion;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Sesión activa de la aplicación: quién inició sesión y qué permisos tiene su
 * rol (cargados desde la base de datos al iniciar sesión). Las vistas consultan
 * esta clase para habilitar o restringir opciones, botones y acciones.
 *
 * @author Joshua Odin Montoya Flores
 */
public final class Sesion {
    private static UsuarioSesion usuario;
    private static Set<String> permisos = Collections.emptySet();
    private static int idSesion;

    private Sesion() {
    }

    /**
     * Inicia la sesión en memoria.
     *
     * @param usuarioNuevo usuario autenticado
     * @param permisosRol  códigos de permiso de su rol
     * @param idRegistro   id del registro de la sesión en la base de datos
     */
    public static void iniciar(UsuarioSesion usuarioNuevo, Set<String> permisosRol, int idRegistro) {
        usuario = usuarioNuevo;
        permisos = new HashSet<>(permisosRol);
        idSesion = idRegistro;
    }

    /** @return {@code true} si hay una sesión iniciada */
    public static boolean activa() {
        return usuario != null;
    }

    /** @return el usuario de la sesión, o {@code null} si no hay sesión */
    public static UsuarioSesion getUsuario() {
        return usuario;
    }

    /**
     * Valida un permiso del rol del usuario.
     *
     * @param codigo código del permiso (p. ej. LIBROS_CREAR)
     * @return {@code true} si hay sesión y el rol tiene el permiso
     */
    public static boolean tiene(String codigo) {
        return usuario != null && permisos.contains(codigo);
    }

    /**
     * Cierra la sesión: registra la hora de cierre en la base de datos y borra
     * los datos de la memoria. Si no hay sesión, no hace nada.
     */
    public static void cerrar() {
        if (usuario == null) {
            return;
        }
        try {
            new SesionDao().registrarCierre(idSesion);
        } catch (SQLException ex) {
            // Si no se pudo registrar el cierre, la sesión se cierra igualmente en la aplicación.
        }
        usuario = null;
        permisos = Collections.emptySet();
        idSesion = 0;
    }
}
