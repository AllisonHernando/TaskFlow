package core;

import modelo.EstadoTrabajo;
import modelo.Trabajo;

public class GestorEstados {

    private int aceptados;
    private int pendientes;
    private int enCola;
    private int ejecutando;
    private int completados;
    private int fallidos;
    private int cancelados;

    private boolean transicionValida(EstadoTrabajo actual, EstadoTrabajo nuevo) {
        if (actual == EstadoTrabajo.PENDIENTE) {
            return nuevo == EstadoTrabajo.EN_COLA || nuevo == EstadoTrabajo.CANCELADO;
        }
        if (actual == EstadoTrabajo.EN_COLA) {
            return nuevo == EstadoTrabajo.EJECUTANDO || nuevo == EstadoTrabajo.CANCELADO;
        }
        if (actual == EstadoTrabajo.EJECUTANDO) {
            return nuevo == EstadoTrabajo.COMPLETADO || nuevo == EstadoTrabajo.FALLIDO;
        }
        return false;
    }

    private void sumar(EstadoTrabajo estado, int cantidad) {
        switch (estado) {
            case PENDIENTE: pendientes += cantidad; break;
            case EN_COLA: enCola += cantidad; break;
            case EJECUTANDO: ejecutando += cantidad; break;
            case COMPLETADO: completados += cantidad; break;
            case FALLIDO: fallidos += cantidad; break;
            case CANCELADO: cancelados += cantidad; break;
            default: break;
        }
    }

    public synchronized void registrar(Trabajo trabajo) {
        if (trabajo == null || trabajo.getEstado() != EstadoTrabajo.PENDIENTE) {
            throw new IllegalArgumentException("El trabajo no está en estado PENDIENTE");
        }
        aceptados++;
        pendientes++;
    }

    public synchronized void descartar(Trabajo trabajo) {
        if (trabajo != null && trabajo.getEstado() == EstadoTrabajo.PENDIENTE) {
            aceptados--;
            pendientes--;
        }
    }

    public synchronized boolean cambiarEstado(Trabajo trabajo, EstadoTrabajo nuevoEstado) {
        if (trabajo == null || nuevoEstado == null) return false;
        EstadoTrabajo actual = trabajo.getEstado();
        if (!transicionValida(actual, nuevoEstado)) return false;
        trabajo.setEstado(nuevoEstado);
        sumar(actual, -1);
        sumar(nuevoEstado, 1);
        return true;
    }

    public synchronized boolean cancelarTrabajo(Trabajo trabajo) {
        if (trabajo == null) return false;
        EstadoTrabajo actual = trabajo.getEstado();
        if (actual != EstadoTrabajo.PENDIENTE && actual != EstadoTrabajo.EN_COLA) return false;
        trabajo.setEstado(EstadoTrabajo.CANCELADO);
        sumar(actual, -1);
        sumar(EstadoTrabajo.CANCELADO, 1);
        return true;
    }

    public synchronized int getAceptados() { return aceptados; }
    public synchronized int getPendientes() { return pendientes; }
    public synchronized int getEnCola() { return enCola; }
    public synchronized int getEjecutando() { return ejecutando; }
    public synchronized int getEnProceso() { return ejecutando; }
    public synchronized int getCompletados() { return completados; }
    public synchronized int getFallidos() { return fallidos; }
    public synchronized int getCancelados() { return cancelados; }

    public synchronized boolean balanceCuadra() {
        return aceptados == completados + fallidos + cancelados;
    }

    public synchronized String resumen() {
        return "aceptados=" + aceptados
            + " pendientes=" + pendientes
            + " enCola=" + enCola
            + " ejecutando=" + ejecutando
            + " completados=" + completados
            + " fallidos=" + fallidos
            + " cancelados=" + cancelados;
    }
}
