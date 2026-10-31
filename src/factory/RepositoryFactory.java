package factory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import config.ConfigManager;

public class RepositoryFactory<T> {

    private final Map<String, Supplier<? extends T>>
            proveedores = new HashMap<>();

    public void registrar(String tipo, Supplier<? extends T> proveedor) {

        if (tipo == null || tipo.trim().isEmpty()
                || proveedor == null) {
            throw new IllegalArgumentException(
                "El proveedor no es valido");
        }
        proveedores.put(tipo.trim().toUpperCase(Locale.ROOT), proveedor);
    }

    public T crear(String tipo) {

        if (tipo == null) {
            throw new IllegalArgumentException("No se ha indicado el tipo de repositorio");
        }

        String nombre = tipo.trim().toUpperCase(Locale.ROOT);

        Supplier<? extends T> proveedor=proveedores.get(nombre);

        if (proveedor == null) {
            throw new IllegalArgumentException(
                "No se ha registrado el repositorio " + tipo);
        }

        return proveedor.get();
    }

    public T crearConfigurado() {

        String tipo = ConfigManager.obtener(
            "repositorio.tipo", "CSV"
        );

        return crear(tipo);
    }
}