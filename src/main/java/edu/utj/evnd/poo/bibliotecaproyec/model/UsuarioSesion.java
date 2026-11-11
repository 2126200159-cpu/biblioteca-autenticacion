package edu.utj.evnd.poo.bibliotecaproyec.model;

import edu.utj.evnd.poo.bibliotecaproyec.enums.Permiso;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Datos del usuario que inició sesión: quién es, qué rol tiene y qué permisos
 * le corresponden. No guarda la contraseña.
 *
 * @author Joshua Odin Montoya Flores
 */
public final class UsuarioSesion {
    private final int idPersona;
    private final String nombreCompleto;
    private final String correo;
    private final String usuario;
    private final String rol;
    private final Set<Permiso> permisos;
    private int idSesion;

    public UsuarioSesion(int idPersona, String nombreCompleto, String correo,
                         String usuario, String rol, Set<Permiso> permisos) {
        this.idPersona = idPersona;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.usuario = usuario;
        this.rol = rol;
        this.permisos = permisos.isEmpty() ? EnumSet.noneOf(Permiso.class) : EnumSet.copyOf(permisos);
    }

    public int getIdPersona() {
        return idPersona;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getRol() {
        return rol;
    }

    /** @return permisos del rol (solo lectura) */
    public Set<Permiso> getPermisos() {
        return Collections.unmodifiableSet(permisos);
    }

    /** @return id del registro en la bitácora {@code sesion_log} */
    public int getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(int idSesion) {
        this.idSesion = idSesion;
    }

    /**
     * @param permiso permiso a comprobar
     * @return {@code true} si el rol del usuario lo tiene
     */
    public boolean tiene(Permiso permiso) {
        return permisos.contains(permiso);
    }
}
