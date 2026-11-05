package core;

import modelo.Trabajo;
import modelo.EstadoTrabajo;
import java.util.concurrent.Callable;

public class HiloTrabajador implements Callable<Boolean> {
    private final Trabajo trabajo;
    private final GestorEstados gestorEstados;

    public HiloTrabajador(Trabajo trabajo, GestorEstados gestorEstados) {
        this.trabajo = trabajo;
        this.gestorEstados = gestorEstados;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            boolean iniciado = gestorEstados.cambiarEstado(trabajo, EstadoTrabajo.EN_PROCESO);
            if (!iniciado) {
                return false; 
            }

            Thread.sleep(100); 

            gestorEstados.cambiarEstado(trabajo, EstadoTrabajo.COMPLETADO);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            gestorEstados.cambiarEstado(trabajo, EstadoTrabajo.CANCELADO);
            return false;
        } catch (Exception e) {
            gestorEstados.cambiarEstado(trabajo, EstadoTrabajo.FALLIDO);
            return false;
        }
    }
}