package edu.utj.evnd.poo.bibliotecaproyec.model;

import java.util.Objects;

/**
 * Libro del acervo de la biblioteca. Pertenece a una editorial y a una
 * categoría. Los nombres de editorial y categoría solo se usan para mostrarlos
 * en las tablas (los llena la consulta a la base de datos).
 *
 * @author Joshua Odin Montoya Flores
 */
public class Libro implements Imagenable {
    private Integer id;
    private String titulo;
    private String autor;
    private String isbn;
    private Integer idEditorial;
    private Integer idCategoria;
    private double precio;
    private int ejemplaresDisponibles;
    private int ejemplaresEnPedido;
    private int nivelReorden;
    private boolean descontinuado;
    private String urlImagen;
    private String nombreEditorial;
    private String nombreCategoria;

    /** Constructor vacío con valores por defecto. */
    public Libro() {
        this.id = 0;
        this.titulo = "";
        this.autor = "";
        this.isbn = "";
        this.idEditorial = 0;
        this.idCategoria = 0;
        this.precio = 0.0;
        this.ejemplaresDisponibles = 0;
        this.ejemplaresEnPedido = 0;
        this.nivelReorden = 0;
        this.descontinuado = false;
        this.urlImagen = "";
        this.nombreEditorial = "";
        this.nombreCategoria = "";
    }

    /**
     * Constructor con los datos mínimos.
     *
     * @param titulo      título (no vacío)
     * @param autor       autor (no vacío)
     * @param idEditorial id de la editorial (mayor que 0)
     * @param idCategoria id de la categoría (mayor que 0)
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public Libro(String titulo, String autor, Integer idEditorial, Integer idCategoria) {
        this();
        setTitulo(titulo);
        setAutor(autor);
        setIdEditorial(idEditorial);
        setIdCategoria(idCategoria);
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

    /** @return título */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo nuevo título (no nulo ni vacío)
     * @throws IllegalArgumentException si es nulo o vacío
     */
    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }
        this.titulo = titulo.trim();
    }

    /** @return autor */
    public String getAutor() {
        return autor;
    }

    /**
     * @param autor nuevo autor (no nulo ni vacío)
     * @throws IllegalArgumentException si es nulo o vacío
     */
    public void setAutor(String autor) {
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("El autor es obligatorio.");
        }
        this.autor = autor.trim();
    }

    /** @return ISBN (vacío si no tiene) */
    public String getIsbn() {
        return isbn;
    }

    /** @param isbn nuevo ISBN (nulo se guarda vacío) */
    public void setIsbn(String isbn) {
        this.isbn = isbn == null ? "" : isbn.trim();
    }

    /** @return id de la editorial */
    public Integer getIdEditorial() {
        return idEditorial;
    }

    /**
     * @param idEditorial id de la editorial (mayor que 0)
     * @throws IllegalArgumentException si es nulo o menor o igual a cero
     */
    public void setIdEditorial(Integer idEditorial) {
        if (idEditorial == null || idEditorial <= 0) {
            throw new IllegalArgumentException("Selecciona una editorial.");
        }
        this.idEditorial = idEditorial;
    }

    /** @return id de la categoría */
    public Integer getIdCategoria() {
        return idCategoria;
    }

    /**
     * @param idCategoria id de la categoría (mayor que 0)
     * @throws IllegalArgumentException si es nulo o menor o igual a cero
     */
    public void setIdCategoria(Integer idCategoria) {
        if (idCategoria == null || idCategoria <= 0) {
            throw new IllegalArgumentException("Selecciona una categoría.");
        }
        this.idCategoria = idCategoria;
    }

    /** @return precio */
    public double getPrecio() {
        return precio;
    }

    /**
     * @param precio nuevo precio (no negativo)
     * @throws IllegalArgumentException si es negativo
     */
    public void setPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        this.precio = precio;
    }

    /** @return ejemplares disponibles */
    public int getEjemplaresDisponibles() {
        return ejemplaresDisponibles;
    }

    /**
     * @param ejemplaresDisponibles cantidad (no negativa)
     * @throws IllegalArgumentException si es negativa
     */
    public void setEjemplaresDisponibles(int ejemplaresDisponibles) {
        if (ejemplaresDisponibles < 0) {
            throw new IllegalArgumentException("Los ejemplares disponibles no pueden ser negativos.");
        }
        this.ejemplaresDisponibles = ejemplaresDisponibles;
    }

    /** @return ejemplares en pedido */
    public int getEjemplaresEnPedido() {
        return ejemplaresEnPedido;
    }

    /**
     * @param ejemplaresEnPedido cantidad (no negativa)
     * @throws IllegalArgumentException si es negativa
     */
    public void setEjemplaresEnPedido(int ejemplaresEnPedido) {
        if (ejemplaresEnPedido < 0) {
            throw new IllegalArgumentException("Los ejemplares en pedido no pueden ser negativos.");
        }
        this.ejemplaresEnPedido = ejemplaresEnPedido;
    }

    /** @return nivel de reorden */
    public int getNivelReorden() {
        return nivelReorden;
    }

    /**
     * @param nivelReorden nivel (no negativo)
     * @throws IllegalArgumentException si es negativo
     */
    public void setNivelReorden(int nivelReorden) {
        if (nivelReorden < 0) {
            throw new IllegalArgumentException("El nivel de reorden no puede ser negativo.");
        }
        this.nivelReorden = nivelReorden;
    }

    /** @return {@code true} si el libro está descontinuado */
    public boolean isDescontinuado() {
        return descontinuado;
    }

    /** @param descontinuado si el libro está descontinuado */
    public void setDescontinuado(boolean descontinuado) {
        this.descontinuado = descontinuado;
    }

    @Override
    public String getUrlImagen() {
        return urlImagen;
    }

    @Override
    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen == null ? "" : urlImagen.trim();
    }

    /** @return nombre de la editorial (solo para mostrar) */
    public String getNombreEditorial() {
        return nombreEditorial;
    }

    /** @param nombreEditorial nombre de la editorial (solo para mostrar) */
    public void setNombreEditorial(String nombreEditorial) {
        this.nombreEditorial = nombreEditorial == null ? "" : nombreEditorial;
    }

    /** @return nombre de la categoría (solo para mostrar) */
    public String getNombreCategoria() {
        return nombreCategoria;
    }

    /** @param nombreCategoria nombre de la categoría (solo para mostrar) */
    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria == null ? "" : nombreCategoria;
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
        return Objects.equals(id, ((Libro) obj).id);
    }

    @Override
    public String toString() {
        return "Libro{id=" + id + ", titulo=" + titulo + ", autor=" + autor + ", isbn=" + isbn
                + ", idEditorial=" + idEditorial + ", idCategoria=" + idCategoria + ", precio=" + precio
                + ", ejemplaresDisponibles=" + ejemplaresDisponibles + ", ejemplaresEnPedido="
                + ejemplaresEnPedido + ", nivelReorden=" + nivelReorden + ", descontinuado="
                + descontinuado + ", urlImagen=" + urlImagen + "}";
    }
}
