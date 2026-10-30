package pruebas;

import config.ConfigManager;

public class PruebasJuanConfig {

    public static void main(String[] args) {
        int capacidad = ConfigManager.obtenerEnteroPositivo("cola.capacidad", 10);
        int trabajadores = ConfigManager.obtenerEnteroPositivo("trabajadores", 1);
        int intervalo = ConfigManager.obtenerEnteroPositivo("monitor.intervalo.segundos", 1);
        String repositorio = ConfigManager.obtener("repositorio.tipo", "MEMORIA");
        String directorio = ConfigManager.obtener("resultados.directorio", "salida");

        System.out.println("Capacidad de cola: " + capacidad);
        System.out.println("Trabajadores: " + trabajadores);
        System.out.println("Intervalo monitor: " + intervalo);
        System.out.println("Tipo repositorio: " + repositorio);
        System.out.println("Directorio resultados: " + directorio);
    }
}