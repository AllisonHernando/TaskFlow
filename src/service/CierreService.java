package service;

import core.GestorEstados;
import core.MotorTrabajos;
import monitor.MonitorSistema;

public class CierreService {

	private final MotorTrabajos motor;
	private final MonitorSistema monitor;
	private boolean cerrado;

	public CierreService(MotorTrabajos motor, MonitorSistema monitor) {
		this.motor = motor;
		this.monitor = monitor;
		this.cerrado = false;
	}

	public synchronized void cerrar() throws InterruptedException {
		if (cerrado) {
			return;
		}

		monitor.cerrar();

		boolean termino;
		try {
			termino = motor.cerrar(30);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw e;
		}

		if (!termino) {
			throw new IllegalStateException("El motor no termino");
		}

		GestorEstados gestor = motor.getGestorEstados();

		if (gestor.getPendientes() != 0 || gestor.getEnProceso() != 0) {
			throw new IllegalStateException("Quedan trabajos sin terminar: " + gestor.resumen());
		}

		if (!gestor.balanceCuadra()) {
			throw new IllegalStateException("El balance no cuadra: " + gestor.resumen());
		}

		System.out.println("Cierre correcto: " + gestor.resumen());
		cerrado = true;
	}
}