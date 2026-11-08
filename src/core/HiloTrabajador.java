package core;

import java.util.concurrent.Callable;

import factory.ProcessorFactory;
import modelo.EstadoTrabajo;
import modelo.ResultadoTrabajo;
import modelo.Solicitud;
import modelo.Trabajo;
import processors.OperacionStrategy;

public class HiloTrabajador implements Callable<Boolean> {
    private final Trabajo trabajo;
    private final GestorEstados gestorEstados;
    private final ProcessorFactory factory;

    public HiloTrabajador(Trabajo trabajo, GestorEstados gestorEstados) {
        this(trabajo, gestorEstados, null);
    }

    public HiloTrabajador(Trabajo trabajo, GestorEstados gestorEstados, ProcessorFactory factory) {
        this.trabajo = trabajo;
        this.gestorEstados = gestorEstados;
        this.factory = factory;
    }

    @Override
    public Boolean call() throws Exception {
        long inicio = System.currentTimeMillis();
        try {
            boolean iniciado = gestorEstados.cambiarEstado(trabajo, EstadoTrabajo.EJECUTANDO);
            if (!iniciado) {
                return false;
            }

            inicio = System.currentTimeMillis();
            Solicitud solicitud = trabajo.getSolicitud();
            String salida;
            if (factory != null && solicitud != null) {
                OperacionStrategy operacion = factory.crear(solicitud.getOperacion());
                salida = operacion.ejecutar(solicitud);
            } else {
                Thread.sleep(100); 
                salida = "Trabajo de prueba";
            }

            long tiempo = System.currentTimeMillis() - inicio;
            trabajo.setResultado(new ResultadoTrabajo(trabajo.getId(), true, salida, tiempo));
            return gestorEstados.cambiarEstado(trabajo, EstadoTrabajo.COMPLETADO);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            long tiempo = System.currentTimeMillis() - inicio;
            trabajo.setResultado(new ResultadoTrabajo(trabajo.getId(), false, "Cancelado", tiempo));
            gestorEstados.cambiarEstado(trabajo, EstadoTrabajo.CANCELADO);
            return false;
        } catch (Exception e) {
            long tiempo = System.currentTimeMillis() - inicio;
            trabajo.setResultado(new ResultadoTrabajo(trabajo.getId(), false, "Error: " + e.getMessage(), tiempo));
            gestorEstados.cambiarEstado(trabajo, EstadoTrabajo.FALLIDO);
            return false;
        }
    }
}