package edu.utj.evnd.poo.bibliotecaproyec.enums;

/**
 * Acciones del CRUD que pueden permitirse o restringirse según el rol.
 *
 * @author Joshua Odin Montoya Flores
 */
public enum Accion {
    CONSULTAR("consultar"),
    CREAR("registrar"),
    EDITAR("modificar"),
    ELIMINAR("eliminar");

    private final String verbo;

    Accion(String verbo) {
        this.verbo = verbo;
    }

    /** @return verbo en infinitivo para los mensajes al usuario */
    public String getVerbo() {
        return verbo;
    }
}
