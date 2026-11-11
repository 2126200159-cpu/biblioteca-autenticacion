package edu.utj.evnd.poo.bibliotecaproyec.service;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Protege las contraseñas con PBKDF2 (HMAC-SHA256, sal aleatoria). En la base
 * se guarda el texto {@code pbkdf2$iteraciones$sal$hash}, nunca la contraseña.
 *
 * @author Joshua Odin Montoya Flores
 */
public final class ContrasenaService {
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 65536;
    private static final int BITS_HASH = 256;
    private static final int BYTES_SAL = 16;
    private static final String FORMATO = "pbkdf2\\$\\d+\\$[A-Za-z0-9+/=]+\\$[A-Za-z0-9+/=]+";
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private ContrasenaService() {
    }

    /**
     * @param clave contraseña en texto
     * @return texto con iteraciones, sal y hash, listo para guardar
     */
    public static String hash(String clave) {
        byte[] sal = new byte[BYTES_SAL];
        ALEATORIO.nextBytes(sal);
        byte[] resumen = derivar(clave, sal, ITERACIONES);
        return "pbkdf2$" + ITERACIONES + "$" + Base64.getEncoder().encodeToString(sal)
                + "$" + Base64.getEncoder().encodeToString(resumen);
    }

    /** @return {@code true} si el texto ya tiene el formato de un hash de esta clase */
    public static boolean esHash(String texto) {
        return texto != null && texto.matches(FORMATO);
    }

    /**
     * Devuelve el hash de la contraseña; si ya es un hash lo deja igual (así
     * una cuenta que se edita sin cambiar la contraseña no se vuelve a cifrar).
     *
     * @param clave contraseña en texto o hash existente
     * @return hash para guardar en la base
     */
    public static String protegerSiNecesario(String clave) {
        return esHash(clave) ? clave : hash(clave);
    }

    /**
     * Comprueba una contraseña contra lo guardado en la base. Acepta también
     * contraseñas antiguas en texto plano (de la actividad anterior); quien
     * llama debe convertirlas a hash con {@link #esHash(String)} + {@link #hash}.
     *
     * @param clave      contraseña escrita por el usuario
     * @param almacenada valor de la columna {@code contrasena}
     * @return {@code true} si coincide
     */
    public static boolean verificar(String clave, String almacenada) {
        if (clave == null || almacenada == null) {
            return false;
        }
        if (!esHash(almacenada)) {
            return MessageDigest.isEqual(clave.getBytes(StandardCharsets.UTF_8),
                    almacenada.getBytes(StandardCharsets.UTF_8));
        }
        String[] partes = almacenada.split("\\$");
        try {
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            return MessageDigest.isEqual(esperado, derivar(clave, sal, iteraciones));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static byte[] derivar(String clave, byte[] sal, int iteraciones) {
        try {
            PBEKeySpec spec = new PBEKeySpec(clave.toCharArray(), sal, iteraciones, BITS_HASH);
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("No está disponible " + ALGORITMO, ex);
        }
    }
}
