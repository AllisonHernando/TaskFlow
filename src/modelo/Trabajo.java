package modelo;

public class Trabajo {
    private final String id;
    private final Solicitud solicitud;        // puede ser null (trabajos de prueba)
    private EstadoTrabajo estado;
    private ResultadoTrabajo resultado;       // null hasta que termina

    // Constructor original (no rompe a nadie)
    public Trabajo(String id) {
        this.id = id;
        this.solicitud = null;
        this.estado = EstadoTrabajo.PENDIENTE;
    }

    public Trabajo(Solicitud solicitud) {
        this.id = solicitud.getId();
        this.solicitud = solicitud;
        this.estado = EstadoTrabajo.PENDIENTE;
    }

    public synchronized EstadoTrabajo getEstado() {
        return estado;
    }

    public synchronized void setEstado(EstadoTrabajo nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public synchronized ResultadoTrabajo getResultado() {
        return resultado;
    }

    public synchronized void setResultado(ResultadoTrabajo resultado) {
        this.resultado = resultado;
    }

    public String getId() {
        return id;
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }
}