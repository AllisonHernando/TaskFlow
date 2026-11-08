package core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import factory.ProcessorFactory;
import modelo.EstadoTrabajo;
import modelo.ResultadoTrabajo;
import modelo.Solicitud;
import modelo.Trabajo;
import processors.OperacionStrategy;

public class HiloTrabajador implements Callable<Boolean> {
    private final Trabajo trabajo;
    private final BlockingQueue<Trabajo> cola;
    private final Trabajo fin;
    private final GestorEstados gestorEstados;
    private final ProcessorFactory factory;
    private final AtomicInteger trabajadoresActivos;

    public HiloTrabajador(Trabajo trabajo, GestorEstados gestorEstados) {
        this(trabajo, gestorEstados, null);
    }

    public HiloTrabajador(Trabajo trabajo, GestorEstados gestorEstados, ProcessorFactory factory) {
        this.trabajo = trabajo;
        this.cola = null;
        this.fin = null;
        this.gestorEstados = gestorEstados;
        this.factory = factory;
        this.trabajadoresActivos = null;
    }

    public HiloTrabajador(BlockingQueue<Trabajo> cola, Trabajo fin,
                          GestorEstados gestorEstados, ProcessorFactory factory,
                          AtomicInteger trabajadoresActivos) {
        this.trabajo = null;
        this.cola = cola;
        this.fin = fin;
        this.gestorEstados = gestorEstados;
        this.factory = factory;
        this.trabajadoresActivos = trabajadoresActivos;
    }

    @Override
    public Boolean call() {
        if (cola == null) {
            return ejecutarTrabajo(trabajo);
        }

        try {
            while (!Thread.currentThread().isInterrupted()) {
                Trabajo siguiente = cola.take();
                if (siguiente == fin) break;
                ejecutarTrabajo(siguiente);
            }
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private boolean ejecutarTrabajo(Trabajo trabajoActual) {
        synchronized (trabajoActual) {
            if (!gestorEstados.cambiarEstado(trabajoActual, EstadoTrabajo.EJECUTANDO)) {
                return false;
            }
        }

        if (trabajadoresActivos != null) trabajadoresActivos.incrementAndGet();
        long inicio = System.currentTimeMillis();
        try {
            Solicitud solicitud = trabajoActual.getSolicitud();
            String salida;
            if (factory != null && solicitud != null) {
                OperacionStrategy operacion = factory.crear(solicitud.getOperacion());
                salida = operacion.ejecutar(solicitud);
            } else {
                Thread.sleep(100);
                salida = "Trabajo de prueba";
            }

            long duracion = System.currentTimeMillis() - inicio;
            trabajoActual.setResultado(new ResultadoTrabajo(
                trabajoActual.getId(), true, salida, duracion));
            return gestorEstados.cambiarEstado(trabajoActual, EstadoTrabajo.COMPLETADO);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            long duracion = System.currentTimeMillis() - inicio;
            trabajoActual.setResultado(new ResultadoTrabajo(
                trabajoActual.getId(), false, "Error: ejecución interrumpida", duracion));
            gestorEstados.cambiarEstado(trabajoActual, EstadoTrabajo.FALLIDO);
            return false;
        } catch (Throwable e) {
            long duracion = System.currentTimeMillis() - inicio;
            String mensaje = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            trabajoActual.setResultado(new ResultadoTrabajo(
                trabajoActual.getId(), false, "Error: " + mensaje, duracion));
            gestorEstados.cambiarEstado(trabajoActual, EstadoTrabajo.FALLIDO);
            return false;
        } finally {
            if (trabajadoresActivos != null) trabajadoresActivos.decrementAndGet();
        }
    }
}
