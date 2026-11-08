package pruebas;

import java.util.HashMap;

import controlador.AccionesSistema;
import modelo.EstadoTrabajo;

public class SistemaFalso implements AccionesSistema, Runnable {
	//Buscar estado de un trabajo por su id por eso usé hashMap
	private HashMap<String, EstadoTrabajo> trabajos = new HashMap<>();
	private boolean cerrado = false;

	public SistemaFalso() {
		trabajos.put("T1", EstadoTrabajo.PENDIENTE);
		trabajos.put("T2", EstadoTrabajo.PENDIENTE);
		trabajos.put("T3", EstadoTrabajo.PENDIENTE);
		trabajos.put("T4", EstadoTrabajo.PENDIENTE);
	}

	@Override
	public void run() {
		String[] orden = { "T1", "T2", "T3", "T4" };
		try {
			for (String id : orden) {
				//Le digo que espere para que de tiempo a interactuar con el menú
				Thread.sleep(4000);

				synchronized (this) {
					if (cerrado) {
						return;
					}
					// si lo han cancelado lo salta
					if (trabajos.get(id) != EstadoTrabajo.PENDIENTE) {
						System.out.println("[trabajador] " + id + " no se ejecuta, está " + trabajos.get(id));
						continue;
					}
					trabajos.put(id, EstadoTrabajo.EN_PROCESO);
				}
				System.out.println("[trabajador] procesando " + id + "...");

				Thread.sleep(3000);

				synchronized (this) {
					trabajos.put(id, EstadoTrabajo.COMPLETADO);
				}
				System.out.println("[trabajador] " + id + " completado");
			}
		} catch (InterruptedException e) {
			System.out.println("[trabajador] interrumpido, paro");
		}
	}

	@Override
	public String cargarCsv(String ruta) {
		return "(falso) Se cargaría el CSV " + ruta;
	}

	@Override
	public synchronized String anadirSolicitud(String linea) {
		String[] partes = linea.split(";");
		String id = partes[0].trim();
		if (id.isEmpty()) {
			return "Rechazada: el id está vacío";
		}
		if (trabajos.containsKey(id)) {
			return "Rechazada: el id " + id + " ya existe";
		}
		trabajos.put(id, EstadoTrabajo.PENDIENTE);
		return "Solicitud " + id + " aceptada";
	}

	@Override
	public synchronized String consultarEstado(String id) {
		EstadoTrabajo estado = trabajos.get(id);
		if (estado == null) {
			return "No existe el trabajo " + id;
		}
		return id + ": " + estado;
	}
	// cuenta cuantos trabajos hay en cada estado
	@Override
	public synchronized String verEstadisticas() {
		int pendientes = 0, enProceso = 0, completados = 0, cancelados = 0;
		for (EstadoTrabajo estado : trabajos.values()) {
			if (estado == EstadoTrabajo.PENDIENTE)
				pendientes++;
			if (estado == EstadoTrabajo.EN_PROCESO)
				enProceso++;
			if (estado == EstadoTrabajo.COMPLETADO)
				completados++;
			if (estado == EstadoTrabajo.CANCELADO)
				cancelados++;
		}
		return "Pendientes: " + pendientes + " | En proceso: " + enProceso + " | Completados: " + completados
				+ " | Cancelados: " + cancelados;
	}
	// solo se cancela si todavia no ha empezado
	@Override
	public synchronized boolean cancelar(String id) {
		if (trabajos.get(id) == EstadoTrabajo.PENDIENTE) {
			trabajos.put(id, EstadoTrabajo.CANCELADO);
			return true;
		}
		return false;
	}

	@Override
	public synchronized void cerrar() {
		cerrado = true;
	}
}