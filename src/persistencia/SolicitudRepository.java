package persistencia;

import java.nio.file.Path;
import java.util.List;

import exception.PersistenciaException;
import modelo.Solicitud;

public interface SolicitudRepository {
	List<FilaSolicitud> leer(Path archivo) throws PersistenciaException;

	class FilaSolicitud {
		private final int numeroLinea;
		private final Solicitud solicitud;
		private final String error;

		public FilaSolicitud(int numeroLinea, Solicitud solicitud, String error) {
			this.numeroLinea = numeroLinea;
			this.solicitud = solicitud;
			this.error = error;
		}

		public int getNumeroLinea() {
			return numeroLinea;
		}

		public Solicitud getSolicitud() {
			return solicitud;
		}

		public String getError() {
			return error;
		}

		public boolean esValidaEstructuralmente() {
			return solicitud != null;
		}
	}
}