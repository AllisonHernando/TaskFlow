package procesohijo;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class AnalizadorEstadisticasCSV {

    private static final String PATRON_NUMERO = "[-+]?\\d+(\\.\\d+)?([eE][-+]?\\d+)?";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: AnalizadorEstadisticasCSV <ruta.csv>");
            System.exit(2);
        }

        Path ruta = Paths.get(args[0]);
        long cantidad = 0;
        long descartados = 0;
        double suma = 0;
        double minimo = Double.POSITIVE_INFINITY;
        double maximo = Double.NEGATIVE_INFINITY;

        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (!linea.matches(PATRON_NUMERO)) {
                    descartados++;
                    continue;
                }
                double valor = Double.parseDouble(linea);
                if (Double.isInfinite(valor)) {
                    descartados++;
                    continue;
                }
                cantidad++;
                suma += valor;
                minimo = Math.min(minimo, valor);
                maximo = Math.max(maximo, valor);
            }
        } catch (IOException e) {
            System.err.println("No se puede leer el archivo '" + args[0] + "': " + e.getMessage());
            System.exit(1);
        }

        if (cantidad == 0) {
            System.err.println("El archivo no contiene ningun registro valido (descartados: " + descartados + ")");
            System.exit(3);
        }

        double media = suma / cantidad;
        System.out.println(String.format(Locale.US, "OK;%d;%.4f;%.4f;%.4f;%.4f;%d",
                cantidad, suma, media, minimo, maximo, descartados));
        
    }
}
