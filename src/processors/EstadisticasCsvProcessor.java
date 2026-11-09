package processors;

import java.util.Locale;

import modelo.Solicitud;
import processors.LanzadorProcesoHijo.ResultadoHijo;

public class EstadisticasCsvProcessor implements OperacionStrategy {

	// el record es como una manera más limpia de de los constructores sin necesidad de escribirlos, en cristiano, forma avanzada de hacer los getters, setters, tostring 
    public record EstadisticasCsv(long cantidad, double suma, double media,
                                  double minimo, double maximo, long descartados) {
    }

    private final LanzadorProcesoHijo lanzador;

    public EstadisticasCsvProcessor() {
        this(new LanzadorProcesoHijo());
    }

    public EstadisticasCsvProcessor(LanzadorProcesoHijo lanzador) {
        this.lanzador = lanzador;
    }

    // Metodo que llama el motor de TaskFlow. Si algo falla lanza una excepcion que frena el proceos
    @Override
    public String ejecutar(Solicitud solicitud) throws Exception {
        EstadisticasCsv e = ejecutar(solicitud.getRuta());
        return String.format(Locale.US,
                "Cantidad: %d, Suma: %.4f, Media: %.4f, Minimo: %.4f, Maximo: %.4f, Descartados: %d",
                e.cantidad(), e.suma(), e.media(), e.minimo(), e.maximo(), e.descartados());
    } 

    // el IllegalStateException es por si llega a fallar el hijo, una exception de toda la vida vaya, pero que frena todo para que no siga el proceso
    public EstadisticasCsv ejecutar(String rutaCsv) throws InterruptedException {
        ResultadoHijo resultado = lanzador.ejecutar(rutaCsv);

        if (resultado.falloArranque() || resultado.tiempoAgotado()) {
            throw new IllegalStateException(resultado.errores());
        }
        if (resultado.codigoSalida() != 0) {
            throw new IllegalStateException("El proceso hijo termino con codigo "
                    + resultado.codigoSalida() + ": " + resultado.errores());
        }
        return interpretar(resultado.salida());
    }

    private static EstadisticasCsv interpretar(String salida) {
        String linea = salida.trim();
        String[] partes = linea.split(";");
        if (partes.length != 7 || !partes[0].equals("OK")) {
            throw new IllegalStateException("Salida inesperada del proceso hijo: '" + linea + "'");
        }
        try {
            return new EstadisticasCsv(
                    Long.parseLong(partes[1]),
                    Double.parseDouble(partes[2]),
                    Double.parseDouble(partes[3]),
                    Double.parseDouble(partes[4]),
                    Double.parseDouble(partes[5]),
                    Long.parseLong(partes[6]));
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Salida del proceso hijo con numeros incorrectos: '" + linea + "'", e);
        }
    }
}
