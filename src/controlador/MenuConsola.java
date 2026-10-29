package controlador;

import java.util.Scanner;

public class MenuConsola {
	private AccionesSistema sistema;
	private Scanner teclado = new Scanner(System.in);

	public MenuConsola(AccionesSistema sistema) {
		this.sistema = sistema;
	}

	public void mostrarMenu() {
		System.out.println();
		System.out.println("-TaskFlow-");
		System.out.println("1. Cargar CSV de solicitudes");
		System.out.println("2. Añadir solicitud");
		System.out.println("3. Ver estado de un trabajo");
		System.out.println("4. Ver estadísticas");
		System.out.println("6. Cerrar");
		System.out.print("Elige una opción: ");
	}

	public boolean elegirOpcion() {
		mostrarMenu();
		String opcion = teclado.nextLine();

		switch (opcion) {
		case "1":
			System.out.print("Ruta del CSV: ");
			System.out.println(sistema.cargarCsv(teclado.nextLine()));
			break;
		case "2":
			System.out.print("Id: ");
			String nuevoId = teclado.nextLine();
			System.out.print("Operación: ");
			String operacion = teclado.nextLine();
			System.out.print("Ruta: ");
			String ruta = teclado.nextLine();
			System.out.print("Parámetros: ");
			String parametros = teclado.nextLine();
			System.out.println(sistema.anadirSolicitud(nuevoId + ";" + operacion + ";" + ruta + ";" + parametros));
			break;
		case "3":
			System.out.print("Id del trabajo: ");
			System.out.println(sistema.consultarEstado(teclado.nextLine()));
			break;
		case "4":
			System.out.println(sistema.verEstadisticas());
			break;
		case "6":
			System.out.println("Cerrando TaskFlow...");
			sistema.cerrar();
			return false;
		default:
			System.out.println("Opción no válida");
		}
		return true;
	}
}
