package edu.utj.evnd.poo.bibliotecaproyec.enums;

/**
 * Estados posibles de la cuenta de un {@code Administrador}.
 *
 * @author Joshua Odin Montoya Flores
 */
public enum EstadoAdministrador {
    /** Cuenta operativa. */
    ACTIVO,
    /** Cuenta bloqueada por seguridad. */
    BLOQUEADO,
    /** Cuenta suspendida temporalmente. */
    SUSPENDIDO,
    /** Cuenta dada de baja definitivamente. */
    BAJA
}
