package edu.utj.evnd.poo.bibliotecaproyec.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Administra las imágenes de las personas. Estrategia: la imagen elegida se
 * <b>copia</b> a la carpeta {@code imagenes/} del proyecto con un nombre único
 * ({@code id_marcaDeTiempo.ext}) y en el registro se guarda solo la <b>ruta
 * relativa</b>. Así el registro no depende de dónde estaba el archivo original,
 * no se guardan bytes en memoria (no hay base de datos) y al reemplazar una
 * imagen se borra la anterior para no dejar archivos huérfanos.
 *
 * @author Joshua Odin Montoya Flores
 */
public class ImagenService {
    private static final Path CARPETA = Paths.get("imagenes");
    private static final long MAX_BYTES = 5L * 1024 * 1024;

    /**
     * Copia una imagen a la carpeta del proyecto.
     *
     * @param origen archivo elegido por el usuario
     * @param id     id de la persona a la que se asocia
     * @return ruta relativa donde quedó guardada (usa '/')
     * @throws IOException si el archivo supera 5 MB o no se puede copiar
     */
    public String guardar(File origen, int id) throws IOException {
        return guardar(origen, String.valueOf(id));
    }

    /**
     * Copia una imagen a la carpeta {@code imagenes/} con un nombre único
     * ({@code prefijo_marcaDeTiempo.extensión}).
     *
     * @param origen  archivo elegido por el usuario
     * @param prefijo texto con el que empieza el nombre (p. ej. "libro")
     * @return ruta relativa donde quedó guardada (usa '/')
     * @throws IOException si el archivo supera 5 MB o no se puede copiar
     */
    public String guardar(File origen, String prefijo) throws IOException {
        if (origen.length() > MAX_BYTES) {
            throw new IOException("La imagen supera el tamaño máximo de 5 MB.");
        }
        Files.createDirectories(CARPETA);
        String nombre = origen.getName();
        int punto = nombre.lastIndexOf('.');
        String extension = punto >= 0 ? nombre.substring(punto).toLowerCase() : "";
        Path destino = CARPETA.resolve(prefijo + "_" + System.currentTimeMillis() + extension);
        Files.copy(origen.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
        return destino.toString().replace('\\', '/');
    }

    /**
     * Borra una imagen guardada. Si la ruta está vacía o no existe, no hace nada.
     *
     * @param ruta ruta relativa devuelta por {@link #guardar(File, int)}
     */
    public void eliminar(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(Paths.get(ruta));
        } catch (IOException ignorada) {
            // Si no se puede borrar, el registro sigue siendo válido.
        }
    }

    /**
     * Convierte una ruta guardada en un archivo existente.
     *
     * @param ruta ruta relativa guardada en el registro
     * @return el archivo, o {@code null} si no hay ruta o el archivo no existe
     */
    public File resolver(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }
        File archivo = new File(ruta);
        return archivo.exists() ? archivo : null;
    }
}
