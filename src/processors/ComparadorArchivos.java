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
    public String ejecutar(Solicitud solicitud) throws Exception {

        if (solicitud == null || solicitud.getRuta() == null
                || solicitud.getRuta().trim().isEmpty()) {
            return "ERROR: no se han indicado archivos";
        }

        String[] rutas = solicitud.getRuta().split("\\|", -1);

        if (rutas.length != 2 || rutas[0].trim().isEmpty() || rutas[1].trim().isEmpty()) {
            return "No se han indicado donde estan los archivos";
        }

        Path archivoA;
        Path archivoB;

        try {
            archivoA = Paths.get(rutas[0].trim());
            archivoB = Paths.get(rutas[1].trim());

        } catch (InvalidPathException e) {
            return "La ruta no es valida" + e.getMessage();
        }

        if (!Files.isRegularFile(archivoA)) {
            return "No se han encontrado archivos " + archivoA;
        }

        if (!Files.isRegularFile(archivoB)) {
            return "No se han encontrado archivos  " + archivoB;
        }

        try (
           BufferedReader lectorA=Files.newBufferedReader(archivoA, StandardCharsets.UTF_8);
            BufferedReader lectorB=Files.newBufferedReader(archivoB, StandardCharsets.UTF_8)
        ) {
            int numeroLinea = 1;

            while (true) {
                String lineaA = lectorA.readLine();
                String lineaB = lectorB.readLine();

                if (lineaA == null && lineaB == null) {
                    return "Los archivos son iguales";
                }

                if (!Objects.equals(lineaA, lineaB)) {
                    return "Hay difrenecias en la linea-> " + numeroLinea;
                }
                numeroLinea++;
            }

        } catch (IOException e) {
            return "No se han podido leer los archivos " + e.getMessage();
        }
    }
}