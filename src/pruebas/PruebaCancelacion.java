package pruebas;
public class PruebaCancelacion {
	// prueba 6: si cancelo un trabajo antes de que empiece, el trabajador no lo hace
	public static void main(String[] args) throws InterruptedException {
		SistemaFalso sistema = new SistemaFalso();
		Thread trabajador = new Thread(sistema, "trabajador");
		
		//Aquí empieza
		trabajador.start();
		
		//Se cancela t3 porque el trabajador aun no ha llegado
		System.out.println("Cancelar T3: " + sistema.cancelar("T3"));
		
		//Esperamos y empieza con t1
		Thread.sleep(5000);
		System.out.println(sistema.consultarEstado("T1"));
		
		//Intenta cancelar t1 pero no puede porque ya esta en proceso
		System.out.println("Cancelar T1: " + sistema.cancelar("T1"));

		//Intenta cancelar un trabajo que no existe por lo cual no puede
		System.out.println("Cancelar T99: " + sistema.cancelar("T99"));
		
		//Espera
		trabajador.join();

		System.out.println();
		System.out.println("Estados finales:");
		System.out.println(sistema.consultarEstado("T1"));
		System.out.println(sistema.consultarEstado("T2"));
		System.out.println(sistema.consultarEstado("T3"));
		System.out.println(sistema.consultarEstado("T4"));
		System.out.println(sistema.verEstadisticas());

		System.out.println();
		System.out.println("Resultado esperado: T3 CANCELADO, los demás COMPLETADO,");
		System.out.println("Cancelar T3 true, Cancelar T1 false y Cancelar T99 false");
	}
}