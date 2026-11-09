package persistencia;

import java.nio.file.Path;
import java.util.Collection;

import exception.PersistenciaException;
import modelo.ResultadoTrabajo;

public interface ResultadoRepository {
	void guardar(Path archivo, Collection<ResultadoTrabajo> resultados) throws PersistenciaException;
}