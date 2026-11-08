package core;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import modelo.Trabajo;

public class MotorTrabajos {
    private final BlockingQueue<Trabajo> colaTrabajos;
    private final ExecutorService poolTrabajadores;
    private final GestorEstados gestorEstados;

  
    private final Semaphore permisos;

    private final AtomicInteger enCola = new AtomicInteger(0);
    private final AtomicInteger trabajadoresActivos = new AtomicInteger(0);
    private final Queue<Future<Boolean>> futuros = new ConcurrentLinkedQueue<>();
    private final Object candado = new Object();
    private final Thread despachador;
    private volatile boolean activo;

    public MotorTrabajos(int capacidadCola, int numHilosPool) {
        this(capacidadCola, numHilosPool, new GestorEstados());
    }

    public MotorTrabajos(int capacidadCola, int numHilosPool, GestorEstados gestorEstados) {
        this.colaTrabajos = new ArrayBlockingQueue<>(capacidadCola);
        this.poolTrabajadores = Executors.newFixedThreadPool(numHilosPool);
        this.gestorEstados = gestorEstados;
        this.permisos = new Semaphore(numHilosPool);
        this.activo = true;
        this.despachador = new Thread(() -> despachar());
        this.despachador.start();
    }
    
    public boolean agregarTrabajo(Trabajo trabajo) throws InterruptedException {
        synchronized (candado) {
            if (!activo) {
                return false;
            }
            gestorEstados.registrar(trabajo);
            enCola.incrementAndGet();
            try {
                colaTrabajos.put(trabajo);
            } catch (InterruptedException e) {
                enCola.decrementAndGet();
                gestorEstados.descartar(trabajo);
                throw e;
            }
            return true;
        }
    }

    public Trabajo obtenerSiguienteTrabajo() throws InterruptedException {
        return colaTrabajos.take();
    }

    private void despachar() {
        try {
            while (activo || enCola.get() > 0) {
                permisos.acquire();           
                final Trabajo t = colaTrabajos.poll();
                if (t == null) {            
                    permisos.release();
                    Thread.sleep(10);
                    continue;
                }
                enCola.decrementAndGet();
                Callable<Boolean> tarea = () -> {
                    trabajadoresActivos.incrementAndGet();
                    try {
                        return new HiloTrabajador(t, gestorEstados).call();
                    } finally {
                        trabajadoresActivos.decrementAndGet();
                        permisos.release();
                    }
                };
                Future<Boolean> f = poolTrabajadores.submit(tarea);
                futuros.add(f);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean cerrar(long timeoutSegundos) throws InterruptedException {
        synchronized (candado) {
            activo = false;   
        }

        despachador.join(timeoutSegundos * 1000);
        boolean limpio = !despachador.isAlive();
        if (!limpio) {
            despachador.interrupt();
            despachador.join();
        }

        poolTrabajadores.shutdown();
        limpio = poolTrabajadores.awaitTermination(timeoutSegundos, TimeUnit.SECONDS) && limpio;

        if (!limpio) {
            Trabajo t = colaTrabajos.poll();
            while (t != null) {
                enCola.decrementAndGet();
                gestorEstados.cancelarTrabajo(t);
                t = colaTrabajos.poll();
            }
            poolTrabajadores.shutdownNow();
            poolTrabajadores.awaitTermination(2, TimeUnit.SECONDS);
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

    public int getTrabajosEnEspera()        { return enCola.get(); }
    public int getTrabajadoresActivos()     { return trabajadoresActivos.get(); }
    public boolean estaActivo()             { return activo; }
    public GestorEstados getGestorEstados() { return gestorEstados; }
    public Queue<Future<Boolean>> getFuturos() { return futuros; }
}