package pruebas;

import modelo.Solicitud;
import processors.EstadisticasCsvProcessor;
import processors.EstadisticasCsvProcessor.EstadisticasCsv;

public class PruebaEstadisticas {

    private interface Accion {
        void ejecutar() throws Exception;
    }

    public static void main(String[] args) throws Exception {
        EstadisticasCsvProcessor procesador = new EstadisticasCsvProcessor();

        EstadisticasCsv r = procesador.ejecutar("data/ventas.csv");
        System.out.println(r);
        comprobar("cantidad = 3", r.cantidad() == 3);
        comprobar("suma = 60.5", Math.abs(r.suma() - 60.5) < 1e-9);
        comprobar("media = 20.1667", Math.abs(r.media() - 60.5 / 3) < 1e-3);
        comprobar("minimo = 10", r.minimo() == 10);
        comprobar("maximo = 30", r.maximo() == 30);
        comprobar("descartados = 2", r.descartados() == 2);

        // Igual que lo llamara el motor de TaskFlow (a traves de OperacionStrategy), o sea, esto es para simular como bien dice su nombre la carpeta
        String texto = procesador.ejecutar(new Solicitud("S1", "ESTADISTICAS_CSV", "data/ventas.csv", ""));
        System.out.println(texto);
        comprobar("via OperacionStrategy empieza por Cantidad: 3", texto.startsWith("Cantidad: 3,"));

        esperarFallo("Archivo inexistente", () -> procesador.ejecutar("data/no_existe.csv"));
        esperarFallo("CSV sin valores validos", () -> procesador.ejecutar("data/ventas_mal.csv"));
        esperarFallo("Solicitud con archivo inexistente",
                () -> procesador.ejecutar(new Solicitud("S2", "ESTADISTICAS_CSV", "data/no_existe.csv", "")));
    }

    private static void esperarFallo(String descripcion, Accion accion) throws Exception {
        try {
            accion.ejecutar();
            System.out.println("[FALLO] " + descripcion + ": deberia haber fallado");
        } catch (IllegalStateException e) {
            System.out.println("[OK]    " + descripcion + " -> " + e.getMessage());
        }
    }

    private static void comprobar(String descripcion, boolean ok) {
        System.out.println((ok ? "[OK]    " : "[FALLO] ") + descripcion);
    }
}
