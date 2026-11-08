package controlador;

import java.util.LinkedHashMap;
import java.util.Map;

import config.ConfigManager;
import core.GestorEstados;
import core.MotorTrabajos;
import modelo.TaskFlow;
import monitor.MonitorSistema;
import persistencia.CsvSolicitudRepository;
import service.CierreService;
import service.InputService;
import service.ValidadorSolicitudes;

public class Main {

	public static void main(String[] args) {
		int capacidadCola = ConfigManager.obtenerEnteroPositivo("cola.capacidad", 20);
		int numeroTrabajadores = ConfigManager.obtenerEnteroPositivo("trabajadores", 4);
		int intervaloMonitor = ConfigManager.obtenerEnteroPositivo("monitor.intervalo.segundos", 5);

		GestorEstados gestorEstados = new GestorEstados();
		MotorTrabajos motor = new MotorTrabajos(capacidadCola, numeroTrabajadores, gestorEstados);

		MonitorSistema monitor = new MonitorSistema(() -> {
			Map<String, Integer> estados = new LinkedHashMap<>();
			estados.put("PENDIENTE", gestorEstados.getPendientes());
			estados.put("EN_PROCESO", gestorEstados.getEnProceso());
			estados.put("COMPLETADO", gestorEstados.getCompletados());
			estados.put("FALLIDO", gestorEstados.getFallidos());
			estados.put("CANCELADO", gestorEstados.getCancelados());

			return new MonitorSistema.Datos(motor.getTrabajosEnEspera(), motor.getTrabajadoresActivos(), estados);
		});

		monitor.iniciar(intervaloMonitor);

		ValidadorSolicitudes validador = new ValidadorSolicitudes();
		InputService inputService = new InputService(new CsvSolicitudRepository(), validador);
		CierreService cierreService = new CierreService(motor, monitor);

		TaskFlow sistema = new TaskFlow(inputService, validador, motor, cierreService);

		MenuConsola menu = new MenuConsola(sistema);

		try {
			while (menu.elegirOpcion()) {
				// El menú continúa hasta que el usuario elige la opción 6.
			}
		} finally {
			sistema.cerrar();
		}
	}
}