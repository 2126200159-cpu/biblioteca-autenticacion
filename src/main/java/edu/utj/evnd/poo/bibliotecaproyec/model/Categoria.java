package edu.utj.evnd.poo.bibliotecaproyec.model;

import java.util.Objects;

/**
 * Categoría con la que se clasifican los libros de la biblioteca.
 *
 * @author Joshua Odin Montoya Flores
 */
public class Categoria implements Imagenable {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String urlImagen;

    /** Constructor vacío con valores por defecto. */
    public Categoria() {
        this.id = 0;
        this.nombre = "";
        this.descripcion = "";
        this.urlImagen = "";
    }

    /**
     * Constructor con los datos mínimos.
     *
     * @param nombre nombre de la categoría (no vacío)
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    public Categoria(String nombre) {
        this();
        setNombre(nombre);
    }

    @Override
    public Integer getId() {
        return id;
    }

    /**
     * @param id identificador (no nulo ni negativo)
     * @throws IllegalArgumentException si es nulo o negativo
     */
    public void setId(Integer id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("El id no es válido.");
        }
        this.id = id;
    }

    /** @return nombre */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre nuevo nombre (no nulo ni vacío)
     * @throws IllegalArgumentException si es nulo o vacío
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    /** @return descripción */
    public String getDescripcion() {
        return descripcion;
    }

    /** @param descripcion nueva descripción (nulo se guarda vacío) */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? "" : descripcion.trim();
    }

    @Override
    public String getUrlImagen() {
        return urlImagen;
    }

    @Override
    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen == null ? "" : urlImagen.trim();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(id, ((Categoria) obj).id);
    }

    @Override
    public String toString() {
        return "Categoria{id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion
                + ", urlImagen=" + urlImagen + "}";
    }
}
