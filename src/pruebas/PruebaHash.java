package pruebas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import modelo.Solicitud;
import processors.CalculadorHashProcessor;

public class PruebaHash {

    private static final String hashVacio =
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

    public static void main(String[] args) throws Exception {
        CalculadorHashProcessor calculador = new CalculadorHashProcessor();

        Path vacio = Files.createTempFile("taskflow", ".txt");
        try {
            comprobar("Hash de archivo vacio correcto", calculador.ejecutar(vacio.toString()).equals(hashVacio));
        } finally {
            Files.deleteIfExists(vacio);
        }

        System.out.println("Hash de data/hola.txt: " + calculador.ejecutar("data/hola.txt"));

        String texto = calculador.ejecutar(new Solicitud("Solicitud1", "CALCULAR_HASH", "data/hola.txt", ""));
        System.out.println(texto);
        comprobar("via OperacionStrategy empieza por SHA-256 de", texto.startsWith("SHA-256 de data/hola.txt: ")); 

        try {
            calculador.ejecutar(new Solicitud("Solicitud2", "CALCULAR_HASH", "data/no_existe.txt", ""));
            comprobar("Archivo inexistente deberia fallar", false);
        } catch (IOException e) {
            comprobar("Archivo inexistente lanza IOException", true);
        }
    }

    private static void comprobar(String descripcion, boolean ok) {
        System.out.println((ok ? "[OK]    " : "[FALLO] ") + descripcion);
    }
}
