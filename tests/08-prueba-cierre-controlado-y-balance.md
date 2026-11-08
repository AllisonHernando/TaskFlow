------- Prueba 8: Cierre controlado y balance

------- Objetivo
Comprobar que el motor termina los trabajos aceptados antes de cerrar y que el
balance de estados cuadra, sin trabajos pendientes ni trabajadores activos.

------- Datos de entrada
20 trabajos añadidos al motor, con 4 trabajadores y una cola de capacidad 20.

------- Procedimiento
1. Ejecutar la clase pruebas.PruebaCierre (Run As, Java Application).
2. La prueba inicia el monitor y añade los 20 trabajos al motor.
3. La prueba solicita el cierre del motor.
4. Comprobar el balance, los trabajos pendientes y en proceso, la cola y los
   trabajadores activos.

------- Resultado esperado
Los 20 trabajos quedan completados. No quedan trabajos pendientes ni en proceso,
la cola está vacía, no quedan trabajadores activos y el balance cuadra.

------- Resultado obtenido
Cierre correcto: aceptados=20 pendientes=0 enProceso=0 completados=20 fallidos=0 cancelados=0
OK: El balance de trabajos cuadra
OK: No quedan trabajos pendientes
OK: No quedan trabajos en proceso
OK: La cola está vacía
OK: No quedan trabajadores activos

Esta prueba comprueba el cierre y el balance. No comprueba la exportación del CSV
de resultados.