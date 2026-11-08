package pruebas;

import java.util.Collections;

import core.GestorEstados;
import core.MotorTrabajos;
import monitor.MonitorSistema;
import service.CierreService;

public class PruebaCierre {

	public static void main(String[] args) throws InterruptedException {
		MotorTrabajos motor = new MotorTrabajos(20, 4);

		MonitorSistema monitor = new MonitorSistema(() -> new MonitorSistema.Datos(motor.getTrabajosEnEspera(),
				motor.getTrabajadoresActivos(), Collections.emptyMap()));

		monitor.iniciar(60);

		for (int i = 1; i <= 20; i++) {
			boolean agregado = motor.agregarTrabajo(new modelo.Trabajo("T" + i));

			if (!agregado) {
				throw new AssertionError("No se pudo agregar el trabajo T" + i);
			}
		}

		CierreService cierre = new CierreService(motor, monitor);
		cierre.cerrar();

		GestorEstados gestor = motor.getGestorEstados();

		comprobar(gestor.balanceCuadra(), "El balance de trabajos cuadra");
		comprobar(gestor.getPendientes() == 0, "No quedan trabajos pendientes");
		comprobar(gestor.getEnProceso() == 0, "No quedan trabajos en proceso");
		comprobar(motor.getTrabajosEnEspera() == 0, "La cola está vacia");
		comprobar(motor.getTrabajadoresActivos() == 0, "No quedan trabajadores activos");

	}

	private static void comprobar(boolean correcto, String mensaje) {
		if (!correcto) {
			throw new AssertionError("FALLO: " + mensaje);
		}

		System.out.println("OK: " + mensaje);
	}
}