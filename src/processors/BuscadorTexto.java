package processors;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import modelo.Solicitud;

public class BuscadorTexto {

    public String ejecutar(Solicitud solicitud) {
        if (solicitud == null) {
            return "ERROR: solicitud nula";
        }

        String textoBuscado=solicitud.getParametros();

        if (textoBuscado == null || textoBuscado.trim().isEmpty()) {
            return "No hay texto para buscar";
        }

        String rutas= solicitud.getRuta();

        if (rutas == null || rutas.trim().isEmpty()) {
            return "No hay archivos";
        }

        String[] archivos = rutas.split("\\|");
        StringBuilder resultado = new StringBuilder();

        resultado.append("Texto buscado: ")
                 .append(textoBuscado)
                 .append("\n");

        for (String archivo : archivos) {
            String nombre = archivo.trim();

            Path ruta;

            try {
                ruta = Paths.get(nombre);
            } catch (InvalidPathException e) {
                resultado.append(nombre)
                         .append("La ruta no es valida\n");
                continue;
            }

            if (!Files.isRegularFile(ruta)) {
                resultado.append(nombre)
                         .append("No se ha encontrado ningun archivo\n");
                continue;
            }

            int coincidencias = 0;

            try (BufferedReader lector=Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {

                String linea;

                while ((linea = lector.readLine()) != null) {

                    int posicion = 0;
                    int indice;

                    while ((indice=linea.indexOf(textoBuscado, posicion)) != -1) {
                        coincidencias++;
                        posicion = indice + textoBuscado.length();
                    }
                }

                resultado.append(nombre)
                         .append(": ")
                         .append(coincidencias)
                         .append(" coincidencias\n");

            } catch (IOException e) {
                resultado.append(nombre)
                         .append("No se ha podido leer el archivo")
                         .append(e.getMessage())
                         .append("\n");
            }
        }

        return resultado.toString();
    }
}