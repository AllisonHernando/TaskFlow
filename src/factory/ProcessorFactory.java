package factory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import processors.AnalizadorTexto;
import processors.BuscadorTexto;
import processors.OperacionStrategy;

public class ProcessorFactory {

    private final Map<String, Supplier<? extends OperacionStrategy>>
            procesadores = new HashMap<>();

    public ProcessorFactory() {
        registrar("ANALIZAR_TEXTO", AnalizadorTexto::new);

        registrar("BUSCAR_TEXTO", BuscadorTexto::new);
    }

    public void registrar(String nombre, Supplier<? extends OperacionStrategy> proveedor) {
        if (nombre == null || nombre.trim().isEmpty()
                || proveedor == null) {
            throw new IllegalArgumentException(
                "No es valido el registro");
        }

        procesadores.put(nombre.trim().toUpperCase(Locale.ROOT), proveedor);
    }

    public OperacionStrategy crear(String operacion) {
        if (operacion == null) {
            throw new IllegalArgumentException(
                "No se ha indicado ninguna operacion");
        }

        String nombre=operacion.trim().toUpperCase(Locale.ROOT);

        Supplier<? extends OperacionStrategy> proveedor=procesadores.get(nombre);

        if (proveedor == null) {
            throw new IllegalArgumentException(
                "La operacion no es reconocida: " + operacion);
        }

        return proveedor.get();
    }
}