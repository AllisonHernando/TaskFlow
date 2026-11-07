-Prueba 6 - Cancelar un trabajo pendiente-

Hecha por: Allison

-Datos de entrada-

SistemaFalso con 4 trabajos pendientes (T1, T2, T3 y T4). El trabajador los hace en orden,
uno detras de otro.

-Que he hecho-

1. Ejecutar PruebaCancelacion.
2. Nada mas arrancar el trabajador se cancela T3, que todavia no ha empezado.
3. A los 5 segundos, cuando T1 ya esta EN_PROCESO, se intenta cancelar T1.
4. Se intenta cancelar T99, que no existe.
5. Se espera a que el trabajador acabe y se sacan los estados finales.

Tambien lo probe a mano con PruebaConsola: opcion 5 con T4 antes de que el trabajador
llegue a el, y luego opcion 3 con T4 para ver que esta CANCELADO.

-Que tendria que pasar-

- Cancelar T3 da true, T3 se queda CANCELADO y el trabajador no lo hace.
- Cancelar T1 da false porque ya habia empezado.
- Cancelar T99 da false porque no existe.
- T1, T2 y T4 acaban COMPLETADO.

-Que ha pasado-

Cancelar T3: true
[trabajador] procesando T1...
T1: EN_PROCESO
Cancelar T1: false
Cancelar T99: false
[trabajador] T1 completado
[trabajador] procesando T2...
[trabajador] T2 completado
[trabajador] T3 no se ejecuta, está CANCELADO
[trabajador] procesando T4...
[trabajador] T4 completado

Estados finales:
T1: COMPLETADO
T2: COMPLETADO
T3: CANCELADO
T4: COMPLETADO
Pendientes: 0 | En proceso: 0 | Completados: 3 | Cancelados: 1

Sale bien, el trabajo cancelado no se llega a hacer.

-Falta-

Repetirla con el programa entero: dejar trabajos en la cola, cancelar uno con la opcion 5 y
mirar que no sale como hecho en el CSV de resultados.