package persistencia;

import java.util.ArrayList;
import java.util.List;

import modelo.Solicitud;

public final class SolicitudMapper {
	private SolicitudMapper() {
	}

	public static Solicitud convertir(String linea) {
		List<String> campos = separar(linea);

		if (campos.size() != 4) {
			throw new IllegalArgumentException("Se esperaban 4 campos y se encontraron " + campos.size());
		}

		return new Solicitud(campos.get(0).trim(), campos.get(1).trim(), campos.get(2).trim(), campos.get(3).trim());
	}

	private static List<String> separar(String linea) {
		List<String> campos = new ArrayList<>();
		StringBuilder campo = new StringBuilder();
		boolean dentroDeComillas = false;
		boolean comillasCerradas = false;

		for (int i = 0; i < linea.length(); i++) {
			char caracter = linea.charAt(i);

			if (dentroDeComillas) {
				if (caracter == '"') {
					if (i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
						campo.append('"');
						i++;
					} else {
						dentroDeComillas = false;
						comillasCerradas = true;
					}
				} else {
					campo.append(caracter);
				}
				continue;
			}

			if (comillasCerradas) {
				if (caracter == ';') {
					campos.add(campo.toString());
					campo.setLength(0);
					comillasCerradas = false;
				} else {
					throw new IllegalArgumentException("Hay caracteres después de cerrar un campo entre comillas");
				}
				continue;
			}

			if (caracter == ';') {
				campos.add(campo.toString());
				campo.setLength(0);
			} else if (caracter == '"') {
				if (campo.length() != 0) {
					throw new IllegalArgumentException("Las comillas solo pueden comenzar un campo");
				}
				dentroDeComillas = true;
			} else {
				campo.append(caracter);
			}
		}

		if (dentroDeComillas) {
			throw new IllegalArgumentException("Hay comillas sin cerrar");
		}

		campos.add(campo.toString());
		return campos;
	}
}