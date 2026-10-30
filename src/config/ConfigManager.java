package config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigManager {

    private static final Properties propiedades = new Properties();

    static {
        try (InputStream archivo = Files.newInputStream(Paths.get("config", "app.properties"))) {
            propiedades.load(archivo);

        } catch (IOException e) {
            System.err.println("No ha podido cargarse el archivo app.propierties " + e.getMessage());
        }
    }

    public static String obtener(String clave, String valorPorDefecto) {String valor = propiedades.getProperty(clave);
        if (valor==null || valor.trim().isEmpty()) {
            return valorPorDefecto;
        }
        return valor.trim();
    }

    public static int obtenerEnteroPositivo(String clave, int valorPorDefecto) {String valor = obtener(clave, String.valueOf(valorPorDefecto));
        try {
            int numero = Integer.parseInt(valor);

            if (numero > 0) {
                return numero;
            }
        } catch (NumberFormatException e) {}
        return valorPorDefecto;
    }
}