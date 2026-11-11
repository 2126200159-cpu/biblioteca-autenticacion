package edu.utj.evnd.poo.bibliotecaproyec.service;

import edu.utj.evnd.poo.bibliotecaproyec.enums.Permiso;
import edu.utj.evnd.poo.bibliotecaproyec.model.UsuarioSesion;

/**
 * Usuario que tiene la sesión iniciada en este momento. Los controladores la
 * consultan para habilitar o restringir módulos, botones y acciones.
 *
 * @author Joshua Odin Montoya Flores
 */
public final class SesionActual {
    private static UsuarioSesion usuario;

    private SesionActual() {
    }

    public static void iniciar(UsuarioSesion nuevo) {
        usuario = nuevo;
    }

    /** @return el usuario con sesión, o {@code null} si no hay */
    public static UsuarioSesion getUsuario() {
        return usuario;
    }

    public static boolean haySesion() {
        return usuario != null;
    }

    /**
     * @param permiso permiso a comprobar
     * @return {@code true} si hay sesión y el rol tiene el permiso (sin sesión, siempre {@code false})
     */
    public static boolean tiene(Permiso permiso) {
        return usuario != null && usuario.tiene(permiso);
    }

    /** Olvida al usuario actual. */
    public static void cerrar() {
        usuario = null;
    }
}
