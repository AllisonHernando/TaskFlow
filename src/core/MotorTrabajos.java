package core;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import factory.ProcessorFactory;
import modelo.EstadoTrabajo;
import modelo.Trabajo;

public class MotorTrabajos {
    private final BlockingQueue<Trabajo> colaTrabajos;
    private final ExecutorService poolTrabajadores;
    private final GestorEstados gestorEstados;
    private final ProcessorFactory factory = new ProcessorFactory();
    private final Trabajo FIN = new Trabajo("FIN");
    private final AtomicInteger trabajadoresActivos = new AtomicInteger(0);
    private final Queue<Future<Boolean>> futuros = new ConcurrentLinkedQueue<>();
    private final Object candado = new Object();
    private volatile boolean activo;
    private final int numeroTrabajadores;

    public MotorTrabajos(int capacidadCola, int numHilosPool) {
        this(capacidadCola, numHilosPool, new GestorEstados());
    }

    public MotorTrabajos(int capacidadCola, int numHilosPool, GestorEstados gestorEstados) {
        if (capacidadCola <= 0 || numHilosPool < 4) {
            throw new IllegalArgumentException("La cola debe tener capacidad positiva y debe haber al menos cuatro trabajadores");
        }
        this.colaTrabajos = new ArrayBlockingQueue<>(capacidadCola);
        this.poolTrabajadores = Executors.newFixedThreadPool(numHilosPool);
        this.gestorEstados = gestorEstados;
        this.numeroTrabajadores = numHilosPool;
        this.activo = true;

        for (int i = 0; i < numeroTrabajadores; i++) {
            Future<Boolean> futuro = poolTrabajadores.submit(
                new HiloTrabajador(colaTrabajos, FIN, gestorEstados, factory, trabajadoresActivos));
            futuros.add(futuro);
        }
    }

    public boolean agregarTrabajo(Trabajo trabajo) throws InterruptedException {
        if (trabajo == null) return false;
        synchronized (candado) {
            if (!activo) return false;
            synchronized (trabajo) {
                gestorEstados.registrar(trabajo);
                if (!colaTrabajos.offer(trabajo)) {
                    gestorEstados.descartar(trabajo);
                    return false;
                }
                gestorEstados.cambiarEstado(trabajo, EstadoTrabajo.EN_COLA);
                return true;
            }
        }
    }

    public Trabajo obtenerSiguienteTrabajo() throws InterruptedException {
        return colaTrabajos.take();
    }

    public boolean cerrar(long timeoutSegundos) throws InterruptedException {
        synchronized (candado) {
            if (!activo) {
                return poolTrabajadores.isTerminated();
            }
            activo = false;
        }

        for (int i = 0; i < numeroTrabajadores; i++) {
            colaTrabajos.put(FIN);
        }

        poolTrabajadores.shutdown();
        boolean limpio = poolTrabajadores.awaitTermination(timeoutSegundos, TimeUnit.SECONDS);
        if (!limpio) {
            poolTrabajadores.shutdownNow();
            limpio = poolTrabajadores.awaitTermination(2, TimeUnit.SECONDS);
        }
        return limpio;
    }

    public void apagarMotor() {
        try {
            cerrar(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getTrabajosEnEspera() { return colaTrabajos.size(); }
    public int getTrabajadoresActivos() { return trabajadoresActivos.get(); }
    public boolean estaActivo() { return activo; }
    public GestorEstados getGestorEstados() { return gestorEstados; }
    public Queue<Future<Boolean>> getFuturos() { return futuros; }
}
