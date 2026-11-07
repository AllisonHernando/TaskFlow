package pruebas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;

import modelo.Solicitud;
import persistencia.SolicitudMapper;
import service.ValidadorSolicitudes;

public class PruebaCsv {

	public static void main(String[] args) throws Exception {
		probar("data/solicitudes_ejemplo.csv");
		probar("data/solicitudes_100.csv");
		probar("data/solicitudes_100_con_errores.csv");
	}

	public static void probar(String ruta) throws Exception {
		System.out.println("=== " + ruta);

		ValidadorSolicitudes validador = new ValidadorSolicitudes();
		HashSet<String> ids = new HashSet<>();
		int aceptadas = 0;
		int rechazadas = 0;
		int numLinea = 0;

		BufferedReader lector = new BufferedReader(new FileReader(ruta));
		String linea = lector.readLine();

		while (linea != null) {
			numLinea++;

			if (numLinea > 1 && !linea.isBlank()) {
				try {
					Solicitud solicitud = SolicitudMapper.convertir(linea);
					String error = validador.validar(solicitud, ids);

					if (error == null) {
						ids.add(solicitud.getId());
						aceptadas++;
					} else {
						System.out.println("  Línea " + numLinea + ": " + error);
						rechazadas++;
					}
				} catch (IllegalArgumentException e) {
					System.out.println("  Línea " + numLinea + ": mal formada, " + e.getMessage());
					rechazadas++;
				}
			}
			linea = lector.readLine();
		}
		lector.close();

		System.out.println("Aceptadas: " + aceptadas);
		System.out.println("Rechazadas: " + rechazadas);
		System.out.println();
	}
}
