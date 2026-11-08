package core;

import modelo.EstadoTrabajo;
import modelo.Trabajo;

public class GestorEstados {

    private int aceptados = 0;
    private int pendientes = 0;
    private int enCola = 0;
    private int ejecutando = 0;
    private int completados = 0;
    private int fallidos = 0;
    private int cancelados = 0;

    private boolean transicionValida(EstadoTrabajo actual, EstadoTrabajo nuevo) {
        if (actual == EstadoTrabajo.PENDIENTE) {
            return nuevo == EstadoTrabajo.EN_COLA || nuevo == EstadoTrabajo.CANCELADO;
        }
        if (actual == EstadoTrabajo.EN_COLA) {
            return nuevo == EstadoTrabajo.EJECUTANDO || nuevo == EstadoTrabajo.CANCELADO;
        }
        if (actual == EstadoTrabajo.EJECUTANDO) {
            return nuevo == EstadoTrabajo.COMPLETADO
                || nuevo == EstadoTrabajo.FALLIDO
                || nuevo == EstadoTrabajo.CANCELADO;
        }
        return false; 
    }

    private void sumar(EstadoTrabajo estado, int cantidad) {
        if (estado == EstadoTrabajo.PENDIENTE) {
            pendientes += cantidad;
        } else if (estado == EstadoTrabajo.EN_COLA) {
            enCola += cantidad;
        } else if (estado == EstadoTrabajo.EJECUTANDO) {
            ejecutando += cantidad;
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
        trabajo.setEstado(EstadoTrabajo.EN_COLA);
        enCola++;
    }

    public synchronized void descartar(Trabajo trabajo) {
        aceptados--;
        enCola--;
        trabajo.setEstado(EstadoTrabajo.PENDIENTE);
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
        EstadoTrabajo actual = trabajo.getEstado();
        if (actual == EstadoTrabajo.EN_COLA || actual == EstadoTrabajo.PENDIENTE) {
            trabajo.setEstado(EstadoTrabajo.CANCELADO);
            sumar(actual, -1);
            sumar(EstadoTrabajo.CANCELADO, 1);
            return true;
        }
        return false;
    }


    public synchronized int getAceptados()   { return aceptados; }
    public synchronized int getEnCola()      { return enCola; }
    public synchronized int getEjecutando()  { return ejecutando; }
    public synchronized int getCompletados() { return completados; }
    public synchronized int getFallidos()    { return fallidos; }
    public synchronized int getCancelados()  { return cancelados; }

    public synchronized int getPendientes()  { return enCola; }
    public synchronized int getEnProceso()   { return ejecutando; }

    public synchronized boolean balanceCuadra() {
        return aceptados == completados + fallidos + cancelados;
    }

    public synchronized String resumen() {
        return "aceptados=" + aceptados
            + " enCola=" + enCola
            + " ejecutando=" + ejecutando
            + " completados=" + completados
            + " fallidos=" + fallidos
            + " cancelados=" + cancelados;
    }
}