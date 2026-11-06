package controlador;

public class ComandoConsola implements Runnable {
	private MenuConsola menu;

	public ComandoConsola(MenuConsola menu) {
		this.menu = menu;
	}

	@Override
	public void run() {
		boolean seguir = true;
		while (seguir) {
			seguir = menu.elegirOpcion();
		}
		System.out.println("Consola cerrada");
	}
}