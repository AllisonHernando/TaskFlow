package modelo;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import controlador.AccionesSistema;
import core.GestorEstados;
import core.MotorTrabajos;
import exception.PersistenciaException;
import persistencia.CsvResultadoRepository;
import persistencia.SolicitudMapper;
import service.CierreService;
import service.InputService;
import service.InputService.InformeCarga;
import service.ValidadorSolicitudes;

public class TaskFlow implements AccionesSistema {

	private final InputService inputService;
	private final ValidadorSolicitudes validador;
	private final MotorTrabajos motor;
	private final CierreService cierreService;
	private final Map<String, Trabajo> trabajos;

	public TaskFlow(InputService inputService, ValidadorSolicitudes validador, MotorTrabajos motor,
			CierreService cierreService) {
		this.inputService = inputService;
		this.validador = validador;
		this.motor = motor;
		this.cierreService = cierreService;
		this.trabajos = new ConcurrentHashMap<>();
	}

	@Override
	public synchronized String cargarCsv(String ruta) {
		try {
			InformeCarga informe = inputService.cargar(Path.of(ruta), new HashSet<>(trabajos.keySet()),
					this::encolarSolicitud);

			StringBuilder mensaje = new StringBuilder();
			mensaje.append("Solicitudes aceptadas: ").append(informe.getAceptadas());

			for (String error : informe.getErrores()) {
				mensaje.append(System.lineSeparator()).append(error);
			}

			return mensaje.toString();

		} catch (InvalidPathException e) {
			return "La ruta del CSV no es válida: " + ruta;
		} catch (PersistenciaException e) {
			return "No se pudo leer el CSV: " + e.getMessage();
		} catch (ErrorAlEncolarException e) {
			return "No se pudieron cargar todas las solicitudes: " + e.getMessage();
		}
	}

	@Override
	public synchronized String anadirSolicitud(String linea) {
		try {
			Solicitud solicitud = SolicitudMapper.convertir(linea);

			String error = validador.validar(solicitud, new HashSet<>(trabajos.keySet()));

			if (error != null) {
				return "Solicitud rechazada: " + error;
			}

			encolarSolicitud(solicitud);
			return "Solicitud " + solicitud.getId() + " aceptada";

		} catch (IllegalArgumentException e) {
			return "Solicitud mal formada: " + e.getMessage();
		} catch (ErrorAlEncolarException e) {
			return "No se pudo añadir la solicitud: " + e.getMessage();
		}
	}

	@Override
	public String consultarEstado(String id) {
		Trabajo trabajo = trabajos.get(id.trim());

		if (trabajo == null) {
			return "No existe el trabajo " + id;
		}

		return id + ": " + trabajo.getEstado();
	}

	@Override
	public String verEstadisticas() {
		GestorEstados gestor = motor.getGestorEstados();
		return gestor.resumen();
	}

	@Override
	public boolean cancelar(String id) {
		Trabajo trabajo = trabajos.get(id.trim());

		if (trabajo == null) {
			return false;
		}

		return motor.getGestorEstados().cancelarTrabajo(trabajo);
	}

	@Override
	public void cerrar() {
		try {
			// Espera a que termine el motor para recoger los estados finales
			cierreService.cerrar();

			List<ResultadoTrabajo> resultados = new ArrayList<>();

			for (Trabajo trabajo : trabajos.values()) {
				String estado = trabajo.getEstado().name();
				boolean exito = trabajo.getEstado() == EstadoTrabajo.COMPLETADO;

				resultados.add(new ResultadoTrabajo(trabajo.getId(), exito, "Estado final: " + estado, 0));
			}

			Path archivo = Path.of("resultados", "resultados.csv");
			new CsvResultadoRepository().guardar(archivo, resultados);

			System.out.println("Resultados guardados en: " + archivo);

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("El cierre fue interrumpido; no se guardaron los resultados.");
		} catch (PersistenciaException e) {
			System.err.println("No se pudieron guardar los resultados: " + e.getMessage());
		} catch (IllegalStateException e) {
			System.err.println("No se pudo completar el cierre: " + e.getMessage());
		}
	}

	private void encolarSolicitud(Solicitud solicitud) {
		Trabajo trabajo = new Trabajo(solicitud.getId());

		try {
			boolean agregado = motor.agregarTrabajo(trabajo);

			if (!agregado) {
				throw new ErrorAlEncolarException("el motor ya está cerrado");
			}

			trabajos.put(solicitud.getId().trim(), trabajo);

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ErrorAlEncolarException("se interrumpió la espera para añadir el trabajo");
		}
	}

	private static class ErrorAlEncolarException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		ErrorAlEncolarException(String mensaje) {
			super(mensaje);
		}
	}
}