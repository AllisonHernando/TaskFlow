-Prueba 1 - Ejecucion normal-

Hecha por: Allison

-Datos de entrada-

- data/solicitudes_ejemplo.csv (5 solicitudes bien)
- data/allison_texto.txt y data/allison_numeros.csv
- Para la consola uso SistemaFalso, que tiene 4 trabajos pendientes: T1, T2, T3 y T4

-Que he hecho-

1. Ejecutar PruebaConsola.
2. Poner 4 para ver las estadisticas.
3. Esperar sin escribir y ver que el trabajador va haciendo los trabajos.
4. Poner 3 y T1 para ver como esta.
5. Poner 2 y añadir una solicitud nueva.
6. Poner 4 otra vez y luego 6 para cerrar.
7. Ejecutar PruebaCsv para ver que las 5 solicitudes del ejemplo se aceptan.

-Que tendria que pasar-

- La consola sigue funcionando mientras el trabajador hace los trabajos.
- Cada trabajo termina en un solo estado (COMPLETADO).
- La solicitud que añado a mano se queda PENDIENTE.
- PruebaCsv acepta las 5 del ejemplo y no rechaza ninguna.

-Que ha pasado-

- Con el menu esperando en "Elige una opción:" fueron saliendo los mensajes del trabajador
  haciendo T1, T2, T3 y T4. O sea que la consola no bloquea al trabajador.
- Al final las estadisticas daban:
  Pendientes: 1 | En proceso: 0 | Completados: 4 | Cancelados: 0
  El pendiente es la que añadi a mano.
- PruebaCsv: Aceptadas: 5, Rechazadas: 0.

Sale lo que tenia que salir.

-Falta-

Cuando este todo junto en el programa principal hay que repetirla: cargar
solicitudes_ejemplo.csv con la opcion 1, esperar a que acaben los 5 y mirar que todos
terminan en un estado final y salen en el CSV de resultados.