package pruebas;

import controlador.ComandoConsola;
import controlador.MenuConsola;

public class PruebaConsola {

	public static void main(String[] args) throws InterruptedException {
		SistemaFalso sistema = new SistemaFalso();
		
		// un hilo para el trabajador y otro para la consola
		Thread trabajador = new Thread(sistema, "trabajador");
		Thread consola = new Thread(new ComandoConsola(new MenuConsola(sistema)), "consola");

		trabajador.start();
		consola.start();
		
		// espera a que se cierre la consola con la opcion 6
		consola.join();
		
		// paramos el trabajador si no ha terminado
		trabajador.interrupt();
		trabajador.join();
		System.out.println("Fin de la prueba");
		System.out.println(sistema.verEstadisticas());
	}
}