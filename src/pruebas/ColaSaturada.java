
package pruebas;

import core.GestorEstados;
import core.MotorTrabajos;
import modelo.Trabajo;

public class ColaSaturada {

    public static void main(String[] args) {

        System.out.println("Iniciando prueba de cola saturada");

        try {

            GestorEstados gestor = new GestorEstados();
            MotorTrabajos motor = new MotorTrabajos(5, 4, gestor);

            int aceptados = 0;
            int rechazados = 0;

            System.out.println("Introduciendo 30 trabajos");

            for (int i = 1; i <= 30; i++) {

                Trabajo trabajo = new Trabajo(
                    "TRABAJO" + String.format("%03d", i)
                );

                boolean aceptado = motor.agregarTrabajo(trabajo);

                if (aceptado) {
                    aceptados++;
                } else {
                    rechazados++;
                }

                System.out.println("Añadido " + trabajo.getId()
                    + " aceptado=" + aceptado
                    + " | en cola=" + motor.getTrabajosEnEspera()
                    + " | activos=" + motor.getTrabajadoresActivos());
            }

            System.out.println("Trabajos aceptados: " + aceptados);
            System.out.println("Trabajos rechazados: " + rechazados);

            System.out.println("Cerrando motor...");

            boolean limpio = motor.cerrar(30);

            System.out.println("Cierre limpio: " + limpio);
            System.out.println(gestor.resumen());

            System.out.println("Balance cuadra: "
                + gestor.balanceCuadra());

            if (limpio && gestor.getAceptados() == aceptados
                    && gestor.getCompletados() == aceptados
                    && gestor.balanceCuadra()) {

                System.out.println("PRUEBA SUPERADA");

            } else {

                System.out.println("PRUEBA FALLIDA");
            }

        } catch (Exception e) {

            System.err.println("Error durante la prueba: "
                + e.getMessage());

            e.printStackTrace();
        }
    }
}
