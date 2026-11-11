package edu.utj.evnd.poo.bibliotecaproyec.service;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Protege las contraseñas: nunca se guardan en texto plano. Se almacena
 * {@code sal:hash} (ambos en Base64), donde el hash se calcula con PBKDF2 (HMAC-SHA256,
 * 65 536 iteraciones) y una sal aleatoria distinta para cada contraseña.
 *
 * @author Joshua Odin Montoya Flores
 */
public final class Seguridad {
    private static final int ITERACIONES = 65536;
    private static final int LONGITUD_BITS = 256;

    private Seguridad() {
    }

    /**
     * Calcula el hash de una contraseña.
     *
     * @param clave contraseña en texto plano
     * @return texto {@code sal:hash} para guardar en la base de datos
     */
    public static String hash(String clave) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        return Base64.getEncoder().encodeToString(sal) + ":"
                + Base64.getEncoder().encodeToString(derivar(clave, sal));
    }

    /**
     * Comprueba una contraseña contra el valor guardado.
     *
     * @param clave      contraseña escrita por el usuario
     * @param almacenado valor {@code sal:hash} guardado en la base de datos
     * @return {@code true} si coincide
     */
    public static boolean verificar(String clave, String almacenado) {
        if (clave == null || almacenado == null) {
            return false;
        }
        String[] partes = almacenado.split(":");
        if (partes.length != 2) {
            return false;
        }
        try {
            byte[] sal = Base64.getDecoder().decode(partes[0]);
            byte[] esperado = Base64.getDecoder().decode(partes[1]);
            return MessageDigest.isEqual(esperado, derivar(clave, sal));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static byte[] derivar(String clave, byte[] sal) {
        try {
            PBEKeySpec especificacion = new PBEKeySpec(clave.toCharArray(), sal, ITERACIONES, LONGITUD_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(especificacion).getEncoded();
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña.", ex);
        }
    }
}
