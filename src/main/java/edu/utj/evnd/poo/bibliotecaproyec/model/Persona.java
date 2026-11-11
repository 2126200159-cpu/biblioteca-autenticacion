package edu.utj.evnd.poo.bibliotecaproyec.model;

import java.util.Date;
import java.util.Objects;

/**
 * Clase base abstracta que representa a cualquier persona que interactúa con
 * el sistema de biblioteca (administradores, lectores, etc.). No puede
 * instanciarse directamente; concentra los datos de identidad comunes.
 * La identidad de un objeto se define únicamente por su {@code id}.
 *
 * @author Joshua Odin Montoya Flores
 */
public abstract class Persona implements Imagenable {
    private Integer id;
    private String curp;
    private String rfc;
    private String nombre;
    private String apellido;
    private Date nacimiento;
    private String correo;
    private String telefono;
    private String urlImagen;

    /**
     * Constructor vacío: deja la persona con valores por defecto
     * (compatibilidad con frameworks e instanciación por defecto).
     */
    public Persona() {
        this.id = 0;
        this.curp = "";
        this.rfc = "";
        this.nombre = "";
        this.apellido = "";
        this.nacimiento = new Date();
        this.correo = "";
        this.telefono = "";
        this.urlImagen = "";
    }

    /**
     * Constructor con los datos mínimos indispensables para un estado válido.
     *
     * @param id       identificador primario (entero positivo)
     * @param curp     CURP de la persona (no vacía)
     * @param nombre   nombre(s) (no vacío)
     * @param apellido apellido(s) (no vacío)
     * @throws IllegalArgumentException si algún dato no cumple la validación
     */
    public Persona(Integer id, String curp, String nombre, String apellido) {
        this();
        setId(id);
        setCurp(curp);
        setNombre(nombre);
        setApellido(apellido);
    }

    /** @return identificador primario */
    public Integer getId() {
        return id;
    }

    /**
     * @param id nuevo identificador; debe ser un entero positivo
     * @throws IllegalArgumentException si es nulo o menor o igual a cero
     */
    public void setId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id debe ser un entero positivo.");
        }
        this.id = id;
    }

    /** @return CURP */
    public String getCurp() {
        return curp;
    }

    /**
     * @param curp nueva CURP (no nula ni vacía)
     * @throws IllegalArgumentException si es nula o vacía
     */
    public void setCurp(String curp) {
        this.curp = requerirTexto(curp, "La CURP");
    }

    /** @return RFC */
    public String getRfc() {
        return rfc;
    }

    /** @param rfc nuevo RFC (un valor nulo se guarda como cadena vacía) */
    public void setRfc(String rfc) {
        this.rfc = rfc == null ? "" : rfc.trim();
    }

    /** @return nombre(s) */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre nuevo nombre (no nulo ni vacío)
     * @throws IllegalArgumentException si es nulo o vacío
     */
    public void setNombre(String nombre) {
        this.nombre = requerirTexto(nombre, "El nombre");
    }

    /** @return apellido(s) */
    public String getApellido() {
        return apellido;
    }

    /**
     * @param apellido nuevo apellido (no nulo ni vacío)
     * @throws IllegalArgumentException si es nulo o vacío
     */
    public void setApellido(String apellido) {
        this.apellido = requerirTexto(apellido, "El apellido");
    }

    /** @return copia de la fecha de nacimiento */
    public Date getNacimiento() {
        return new Date(nacimiento.getTime());
    }

    /**
     * @param nacimiento fecha de nacimiento (no nula ni futura)
     * @throws IllegalArgumentException si es nula o posterior a hoy
     */
    public void setNacimiento(Date nacimiento) {
        if (nacimiento == null || nacimiento.after(new Date())) {
            throw new IllegalArgumentException("La fecha de nacimiento no es válida.");
        }
        this.nacimiento = new Date(nacimiento.getTime());
    }

    /** @return correo electrónico */
    public String getCorreo() {
        return correo;
    }

    /**
     * @param correo correo electrónico; vacío o con formato básico
     *               {@code algo@dominio}
     * @throws IllegalArgumentException si tiene texto y no contiene '@'
     */
    public void setCorreo(String correo) {
        String valor = correo == null ? "" : correo.trim();
        if (!valor.isEmpty() && !valor.matches("^[^@\\s]+@[^@\\s]+$")) {
            throw new IllegalArgumentException("El correo no tiene un formato válido.");
        }
        this.correo = valor;
    }

    /** @return teléfono */
    public String getTelefono() {
        return telefono;
    }

    /** @param telefono nuevo teléfono (nulo se guarda como cadena vacía) */
    public void setTelefono(String telefono) {
        this.telefono = telefono == null ? "" : telefono.trim();
    }

    /** @return ruta o URL de la imagen de perfil */
    public String getUrlImagen() {
        return urlImagen;
    }

    /** @param urlImagen nueva ruta de imagen (nulo se guarda vacío) */
    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen == null ? "" : urlImagen.trim();
    }

    /**
     * Tipo de persona, usado para mostrarlo en las tablas.
     *
     * @return nombre simple de la clase concreta (Administrador o Lector)
     */
    public String getTipo() {
        return getClass().getSimpleName();
    }

    /**
     * Devuelve el nombre completo. Es {@code final} porque la regla de
     * composición no debe alterarse en las subclases.
     *
     * @return nombre y apellido separados por un espacio
     */
    public final String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /**
     * Valida que un texto obligatorio no sea nulo ni vacío.
     *
     * @param valor  texto a validar
     * @param campo  nombre del campo para el mensaje de error
     * @return el texto sin espacios laterales
     */
    private static String requerirTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        return valor.trim();
    }

    /**
     * Calcula el código hash únicamente a partir del {@code id}.
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Dos personas son iguales si son de la misma clase y tienen el mismo id.
     *
     * @param obj objeto a comparar
     * @return {@code true} si son iguales
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(this.id, ((Persona) obj).id);
    }

    /**
     * @return representación con todos los atributos de la persona
     */
    @Override
    public String toString() {
        return "Persona{id=" + id + ", curp=" + curp + ", rfc=" + rfc
                + ", nombre=" + nombre + ", apellido=" + apellido
                + ", nacimiento=" + nacimiento + ", correo=" + correo
                + ", telefono=" + telefono + ", urlImagen=" + urlImagen + "}";
    }
}
