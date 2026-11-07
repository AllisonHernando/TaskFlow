package processors;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import modelo.Solicitud;

public class BuscadorTexto implements OperacionStrategy {

    @Override
    public String ejecutar(Solicitud solicitud) throws IOException {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }

        String textoBuscado = solicitud.getParametros();
        if (textoBuscado == null || textoBuscado.trim().isEmpty()) {
            throw new IllegalArgumentException("No hay texto para buscar");
        }

        String rutas = solicitud.getRuta();
        if (rutas == null || rutas.trim().isEmpty()) {
            throw new IllegalArgumentException("No hay archivos para buscar");
        }

        // -1 permite detectar una ruta vacia al final: "archivo.txt|"
        String[] archivos = rutas.split("\\|", -1);
        StringBuilder resultado = new StringBuilder("Texto buscado: ").append(textoBuscado).append('\n');

        for (String archivo : archivos) {
            String nombre = archivo.trim();
            if (nombre.isEmpty()) {
                throw new IllegalArgumentException("Hay una ruta vacia en la lista de archivos");
            }

            Path ruta;
            try {
                ruta = Paths.get(nombre);
            } catch (InvalidPathException e) {
                throw new IllegalArgumentException("La ruta no es valida: " + nombre, e);
            }
            if (!Files.isRegularFile(ruta)) {
                throw new IOException("No existe el archivo: " + ruta);
            }
            if (!Files.isReadable(ruta)) {
                throw new IOException("No se puede leer el archivo: " + ruta);
            }

            int coincidencias = 0;
            try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    int posicion = 0;
                    int indice;
                    while ((indice = linea.indexOf(textoBuscado, posicion)) != -1) {
                        coincidencias++;
                        posicion = indice + textoBuscado.length();
                    }
                }
            }
            resultado.append(nombre).append(": ").append(coincidencias)
                    .append(coincidencias == 1 ? " coincidencia\n" : " coincidencias\n");
        }

        return resultado.toString();
    }
}
