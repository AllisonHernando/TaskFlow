------- Prueba 5: Error del proceso hijo

------- Objetivo
Comprobar que si el proceso hijo falla, el fallo queda registrado y el sistema sigue funcionando.

------- Datos de entrada
data/ventas_mal.csv (sin numeros validos), data/no_existe.csv (no existe) y data/ventas.csv (correcto).

------- Procedimiento
Se puede hacer de dos formas. Vale cualquiera de las dos.

Opcion A (sin Main, el hijo directamente):
1. Ejecutar la clase pruebas.PruebaLanzador (Run As, Java Application).
2. Mirar las lineas con codigo 3 (ventas_mal.csv) y codigo 1 (no_existe.csv).

Opcion B (programa completo, cuando Main funcione):
1. Ejecutar la clase controlador.Main (Run As, Java Application).
2. Opcion 1 del menu: cargar data/solicitudes_prueba5.csv.
3. Opcion 3 del menu: ver el estado de cada trabajo.

------- Resultado esperado
ventas_mal.csv: el hijo termina con codigo 3. no_existe.csv: codigo 1. ventas.csv: codigo 0. Con Main, los trabajos con error quedan FALLIDOS y el correcto COMPLETADO.

------- Resultado obtenido
Ejecutado con la opcion A (PruebaLanzador), sin Main, que aun no esta terminado.
[OK]    CSV sin valores validos: codigo 3
[OK]    Archivo inexistente: codigo 1
[OK]    Hijo lento: tiempo agotado

Pendiente de repetir con la opcion B cuando Main funcione.