package edu.utj.evnd.poo.bibliotecaproyec.repository;

import edu.utj.evnd.poo.bibliotecaproyec.model.Persona;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio genérico en memoria para cualquier entidad {@link Persona}.
 * No usa base de datos: los registros viven mientras la aplicación está abierta.
 * Garantiza que no existan dos registros con el mismo id ni la misma CURP.
 *
 * @param <T> tipo de persona que almacena
 * @author Joshua Odin Montoya Flores
 */
public class PersonaRepository<T extends Persona> {
    private final List<T> elementos = new ArrayList<>();

    /**
     * Agrega un registro nuevo.
     *
     * @param persona registro a agregar
     * @return {@code true} si se agregó; {@code false} si el id o la CURP ya existen
     */
    public boolean agregar(T persona) {
        if (buscarPorId(persona.getId()) != null || existeCurp(persona.getCurp(), null)) {
            return false;
        }
        return elementos.add(persona);
    }

    /**
     * @return copia de la lista con todos los registros
     */
    public List<T> obtenerTodos() {
        return new ArrayList<>(elementos);
    }

    /**
     * Busca un registro por su identificador.
     *
     * @param id identificador a buscar
     * @return el registro o {@code null} si no existe
     */
    public T buscarPorId(int id) {
        for (T p : elementos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    /**
     * Reemplaza el registro que tenga el mismo id.
     *
     * @param persona registro con los datos nuevos
     * @return {@code true} si se actualizó; {@code false} si no existe o la CURP
     *         pertenece a otro registro
     */
    public boolean actualizar(T persona) {
        for (int i = 0; i < elementos.size(); i++) {
            if (elementos.get(i).getId().equals(persona.getId())) {
                if (existeCurp(persona.getCurp(), persona.getId())) {
                    return false;
                }
                elementos.set(i, persona);
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina el registro con el id indicado.
     *
     * @param id identificador del registro
     * @return {@code true} si se eliminó
     */
    public boolean eliminar(int id) {
        return elementos.removeIf(p -> p.getId() == id);
    }

    /**
     * Busca sobre el ArrayList los registros cuyo ID, CURP, nombre completo o
     * correo contengan el texto indicado (sin distinguir mayúsculas).
     *
     * @param texto texto a buscar
     * @return lista con las coincidencias (vacía si no hay)
     */
    public List<T> buscar(String texto) {
        List<T> resultado = new ArrayList<>();
        String t = texto == null ? "" : texto.trim().toLowerCase();
        for (T p : elementos) {
            if (String.valueOf(p.getId()).contains(t)
                    || p.getCurp().toLowerCase().contains(t)
                    || p.getNombreCompleto().toLowerCase().contains(t)
                    || p.getCorreo().toLowerCase().contains(t)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    /**
     * Indica si una CURP ya pertenece a un registro distinto del indicado.
     *
     * @param curp       CURP a revisar
     * @param exceptoId  id que se ignora (el del propio registro) o {@code null}
     * @return {@code true} si la CURP está en uso por otro registro
     */
    public boolean existeCurp(String curp, Integer exceptoId) {
        for (T p : elementos) {
            if (p.getCurp().equalsIgnoreCase(curp) && !p.getId().equals(exceptoId)) {
                return true;
            }
        }
        return false;
    }
}
