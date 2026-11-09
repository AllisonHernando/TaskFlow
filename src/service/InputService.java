package service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import exception.PersistenciaException;
import modelo.Solicitud;
import persistencia.SolicitudRepository;

public class InputService {

	private final SolicitudRepository repositorio;
	private final ValidadorSolicitudes validador;

	public InputService(SolicitudRepository repositorio, ValidadorSolicitudes validador) {
		this.repositorio = repositorio;
		this.validador = validador;
	}

	public InformeCarga cargar(Path archivo, Set<String> idsYaRegistrados, Consumer<Solicitud> enviarAlMotor)
			throws PersistenciaException {

		List<SolicitudRepository.FilaSolicitud> filas = repositorio.leer(archivo);

		Set<String> idsUsados = new HashSet<>(idsYaRegistrados);
		List<String> errores = new ArrayList<>();
		int aceptadas = 0;

		for (SolicitudRepository.FilaSolicitud fila : filas) {
			if (!fila.esValidaEstructuralmente()) {
				errores.add("Línea " + fila.getNumeroLinea() + ": " + fila.getError());
				continue;
			}

			Solicitud solicitud = fila.getSolicitud();
			String error = validador.validar(solicitud, idsUsados);

			if (error != null) {
				errores.add("Línea " + fila.getNumeroLinea() + ": " + error);
				continue;
			}

			enviarAlMotor.accept(solicitud);
			idsUsados.add(solicitud.getId().trim());
			aceptadas++;
		}

		return new InformeCarga(aceptadas, errores);
	}

	public static class InformeCarga {
		private final int aceptadas;
		private final List<String> errores;

		public InformeCarga(int aceptadas, List<String> errores) {
			this.aceptadas = aceptadas;
			this.errores = List.copyOf(errores);
		}

		public int getAceptadas() {
			return aceptadas;
		}

		public List<String> getErrores() {
			return errores;
		}
	}
}