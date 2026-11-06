package pruebas;
public class PruebaCancelacion {

	public static void main(String[] args) throws InterruptedException {
		SistemaFalso sistema = new SistemaFalso();
		Thread trabajador = new Thread(sistema, "trabajador");
		trabajador.start();

		System.out.println("Cancelar T3: " + sistema.cancelar("T3"));

		Thread.sleep(5000);
		System.out.println(sistema.consultarEstado("T1"));
		System.out.println("Cancelar T1: " + sistema.cancelar("T1"));

		System.out.println("Cancelar T99: " + sistema.cancelar("T99"));

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