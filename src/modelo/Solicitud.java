package modelo;

public class Solicitud {

	private String id;
	private String operacion;
	private String ruta;
	private String parametros;

	public Solicitud(String id, String operacion, String ruta, String parametros) {
		this.id = id;
		this.operacion = operacion;
		this.ruta = ruta;
		this.parametros = parametros;
	}

	public String getId() {
		return id;
	}

	public String getOperacion() {
		return operacion;
	}

	public String getRuta() {
		return ruta;
	}

	public String getParametros() {
		return parametros;
	}

	@Override
	public String toString() {
		return id + ";" + operacion + ";" + ruta + ";" + parametros;
	}
}