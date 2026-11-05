package pruebas;

import core.GestorEstados;
import core.MotorTrabajos;
import core.HiloTrabajador;
import modelo.Trabajo;

public class ColaSaturada {
    public static void main(String[] args) {
        System.out.println("Iniciando Prueba");

        try {
            MotorTrabajos motor = new MotorTrabajos(20, 4);
            GestorEstados gestor = new GestorEstados();

            System.out.println("Generando e introduciendo 30 trabajos en la cola");

            for (int i = 1; i <= 30; i++) {
                Trabajo trabajo = new Trabajo("TRABAJO" + String.format("%03d", i));
                motor.agregarTrabajo(trabajo);
                System.out.println("Añadido a la cola: " + trabajo.getId());
            }

            System.out.println("Los 30 trabajos han sido introducidos. Esperando procesamiento.");

            Thread.sleep(5000);

            motor.apagarMotor();
            System.out.println("Prueba terminada");

        } catch (Exception e) {
            System.err.println("Error durante la prueba de 30 trabajos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
