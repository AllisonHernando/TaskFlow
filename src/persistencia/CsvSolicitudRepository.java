package persistencia;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import exception.PersistenciaException;
import modelo.Solicitud;

public class CsvSolicitudRepository implements SolicitudRepository {

	private static final String cabecera = "id;operacion;ruta;parametros";

	@Override
	public List<FilaSolicitud> leer(Path archivo) throws PersistenciaException {
		List<FilaSolicitud> filas = new ArrayList<>();

		try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
			String linea;
			int numeroLinea = 0;

			while ((linea = lector.readLine()) != null) {
				numeroLinea++;

				if (linea.isBlank()) {
					continue;
				}

				if (numeroLinea == 1 && linea.trim().equalsIgnoreCase(cabecera)) {
					continue;
				}

				try {
					Solicitud solicitud = SolicitudMapper.convertir(linea);
					filas.add(new FilaSolicitud(numeroLinea, solicitud, null));
				} catch (IllegalArgumentException e) {
					filas.add(new FilaSolicitud(numeroLinea, null, "CSV mal formado: " + e.getMessage()));
				}
			}
		} catch (IOException e) {
			throw new PersistenciaException("No se pudo leer el CSV '" + archivo + "': " + e.getMessage(), e);
		}

		return filas;
	}
}