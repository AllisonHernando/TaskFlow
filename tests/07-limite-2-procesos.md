------- Prueba 7: Limite de recursos (maximo 2 procesos hijos)

------- Objetivo
Comprobar que nunca hay mas de 2 procesos hijos a la vez.

------- Datos de entrada
6 ejecuciones a la vez sobre un hijo que tarda 1 segundo (en PruebaLanzador) y, con Main, data/solicitudes_prueba7.csv (20 solicitudes).

------- Procedimiento
Se puede hacer de dos formas. Vale cualquiera de las dos.

Opcion A (sin Main, el lanzador directamente):
1. Ejecutar la clase pruebas.PruebaLanzador (Run As, Java Application).
2. Mirar la parte del semaforo: "Maximo de procesos hijos a la vez: 2".

Opcion B (programa completo, cuando Main funcione):
1. Ejecutar la clase controlador.Main (Run As, Java Application).
2. Opcion 1 del menu: cargar data/solicitudes_prueba7.csv.
3. Mirar en el Administrador de tareas cuantos java.exe hay a la vez.

------- Resultado esperado
Sale "Maximo de procesos hijos a la vez: 2" y [OK] en "Nunca mas de 2 procesos a la vez". Con Main, nunca mas de 2 java.exe a la vez y los 20 trabajos COMPLETADOS.

------- Resultado obtenido
Ejecutado con la opcion A (PruebaLanzador), sin Main, que aun no esta terminado.
Maximo de procesos hijos a la vez: 2
[OK]    Las 6 ejecuciones terminaron bien
[OK]    Nunca mas de 2 procesos a la vez

Pendiente de repetir con la opcion B cuando Main funcione.