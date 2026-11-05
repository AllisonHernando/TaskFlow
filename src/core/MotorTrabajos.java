package core;

import modelo.Trabajo;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MotorTrabajos {
    private final BlockingQueue<Trabajo> colaTrabajos;
    private final ExecutorService poolTrabajadores;
    private volatile boolean activo;

    public MotorTrabajos(int capacidadCola, int numHilosPool) {
        this.colaTrabajos = new ArrayBlockingQueue<>(capacidadCola);
        this.poolTrabajadores = Executors.newFixedThreadPool(numHilosPool);
        this.activo = true;
    }

    public void agregarTrabajo(Trabajo trabajo) throws InterruptedException {
        if (activo) {
            colaTrabajos.put(trabajo);
        }
    }

    public Trabajo obtenerSiguienteTrabajo() throws InterruptedException {
        return colaTrabajos.take();
    }

    public void apagarMotor() {
        this.activo = false;
        poolTrabajadores.shutdown();
    }
}