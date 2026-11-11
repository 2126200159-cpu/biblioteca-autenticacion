package edu.utj.evnd.poo.bibliotecaproyec.model;

/**
 * Entidad que tiene una imagen asociada. Permite que el controlador genérico
 * del CRUD gestione la imagen sin conocer la clase concreta.
 *
 * @author Joshua Odin Montoya Flores
 */
public interface Imagenable {

    /** @return identificador de la entidad */
    Integer getId();

    /** @return ruta relativa de la imagen guardada (vacía si no tiene) */
    String getUrlImagen();

    /** @param urlImagen nueva ruta relativa de la imagen */
    void setUrlImagen(String urlImagen);
}
