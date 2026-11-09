package processors;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import modelo.Solicitud;

public class AnalizadorTexto implements OperacionStrategy {

    @Override
    public String ejecutar(Solicitud solicitud) throws IOException {
        if (solicitud == null || solicitud.getRuta() == null || solicitud.getRuta().trim().isEmpty()) {
            throw new IllegalArgumentException("No se ha indicado una ruta aceptable");
        }

        Path ruta;
        try {
            ruta = Paths.get(solicitud.getRuta().trim());
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("La ruta no es valida: " + solicitud.getRuta(), e);
        }

        if (!Files.isRegularFile(ruta)) {
            throw new IOException("No existe el archivo: " + ruta);
        }
        if (!Files.isReadable(ruta)) {
            throw new IOException("No se puede leer el archivo: " + ruta);
        }

        long inicio = System.nanoTime();
        int lineas = 0;
        int palabras = 0;
        int caracteres = 0;

        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                lineas++;
                caracteres += linea.length();
                if (!linea.trim().isEmpty()) {
                    palabras += linea.trim().split("\\s+").length;
                }
            }
        }

        double tiempoMs = (System.nanoTime() - inicio) / 1_000_000.0;
        return "Lineas: " + lineas
                + "\nPalabras: " + palabras
                + "\nCaracteres: " + caracteres
                + "\nTiempo: " + String.format(Locale.ROOT, "%.3f", tiempoMs) + " ms";
    }
}
