package pruebas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import processors.LanzadorProcesoHijo;
import processors.LanzadorProcesoHijo.ResultadoHijo;

public class PruebaLanzador {

    public static void main(String[] args) throws Exception {
        LanzadorProcesoHijo lanzador = new LanzadorProcesoHijo();

        ResultadoHijo bueno = lanzador.ejecutar("data/ventas.csv");
        mostrar("CSV correcto", bueno);
        comprobar("CSV correcto: codigo 0 y linea OK", bueno.esCorrecto() && bueno.salida().startsWith("OK;3;"));

        ResultadoHijo sinValidos = lanzador.ejecutar("data/ventas_mal.csv");
        mostrar("CSV sin valores validos", sinValidos);
        comprobar("CSV sin valores validos: codigo 3", sinValidos.codigoSalida() == 3);

        ResultadoHijo inexistente = lanzador.ejecutar("data/no_existe.csv");
        mostrar("Archivo inexistente", inexistente);
        comprobar("Archivo inexistente: codigo 1", inexistente.codigoSalida() == 1);

        ResultadoHijo lento = lanzador.ejecutarClase("pruebas.HijoLento", 3);
        mostrar("Hijo lento con tiempo maximo de 3 s", lento);
        comprobar("Hijo lento: tiempo agotado", lento.tiempoAgotado());

        pruebaSemaforo(lanzador);
        System.out.println("\nRevisa en el Administrador de tareas que no queda ningun proceso java colgado.");
    }

    private static void pruebaSemaforo(LanzadorProcesoHijo lanzador) throws Exception {
        System.out.println("\n--- Semaforo: 6 ejecuciones a la vez, hijo de 1 s ---");
        LanzadorProcesoHijo.reiniciarMaximo();
        ExecutorService pool = Executors.newFixedThreadPool(6);
        List<Future<ResultadoHijo>> futuros = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            futuros.add(pool.submit(() -> lanzador.ejecutarClase("pruebas.HijoLento", 10, "1")));
        }
        boolean todosCorrectos = true;
        for (Future<ResultadoHijo> futuro : futuros) {
            todosCorrectos &= futuro.get().esCorrecto();
        }
        pool.shutdown();

        int maximo = LanzadorProcesoHijo.getMaximoSimultaneos();
        System.out.println("Maximo de procesos hijos a la vez: " + maximo);
        comprobar("Las 6 ejecuciones terminaron bien", todosCorrectos);
        comprobar("Nunca mas de 2 procesos a la vez", maximo <= LanzadorProcesoHijo.MAX_PROCESOS_SIMULTANEOS);
    }

    private static void mostrar(String titulo, ResultadoHijo r) {
        System.out.println("\n--- " + titulo + " ---");
        System.out.println("codigo=" + r.codigoSalida() + " tiempoAgotado=" + r.tiempoAgotado()
                + " falloArranque=" + r.falloArranque());
        System.out.println("salida : " + r.salida());
        System.out.println("errores: " + r.errores());
    }

    private static void comprobar(String descripcion, boolean ok) {
        System.out.println((ok ? "[OK]    " : "[FALLO] ") + descripcion);
    }
}
