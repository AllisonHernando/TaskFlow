package service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

import modelo.Solicitud;

public class ValidadorSolicitudes {

	private static final Set<String> operaciones_admitidas = Set.of("ANALIZAR_TEXTO", "BUSCAR_TEXTO",
			"ESTADISTICAS_CSV", "CALCULAR_HASH", "COMPARAR_ARCHIVOS");

	public String validar(Solicitud solicitud, Set<String> idsYaRegistrados) {
		if (solicitud == null) {
			return "La solicitud está vacía";
		}

		String id = limpiar(solicitud.getId());
		String operacion = limpiar(solicitud.getOperacion());
		String ruta = limpiar(solicitud.getRuta());

		if (id.isEmpty()) {
			return "Falta el identificador";
		}

		if (idsYaRegistrados.contains(id)) {
			return "El identificador ya existe: " + id;
		}

		if (!operaciones_admitidas.contains(operacion.toUpperCase(Locale.ROOT))) {
			return "Operación desconocida: " + operacion;
		}

		if (ruta.isEmpty()) {
			return "Falta la ruta del recurso";
		}

		// BUSCAR_TEXTO y COMPARAR_ARCHIVOS pueden llevar varios archivos separados por
		// |
		for (String parte : ruta.split("\\|", -1)) {
			String rutaArchivo = parte.trim();
			if (rutaArchivo.isEmpty()) {
				return "La ruta no es válida: " + ruta;
			}
			Path archivo;
			try {
				archivo = Path.of(rutaArchivo);
			} catch (RuntimeException e) {
				return "La ruta no es válida: " + rutaArchivo;
			}
			if (!Files.isRegularFile(archivo) || !Files.isReadable(archivo)) {
				return "El recurso no existe o no se puede leer: " + rutaArchivo;
			}
		}

		if (solicitud.getParametros() == null) {
			return "Los parámetros no pueden ser nulos";
		}

		return null;
	}

	private String limpiar(String valor) {
		return valor == null ? "" : valor.trim();
	}
}