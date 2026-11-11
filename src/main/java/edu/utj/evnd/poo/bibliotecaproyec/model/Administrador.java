package edu.utj.evnd.poo.bibliotecaproyec.model;

import edu.utj.evnd.poo.bibliotecaproyec.enums.EstadoAdministrador;

import java.util.Date;

/**
 * Persona con acceso administrativo al sistema de biblioteca. Es {@code final}
 * porque su modelo de credenciales y permisos no está pensado para extenderse.
 *
 * @author Joshua Odin Montoya Flores
 */
public final class Administrador extends Persona {
    private String usuario;
    private String contrasena;
    private EstadoAdministrador estado;
    private String rol;
    private Date fechaContratacion;

    /** Constructor vacío: cuenta ACTIVA sin credenciales ni rol. */
    public Administrador() {
        super();
        this.usuario = "";
        this.contrasena = "";
        this.estado = EstadoAdministrador.ACTIVO;
        this.rol = "";
        this.fechaContratacion = new Date();
    }

    /**
     * Constructor con los datos mínimos para un administrador válido.
     *
     * @param id         identificador primario
     * @param curp       CURP
     * @param nombre     nombre(s)
     * @param apellido   apellido(s)
     * @param usuario    nombre de usuario (no vacío)
     * @param contrasena contraseña (mínimo 4 caracteres)
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public Administrador(Integer id, String curp, String nombre, String apellido,
                         String usuario, String contrasena) {
        super(id, curp, nombre, apellido);
        this.estado = EstadoAdministrador.ACTIVO;
        this.rol = "";
        this.fechaContratacion = new Date();
        setUsuario(usuario);
        setContrasena(contrasena);
    }

    /** @return nombre de usuario */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario nuevo usuario (no nulo ni vacío)
     * @throws IllegalArgumentException si es nulo o vacío
     */
    public void setUsuario(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio.");
        }
        this.usuario = usuario.trim();
    }

    /** @return contraseña */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * @param contrasena nueva contraseña (mínimo 4 caracteres)
     * @throws IllegalArgumentException si es nula o demasiado corta
     */
    public void setContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
        }
        this.contrasena = contrasena;
    }

    /** @return estado de la cuenta */
    public EstadoAdministrador getEstado() {
        return estado;
    }

    /**
     * @param estado nuevo estado (no nulo)
     * @throws IllegalArgumentException si es nulo
     */
    public void setEstado(EstadoAdministrador estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        this.estado = estado;
    }

    /** @return rol asignado */
    public String getRol() {
        return rol;
    }

    /** @param rol nuevo rol (nulo se guarda como cadena vacía) */
    public void setRol(String rol) {
        this.rol = rol == null ? "" : rol.trim();
    }

    /** @return copia de la fecha de contratación */
    public Date getFechaContratacion() {
        return new Date(fechaContratacion.getTime());
    }

    /**
     * @param fechaContratacion fecha de contratación (no nula)
     * @throws IllegalArgumentException si es nula
     */
    public void setFechaContratacion(Date fechaContratacion) {
        if (fechaContratacion == null) {
            throw new IllegalArgumentException("La fecha de contratación es obligatoria.");
        }
        this.fechaContratacion = new Date(fechaContratacion.getTime());
    }

    /**
     * @return datos de {@link Persona} más los propios del administrador
     */
    @Override
    public String toString() {
        return super.toString() + " Administrador{usuario=" + usuario
                + ", contrasena=" + contrasena + ", estado=" + estado
                + ", rol=" + rol + ", fechaContratacion=" + fechaContratacion + "}";
    }
}
