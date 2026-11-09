package controlador;

public class ComandoConsola implements Runnable {
	private MenuConsola menu;
	// hilo de la consola, para poder usar el menu mientras se procesan los trabajos
	public ComandoConsola(MenuConsola menu) {
		this.menu = menu;
	}

	@Override
	public void run() {
		boolean seguir = true;
		while (seguir) {
			// se repite hasta que en el menu se elige cerrar
			seguir = menu.elegirOpcion();
		}
		System.out.println("Consola cerrada");
	}
}