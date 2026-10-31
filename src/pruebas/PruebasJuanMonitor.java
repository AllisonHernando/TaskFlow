package pruebas;

import java.util.LinkedHashMap;
import java.util.Map;

import config.ConfigManager;
import monitor.MonitorSistema;

public class PruebasJuanMonitor {

    public static void main(String[] args) throws InterruptedException {

        Map<String, Integer> estados = new LinkedHashMap<>();

        estados.put("PENDIENTE", 3);
        estados.put("EN_PROCESO", 2);
        estados.put("COMPLETADO", 8);

        int intervalo=ConfigManager.obtenerEnteroPositivo("monitor.intervalo.segundos", 5);

        MonitorSistema monitor = new MonitorSistema(
            () -> new MonitorSistema.Datos(3, 2, estados)
        );

        try {
            System.out.println("Iniciando");
            monitor.iniciar(intervalo);
            Thread.sleep((2L * intervalo + 1) * 1000);

        } finally {
            monitor.cerrar();
        }

        System.out.println("Monitor cerrado");

        Thread.sleep((intervalo + 1L) * 1000);

        System.out.println("Prueba terminada");
    }
}