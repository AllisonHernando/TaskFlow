package core;

import modelo.Trabajo;
import modelo.EstadoTrabajo;

public class GestorEstados {

    public synchronized boolean cambiarEstado(Trabajo trabajo, EstadoTrabajo nuevoEstado) {
        EstadoTrabajo actual = trabajo.getEstado();

        if (actual == EstadoTrabajo.COMPLETADO || actual == EstadoTrabajo.FALLIDO || actual == EstadoTrabajo.CANCELADO) {
            return false;
        }

        trabajo.setEstado(nuevoEstado);
        return true;
    }

    public synchronized boolean cancelarTrabajo(Trabajo trabajo) {
        EstadoTrabajo actual = trabajo.getEstado();
        if (actual == EstadoTrabajo.PENDIENTE) {
            trabajo.setEstado(EstadoTrabajo.CANCELADO);
            return true;
        }
        return false;
    }
}