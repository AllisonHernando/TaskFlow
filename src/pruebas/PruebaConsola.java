package pruebas;

import controlador.ComandoConsola;
import controlador.MenuConsola;

public class PruebaConsola {

	public static void main(String[] args) throws InterruptedException {
		SistemaFalso sistema = new SistemaFalso();

		Thread trabajador = new Thread(sistema, "trabajador");
		Thread consola = new Thread(new ComandoConsola(new MenuConsola(sistema)), "consola");

		trabajador.start();
		consola.start();

		consola.join();

		trabajador.interrupt();
		trabajador.join();
		System.out.println("Fin de la prueba");
		System.out.println(sistema.verEstadisticas());
	}
}