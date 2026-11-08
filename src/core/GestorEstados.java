package core;

import modelo.EstadoTrabajo;
import modelo.Trabajo;

public class GestorEstados {

    private int aceptados = 0;
    private int pendientes = 0;
    private int enProceso = 0;
    private int completados = 0;
    private int fallidos = 0;
    private int cancelados = 0;

    private boolean transicionValida(EstadoTrabajo actual, EstadoTrabajo nuevo) {
        if (actual == EstadoTrabajo.PENDIENTE) {
            return nuevo == EstadoTrabajo.EN_PROCESO || nuevo == EstadoTrabajo.CANCELADO;
        }
        if (actual == EstadoTrabajo.EN_PROCESO) {
            return nuevo == EstadoTrabajo.COMPLETADO
                || nuevo == EstadoTrabajo.FALLIDO
                || nuevo == EstadoTrabajo.CANCELADO;
        }
        return false; 
    }

    private void sumar(EstadoTrabajo estado, int cantidad) {
        if (estado == EstadoTrabajo.PENDIENTE) {
            pendientes += cantidad;
        } else if (estado == EstadoTrabajo.EN_PROCESO) {
            enProceso += cantidad;
        } else if (estado == EstadoTrabajo.COMPLETADO) {
            completados += cantidad;
        } else if (estado == EstadoTrabajo.FALLIDO) {
            fallidos += cantidad;
        } else if (estado == EstadoTrabajo.CANCELADO) {
            cancelados += cantidad;
        }
    }

    public synchronized void registrar(Trabajo trabajo) {
        aceptados++;
        pendientes++;
    }

    public synchronized void descartar(Trabajo trabajo) {
        aceptados--;
        pendientes--;
    }

    public synchronized boolean cambiarEstado(Trabajo trabajo, EstadoTrabajo nuevoEstado) {
        EstadoTrabajo actual = trabajo.getEstado();
        if (!transicionValida(actual, nuevoEstado)) {
            return false;
        }
        trabajo.setEstado(nuevoEstado);
        sumar(actual, -1);
        sumar(nuevoEstado, 1);
        return true;
    }

    public synchronized boolean cancelarTrabajo(Trabajo trabajo) {
        if (trabajo.getEstado() == EstadoTrabajo.PENDIENTE) {
            trabajo.setEstado(EstadoTrabajo.CANCELADO);
            sumar(EstadoTrabajo.PENDIENTE, -1);
            sumar(EstadoTrabajo.CANCELADO, 1);
            return true;
        }
        return false;
    }


    public synchronized int getAceptados()   { return aceptados; }
    public synchronized int getPendientes()  { return pendientes; }
    public synchronized int getEnProceso()   { return enProceso; }
    public synchronized int getCompletados() { return completados; }
    public synchronized int getFallidos()    { return fallidos; }
    public synchronized int getCancelados()  { return cancelados; }

    public synchronized boolean balanceCuadra() {
        return aceptados == completados + fallidos + cancelados;
    }

    public synchronized String resumen() {
        return "aceptados=" + aceptados
            + " pendientes=" + pendientes
            + " enProceso=" + enProceso
            + " completados=" + completados
            + " fallidos=" + fallidos
            + " cancelados=" + cancelados;
    }
}