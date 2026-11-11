package edu.utj.evnd.poo.bibliotecaproyec.dao;

/**
 * Credenciales incorrectas o cuenta que no puede iniciar sesión. El mensaje se
 * muestra tal cual en la ventana de login.
 *
 * @author Joshua Odin Montoya Flores
 */
public class AutenticacionException extends Exception {
    private static final long serialVersionUID = 1L;

    public AutenticacionException(String mensaje) {
        super(mensaje);
    }
}
