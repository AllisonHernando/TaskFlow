------- Prueba 2: 100 solicitudes

------- Objetivo
Comprobar que se pueden leer y validar 100 solicitudes de un CSV, que se envían
al callback y que sus identificadores son únicos.

------- Datos de entrada
data/solicitudes_100.csv (100 solicitudes).

------- Procedimiento
1. Ejecutar la clase pruebas.PruebaCarga100 (Run As, Java Application).
2. Comprobar el número de solicitudes aceptadas y enviadas al callback.
3. Comprobar que no hay errores y que los 100 identificadores son únicos.

------- Resultado esperado
100 solicitudes aceptadas, 100 enviadas al callback, 0 errores y 100 IDs únicos.
La prueba debe mostrar "PRUEBA SUPERADA".

------- Resultado obtenido
Solicitudes aceptadas: 100
Solicitudes enviadas al callback: 100
Errores: 0
IDs únicos: 100
PRUEBA SUPERADA: las 100 solicitudes son válidas.

Esta prueba comprueba la lectura y validación del CSV; no ejecuta los trabajos
en el motor.