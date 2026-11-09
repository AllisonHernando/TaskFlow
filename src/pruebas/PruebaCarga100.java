package pruebas;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import modelo.Solicitud;
import persistencia.CsvSolicitudRepository;
import service.InputService;
import service.ValidadorSolicitudes;

public class PruebaCarga100 {

    public static void main(String[] args) throws Exception {
        Path archivo = Path.of("data", "solicitudes_100.csv");

        InputService servicio = new InputService(
                new CsvSolicitudRepository(),
                new ValidadorSolicitudes());

        List<Solicitud> enviadas = new ArrayList<>();

        InputService.InformeCarga informe =
                servicio.cargar(archivo, Set.of(), enviadas::add);

        System.out.println("Archivo: " + archivo);
        System.out.println("Solicitudes aceptadas: " + informe.getAceptadas());
        System.out.println("Solicitudes enviadas al callback: " + enviadas.size());
        System.out.println("Errores: " + informe.getErrores().size());

        Set<String> ids = new HashSet<>();
        for (Solicitud solicitud : enviadas) {
            ids.add(solicitud.getId());
        }
        System.out.println("IDs únicos: " + ids.size());

        for (String error : informe.getErrores()) {
            System.out.println("ERROR: " + error);
        }

        if (informe.getAceptadas() == 100
                && enviadas.size() == 100
                && ids.size() == 100
                && informe.getErrores().isEmpty()) {
            System.out.println("PRUEBA SUPERADA: las 100 solicitudes son válidas.");
        } else {
            System.out.println("PRUEBA NO SUPERADA: revisa los errores anteriores.");
            throw new AssertionError("El CSV no produjo 100 solicitudes válidas y únicas.");
        }
    }
}