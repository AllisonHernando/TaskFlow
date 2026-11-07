package pruebas;

import java.io.IOException;
import factory.ProcessorFactory;
import modelo.Solicitud;
import processors.OperacionStrategy;

public class PruebasJuanArchivoDatosIncorrectos {
    private static final ProcessorFactory factory = new ProcessorFactory();
    private static int realizadas = 0;
    private static int correctas = 0;

    public static void main(String[] args) {
        comprobarError("Analizar: archivo inexistente",
                solicitud("04-1", "ANALIZAR_TEXTO", "data/no_existe_04.txt", ""), IOException.class);
        comprobarError("Analizar: ruta vacia",
                solicitud("04-2", "ANALIZAR_TEXTO", "", ""), IllegalArgumentException.class);
        comprobarError("Analizar: ruta mal formada",
                solicitud("04-3", "ANALIZAR_TEXTO", "data/\u0000archivo.txt", ""), IllegalArgumentException.class);
        comprobarCorrecto("Analizar: archivo correcto despues de errores",
                solicitud("04-4", "ANALIZAR_TEXTO", "data/hola.txt", ""), "Lineas: 2", "Palabras: 5");

        comprobarError("Buscar: texto vacio",
                solicitud("04-5", "BUSCAR_TEXTO", "data/hola.txt", ""), IllegalArgumentException.class);
        comprobarError("Buscar: uno de los archivos no existe",
                solicitud("04-6", "BUSCAR_TEXTO", "data/no_existe_04.txt|data/hola.txt", "hola"), IOException.class);
        comprobarError("Buscar: ruta vacia al final",
                solicitud("04-7", "BUSCAR_TEXTO", "data/hola.txt|", "hola"), IllegalArgumentException.class);
        comprobarCorrecto("Buscar: archivo correcto despues de errores",
                solicitud("04-8", "BUSCAR_TEXTO", "data/hola.txt", "hola"), "5 coincidencias");

        comprobarError("Comparar: segundo archivo inexistente",
                solicitud("04-9", "COMPARAR_ARCHIVOS", "data/hola.txt|data/no_existe_04.txt", ""), IOException.class);
        comprobarError("Comparar: falta una segunda ruta",
                solicitud("04-10", "COMPARAR_ARCHIVOS", "data/hola.txt", ""), IllegalArgumentException.class);
        comprobarCorrecto("Comparar: dos archivos iguales despues de errores",
                solicitud("04-11", "COMPARAR_ARCHIVOS", "data/hola.txt|data/hola.txt", ""), "Los archivos son iguales");
        comprobarCorrecto("Comparar: archivos diferentes",
                solicitud("04-12", "COMPARAR_ARCHIVOS", "data/hola.txt|data/pruebaTextoJuan.txt", ""),
                "Hay diferencias en la linea: 1");

        System.out.printf("%nRESULTADO PRUEBA 04 (procesadores): %d/%d correctos%n", correctas, realizadas);
        System.out.println("Pendiente: comprobar los estados FALLIDO/COMPLETADO con el motor integrado.");
        if (correctas != realizadas) {
            throw new AssertionError("La prueba 04 tiene casos fallidos");
        }
    }

    private static Solicitud solicitud(String id, String operacion, String ruta, String parametros) {
        return new Solicitud(id, operacion, ruta, parametros);
    }

    private static void comprobarError(String nombre, Solicitud solicitud, Class<? extends Exception> esperada) {
        realizadas++;
        try {
            OperacionStrategy procesador = factory.crear(solicitud.getOperacion());
            String resultado = procesador.ejecutar(solicitud);
            System.out.println("[FALLO] " + nombre + ": devolvio resultado en vez de fallar: " + resultado);
        } catch (Exception e) {
            if (esperada.isInstance(e)) {
                correctas++;
                System.out.println("[OK] " + nombre + " (" + e.getClass().getSimpleName() + ")");
            } else {
                System.out.println("[FALLO] " + nombre + ": excepcion inesperada " + e);
            }
        }
    }

    private static void comprobarCorrecto(String nombre, Solicitud solicitud, String... esperados) {
        realizadas++;
        try {
            String resultado = factory.crear(solicitud.getOperacion()).ejecutar(solicitud);
            boolean correcto = resultado != null;
            for (String esperado : esperados) {
                correcto &= resultado != null && resultado.contains(esperado);
            }
            if (correcto) correctas++;
            System.out.println((correcto ? "[OK] " : "[FALLO] ") + nombre);
            if (!correcto) System.out.println("       Obtenido: " + resultado);
        } catch (Exception e) {
            System.out.println("[FALLO] " + nombre + ": " + e);
        }
    }
}
