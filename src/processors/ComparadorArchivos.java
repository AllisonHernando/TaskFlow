package processors;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import modelo.Solicitud;

public class ComparadorArchivos implements OperacionStrategy {

    @Override
    public String ejecutar(Solicitud solicitud) throws IOException {
        if (solicitud == null || solicitud.getRuta() == null || solicitud.getRuta().trim().isEmpty()) {
            throw new IllegalArgumentException("No se han indicado archivos");
        }

        String[] rutas = solicitud.getRuta().split("\\|", -1);
        if (rutas.length != 2 || rutas[0].trim().isEmpty() || rutas[1].trim().isEmpty()) {
            throw new IllegalArgumentException("Hay que indicar exactamente dos rutas separadas por | ");
        }

        Path archivoA;
        Path archivoB;
        try {
            archivoA = Paths.get(rutas[0].trim());
            archivoB = Paths.get(rutas[1].trim());
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("La ruta no es valida", e);
        }

        if (!Files.isRegularFile(archivoA) || !Files.isReadable(archivoA)) {
            throw new IOException("No existe o no se puede leer el archivo: " + archivoA);
        }
        if (!Files.isRegularFile(archivoB) || !Files.isReadable(archivoB)) {
            throw new IOException("No existe o no se puede leer el archivo: " + archivoB);
        }

        try (BufferedReader lectorA = Files.newBufferedReader(archivoA, StandardCharsets.UTF_8);
             BufferedReader lectorB = Files.newBufferedReader(archivoB, StandardCharsets.UTF_8)) {
            int numeroLinea = 1;
            while (true) {
                String lineaA = lectorA.readLine();
                String lineaB = lectorB.readLine();
                if (lineaA == null && lineaB == null) {
                    return "Los archivos son iguales";
                }
                if (!Objects.equals(lineaA, lineaB)) {
                    return "Hay diferencias en la linea: " + numeroLinea;
                }
                numeroLinea++;
            }
        }
    }
}
