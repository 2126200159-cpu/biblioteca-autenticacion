package edu.utj.evnd.poo.bibliotecaproyec.model;

/**
 * Persona que consulta y solicita préstamos de libros en la biblioteca.
 * Es {@code final} para impedir derivaciones posteriores.
 *
 * @author Joshua Odin Montoya Flores
 */
public final class Lector extends Persona {
    private String numeroCredencial;
    private double multaPendiente;

    /** Constructor vacío: sin credencial y sin multas. */
    public Lector() {
        super();
        this.numeroCredencial = "";
        this.multaPendiente = 0.0;
    }

    /**
     * Constructor con los datos mínimos para un lector válido.
     *
     * @param id               identificador primario
     * @param curp             CURP
     * @param nombre           nombre(s)
     * @param apellido         apellido(s)
     * @param numeroCredencial número de credencial de biblioteca (no vacío)
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public Lector(Integer id, String curp, String nombre, String apellido,
                  String numeroCredencial) {
        super(id, curp, nombre, apellido);
        setNumeroCredencial(numeroCredencial);
    }

    /** @return número de credencial */
    public String getNumeroCredencial() {
        return numeroCredencial;
    }

    /**
     * @param numeroCredencial nueva credencial (no nula ni vacía)
     * @throws IllegalArgumentException si es nula o vacía
     */
    public void setNumeroCredencial(String numeroCredencial) {
        if (numeroCredencial == null || numeroCredencial.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de credencial es obligatorio.");
        }
        this.numeroCredencial = numeroCredencial.trim();
    }

    /** @return monto de multa pendiente */
    public double getMultaPendiente() {
        return multaPendiente;
    }

    /**
     * @param multaPendiente nueva multa (no negativa)
     * @throws IllegalArgumentException si es negativa
     */
    public void setMultaPendiente(double multaPendiente) {
        if (multaPendiente < 0) {
            throw new IllegalArgumentException("La multa no puede ser negativa.");
        }
        this.multaPendiente = multaPendiente;
    }

    /**
     * @return datos de {@link Persona} más los propios del lector
     */
    @Override
    public String toString() {
        return super.toString() + " Lector{numeroCredencial=" + numeroCredencial
                + ", multaPendiente=" + multaPendiente + "}";
    }
}
