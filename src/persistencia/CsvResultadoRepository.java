package persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import exception.PersistenciaException;
import modelo.ResultadoTrabajo;

public class CsvResultadoRepository implements ResultadoRepository {

	@Override
	public void guardar(Path archivo, Collection<ResultadoTrabajo> resultados) throws PersistenciaException {

		Path archivoAbsoluto = archivo.toAbsolutePath();
		Path directorio = archivoAbsoluto.getParent();
		Path temporal = null;

		try {
			Files.createDirectories(directorio);
			temporal = Files.createTempFile(directorio, "resultados-", ".tmp");

			List<String> lineas = new ArrayList<>();
			lineas.add("idTrabajo;exito;mensajeResumen;tiempoEjecucionMs");

			for (ResultadoTrabajo resultado : resultados) {
				lineas.add(campo(resultado.getIdTrabajo()) + ";" + resultado.isExito() + ";"
						+ campo(resultado.getMensajeResumen()) + ";" + resultado.getTiempoEjecucionMs());
			}

			Files.write(temporal, lineas, StandardCharsets.UTF_8);

			try {
				Files.move(temporal, archivoAbsoluto, StandardCopyOption.REPLACE_EXISTING,
						StandardCopyOption.ATOMIC_MOVE);
			} catch (AtomicMoveNotSupportedException e) {
				Files.move(temporal, archivoAbsoluto, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new PersistenciaException(
					"No se pudieron guardar los resultados en '" + archivo + "': " + e.getMessage(), e);
		} finally {
			if (temporal != null) {
				try {
					Files.deleteIfExists(temporal);
				} catch (IOException e) {
					System.err.println("No se pudo borrar el archivo temporal '" + temporal + "': " + e.getMessage());
				}
			}
		}
	}

	private String campo(String valor) {
		if (valor == null) {
			return "";
		}

		boolean necesitaComillas = valor.contains(";") || valor.contains("\"") || valor.contains("\n")
				|| valor.contains("\r");

		if (!necesitaComillas) {
			return valor;
		}

		return "\"" + valor.replace("\"", "\"\"") + "\"";
	}
}