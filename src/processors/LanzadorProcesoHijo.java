package processors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class LanzadorProcesoHijo {

    public record ResultadoHijo(int codigoSalida, String salida, String errores,
                                boolean tiempoAgotado, boolean falloArranque) {
        public boolean esCorrecto() {
            return !tiempoAgotado && !falloArranque && codigoSalida == 0;
        }
    }

    public static final int MAX_PROCESOS_SIMULTANEOS = 2;
    public static final long TIMEOUT_POR_DEFECTO_SEGUNDOS = 30;
    public static final String CLASE_HIJO = "procesohijo.AnalizadorEstadisticasCSV";

    private static final Semaphore PERMISOS = new Semaphore(MAX_PROCESOS_SIMULTANEOS, true);
    private static final AtomicInteger ACTIVOS = new AtomicInteger();
    private static final AtomicInteger MAXIMO_ACTIVOS = new AtomicInteger();

    public ResultadoHijo ejecutar(String rutaCsv) throws InterruptedException {
        return ejecutar(rutaCsv, TIMEOUT_POR_DEFECTO_SEGUNDOS);
    }

    public ResultadoHijo ejecutar(String rutaCsv, long timeoutSegundos) throws InterruptedException {
        return ejecutarClase(CLASE_HIJO, timeoutSegundos, rutaCsv);
    }

    public ResultadoHijo ejecutarClase(String claseJava, long timeoutSegundos, String... argumentos)
            throws InterruptedException {
        PERMISOS.acquire(); // espera si ya hay 2 procesos hijos
        try {
            int activos = ACTIVOS.incrementAndGet();
            MAXIMO_ACTIVOS.accumulateAndGet(activos, Math::max);
            try {
                return lanzar(claseJava, timeoutSegundos, argumentos);
            } finally {
                ACTIVOS.decrementAndGet();
            }
        } finally {
            PERMISOS.release(); // siempre se libera el permiso
        }
    }

    private ResultadoHijo lanzar(String claseJava, long timeoutSegundos, String... argumentos)
            throws InterruptedException {
        List<String> comando = new ArrayList<>();
        comando.add(Paths.get(System.getProperty("java.home"), "bin", "java").toString());
        comando.add("-cp");
        comando.add(System.getProperty("java.class.path"));
        comando.add(claseJava);
        comando.addAll(Arrays.asList(argumentos));

        final Process proceso;
        try {
            proceso = new ProcessBuilder(comando).start();
        } catch (IOException e) {
            return new ResultadoHijo(-1, "", "No se pudo arrancar el proceso hijo: " + e.getMessage(), false, true);
        }

        ExecutorService lectores = Executors.newFixedThreadPool(2);
        try {
            Future<String> salida = lectores.submit(() -> leer(proceso.getInputStream()));
            Future<String> errores = lectores.submit(() -> leer(proceso.getErrorStream()));
            cerrarEntrada(proceso);

            boolean termino = proceso.waitFor(timeoutSegundos, TimeUnit.SECONDS);
            if (!termino) {
                proceso.destroyForcibly();
                proceso.waitFor(5, TimeUnit.SECONDS);
            }

            String textoSalida = obtener(salida);
            String textoErrores = obtener(errores);

            if (!termino) {
                return new ResultadoHijo(-1, textoSalida,
                        "Tiempo maximo superado (" + timeoutSegundos + " s). " + textoErrores, true, false);
            }
            return new ResultadoHijo(proceso.exitValue(), textoSalida, textoErrores, false, false);
        } finally {
            if (proceso.isAlive()) {
                proceso.destroyForcibly(); // por si nos interrumpen: no dejar procesos huerfanos
            }
            lectores.shutdownNow();
        }
    }

    private static String leer(InputStream flujo) throws IOException {
        StringBuilder texto = new StringBuilder();
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(flujo, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (texto.length() > 0) {
                    texto.append('\n');
                }
                texto.append(linea);
            }
        }
        return texto.toString();
    }

    private static String obtener(Future<String> lectura) throws InterruptedException {
        try {
            return lectura.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException | TimeoutException e) {
            return "(no se pudo leer el flujo del hijo: " + e.getMessage() + ")";
        }
    }

    private static void cerrarEntrada(Process proceso) {
        try {
            proceso.getOutputStream().close();
        } catch (IOException e) {
            
        }
    }

    public static int getMaximoSimultaneos() {
        return MAXIMO_ACTIVOS.get();
    }

    public static void reiniciarMaximo() {
        MAXIMO_ACTIVOS.set(0);
    }
}
