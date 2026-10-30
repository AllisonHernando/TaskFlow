package processors;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import modelo.Solicitud;

public class AnalizadorTexto {

    public String ejecutar(Solicitud solicitud) {
        if (solicitud == null || solicitud.getRuta() == null || solicitud.getRuta().trim().isEmpty()) {
            return "No se ha indicado una ruta aceptable";
        }

        Path ruta = Paths.get(solicitud.getRuta());

        if (!Files.isRegularFile(ruta)) {
            return "No existe el archivo " + ruta;
        }

        long inicio = System.nanoTime();
        int lineas = 0;
        int palabras = 0;
        int caracteres = 0;

        try (BufferedReader lector=Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                lineas++;
                caracteres += linea.length();
                
                if (!linea.trim().isEmpty()) {
                    palabras += linea.trim().split("\\s+").length;
                }
            }

        } catch (IOException e) {
            return "No se ha podido leer el archivo " + e.getMessage();
        }

        long fin = System.nanoTime();
        double tiempoMs = (fin - inicio) / 1_000_000.0;

        return "Lineas: " + lineas
                + "\nPalabras: " + palabras
                + "\nCaracteres: " + caracteres
                + "\nTiempo: " + String.format(Locale.ROOT, "%.3f", tiempoMs)
                + " ms";
    }
}