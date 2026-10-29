package controlador;

public interface AccionesSistema {
	
	String cargarCsv(String ruta);

	String anadirSolicitud(String linea);

	String consultarEstado(String id);

	String verEstadisticas();

	boolean cancelar(String id);

	void cerrar();
}
