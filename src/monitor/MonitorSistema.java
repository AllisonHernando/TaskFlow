package monitor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class MonitorSistema implements AutoCloseable {
    public static class Datos {
        private final int espera;
        private final int ocupados;
        private final Map<String, Integer> estados;

        public Datos(int espera, int ocupados, Map<String, Integer> estados) {
            this.espera = espera;
            this.ocupados = ocupados;
            this.estados = new LinkedHashMap<>(estados);
        }
    }

    private final Supplier<Datos> proveedor;
    private final ScheduledExecutorService servicio=Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> tarea;

    public MonitorSistema(Supplier<Datos> proveedor) {
        this.proveedor = proveedor;
    }

    public synchronized void iniciar(int intervaloSegundos) {

        if (intervaloSegundos <= 0) {
            throw new IllegalArgumentException(
                "El intervalo tiene que ser positivo");
        }

        if (servicio.isShutdown()) {
            throw new IllegalStateException(
                "El monitor esta cerrado");
        }

        if (tarea != null && !tarea.isDone()) {
            return;
        }

        tarea=servicio.scheduleAtFixedRate(() -> {

            try {
                Datos datos=proveedor.get();

                System.out.println("El monitor espera =" + datos.espera
                    + "; ocupados=" + datos.ocupados
                );

                System.out.println(
                    "Estados del monitor= " + datos.estados
                );

            } catch (RuntimeException e) {
                System.err.println(
                    "Ha habido un error en el monitor" + e.getMessage());
            }

        }, 0, intervaloSegundos, TimeUnit.SECONDS);
    }

    public synchronized void detener() {
        if (tarea != null) {
            tarea.cancel(false);
            tarea = null;
        }
    }

    public synchronized void cerrar() {
        detener();
        servicio.shutdownNow();
    }

    @Override
    public void close() {
        cerrar();
    }
}