package edu.utj.evnd.poo.bibliotecaproyec.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Administra las imágenes de la aplicación. Estrategia: la imagen elegida se
 * <b>copia</b> a la carpeta {@code Documentos/SistemaBiblioteca/imagenes/}
 * (ver {@link AlmacenamientoService}) con un nombre único
 * ({@code prefijo_marcaDeTiempo.ext}) y en el registro se guarda solo el
 * <b>nombre del archivo</b>. Así la base no depende de dónde estaba el archivo
 * original ni de la carpeta desde la que se ejecuta el programa, y al
 * reemplazar una imagen se borra la anterior para no dejar archivos huérfanos.
 *
 * <p>Los registros antiguos que guardaban {@code imagenes/archivo.png} siguen
 * funcionando: se toma solo el nombre del archivo y se busca en la carpeta
 * nueva (y, si no está, en la ruta relativa antigua).</p>
 *
 * @author Joshua Odin Montoya Flores
 */
public class ImagenService {
    private static final long MAX_BYTES = 5L * 1024 * 1024;

    /**
     * Copia una imagen a la carpeta de imágenes.
     *
     * @param origen archivo elegido por el usuario
     * @param id     id de la persona a la que se asocia
     * @return nombre del archivo guardado (es lo que se almacena en la base)
     * @throws IOException si el archivo supera 5 MB o no se puede copiar
     */
    public String guardar(File origen, int id) throws IOException {
        return guardar(origen, String.valueOf(id));
    }

    /**
     * Copia una imagen a la carpeta de imágenes con un nombre único
     * ({@code prefijo_marcaDeTiempo.extensión}).
     *
     * @param origen  archivo elegido por el usuario
     * @param prefijo texto con el que empieza el nombre (p. ej. "libro")
     * @return nombre del archivo guardado (es lo que se almacena en la base)
     * @throws IOException si el archivo supera 5 MB o no se puede copiar
     */
    public String guardar(File origen, String prefijo) throws IOException {
        if (origen.length() > MAX_BYTES) {
            throw new IOException("La imagen supera el tamaño máximo de 5 MB.");
        }
        Path carpeta = AlmacenamientoService.getCarpetaImagenes();
        Files.createDirectories(carpeta);
        String nombre = origen.getName();
        int punto = nombre.lastIndexOf('.');
        String extension = punto >= 0 ? nombre.substring(punto).toLowerCase() : "";
        String nombreFinal = prefijo + "_" + System.currentTimeMillis() + extension;
        Files.copy(origen.toPath(), carpeta.resolve(nombreFinal), StandardCopyOption.REPLACE_EXISTING);
        return nombreFinal;
    }

    /**
     * Borra una imagen guardada. Si la ruta está vacía o no existe, no hace nada.
     *
     * @param ruta valor guardado en el registro (nombre de archivo o ruta antigua)
     */
    public void eliminar(String ruta) {
        File archivo = resolver(ruta);
        if (archivo == null) {
            return;
        }
        try {
            Files.deleteIfExists(archivo.toPath());
        } catch (IOException ignorada) {
            // Si no se puede borrar, el registro sigue siendo válido.
        }
    }

    /**
     * Convierte el valor guardado en un archivo existente.
     *
     * @param ruta nombre de archivo (o ruta relativa antigua) guardado en el registro
     * @return el archivo, o {@code null} si no hay valor o el archivo no existe
     */
    public File resolver(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }
        Path nombre = Paths.get(ruta.replace('\\', '/')).getFileName();
        if (nombre != null) {
            File enCarpeta = AlmacenamientoService.getCarpetaImagenes().resolve(nombre.toString()).toFile();
            if (enCarpeta.isFile()) {
                return enCarpeta;
            }
        }
        File antiguo = new File(ruta);
        return antiguo.isFile() ? antiguo : null;
    }
}
