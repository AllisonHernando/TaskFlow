package modelo;

public class Trabajo {
    private final String id;
    private EstadoTrabajo estado;

    public Trabajo(String id) {
        this.id = id;
        this.estado = EstadoTrabajo.PENDIENTE;
    }

    public synchronized EstadoTrabajo getEstado() {
        return estado;
    }

    public synchronized void setEstado(EstadoTrabajo nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public String getId() {
        return id;
    }
}