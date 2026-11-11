package edu.utj.evnd.poo.bibliotecaproyec.model;

import java.util.Objects;

/**
 * Editorial que provee los libros a la biblioteca.
 *
 * @author Joshua Odin Montoya Flores
 */
public class Editorial implements Imagenable {
    private Integer id;
    private String nombre;
    private String nombreContacto;
    private String cargoContacto;
    private String direccion;
    private String ciudad;
    private String region;
    private String codigoPostal;
    private String pais;
    private String telefono;
    private String sitioWeb;
    private String urlImagen;

    /** Constructor vacío con valores por defecto. */
    public Editorial() {
        this.id = 0;
        this.nombre = "";
        this.nombreContacto = "";
        this.cargoContacto = "";
        this.direccion = "";
        this.ciudad = "";
        this.region = "";
        this.codigoPostal = "";
        this.pais = "";
        this.telefono = "";
        this.sitioWeb = "";
        this.urlImagen = "";
    }

    /**
     * Constructor con los datos mínimos.
     *
     * @param nombre nombre de la editorial (no vacío)
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    public Editorial(String nombre) {
        this();
        setNombre(nombre);
    }

    private static String limpio(String valor) {
        return valor == null ? "" : valor.trim();
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
            throw new IllegalArgumentException("El nombre de la editorial es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    /** @return nombre del contacto */
    public String getNombreContacto() {
        return nombreContacto;
    }

    /** @param nombreContacto nombre del contacto */
    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = limpio(nombreContacto);
    }

    /** @return cargo del contacto */
    public String getCargoContacto() {
        return cargoContacto;
    }

    /** @param cargoContacto cargo del contacto */
    public void setCargoContacto(String cargoContacto) {
        this.cargoContacto = limpio(cargoContacto);
    }

    /** @return dirección */
    public String getDireccion() {
        return direccion;
    }

    /** @param direccion dirección */
    public void setDireccion(String direccion) {
        this.direccion = limpio(direccion);
    }

    /** @return ciudad */
    public String getCiudad() {
        return ciudad;
    }

    /** @param ciudad ciudad */
    public void setCiudad(String ciudad) {
        this.ciudad = limpio(ciudad);
    }

    /** @return estado o región */
    public String getRegion() {
        return region;
    }

    /** @param region estado o región */
    public void setRegion(String region) {
        this.region = limpio(region);
    }

    /** @return código postal */
    public String getCodigoPostal() {
        return codigoPostal;
    }

    /** @param codigoPostal código postal */
    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = limpio(codigoPostal);
    }

    /** @return país */
    public String getPais() {
        return pais;
    }

    /** @param pais país */
    public void setPais(String pais) {
        this.pais = limpio(pais);
    }

    /** @return teléfono */
    public String getTelefono() {
        return telefono;
    }

    /** @param telefono teléfono */
    public void setTelefono(String telefono) {
        this.telefono = limpio(telefono);
    }

    /** @return sitio web */
    public String getSitioWeb() {
        return sitioWeb;
    }

    /** @param sitioWeb sitio web */
    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = limpio(sitioWeb);
    }

    @Override
    public String getUrlImagen() {
        return urlImagen;
    }

    @Override
    public void setUrlImagen(String urlImagen) {
        this.urlImagen = limpio(urlImagen);
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
        return Objects.equals(id, ((Editorial) obj).id);
    }

    @Override
    public String toString() {
        return "Editorial{id=" + id + ", nombre=" + nombre + ", nombreContacto=" + nombreContacto
                + ", cargoContacto=" + cargoContacto + ", direccion=" + direccion + ", ciudad=" + ciudad
                + ", region=" + region + ", codigoPostal=" + codigoPostal + ", pais=" + pais
                + ", telefono=" + telefono + ", sitioWeb=" + sitioWeb + ", urlImagen=" + urlImagen + "}";
    }
}
