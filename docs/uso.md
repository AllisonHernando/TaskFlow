-Como usar la consola-

El menu va en un hilo aparte (ComandoConsola), asi que puedes ir escribiendo opciones
mientras los trabajadores siguen haciendo trabajos. Se ejecuta en Eclipse con Run As ->
Java Application, desde la carpeta del proyecto para que encuentre las rutas de data/.

Mientras no este todo junto se puede probar con PruebaConsola, que usa SistemaFalso.
Tiene 4 trabajos (T1, T2, T3 y T4) y un trabajador que los va haciendo poco a poco

-Menu-

-TaskFlow-
1. Cargar CSV de solicitudes
2. Añadir solicitud
3. Ver estado de un trabajo
4. Ver estadísticas
5. Cancelar un trabajo
6. Cerrar

1 -> te pide la ruta del CSV y carga las solicitudes
2 -> te pide id, operacion, ruta y parametros, uno detras de otro
3 -> te pide un id y te dice en que estado esta
4 -> muestra cuantos trabajos hay en cada estado
5 -> te pide un id y lo cancela si todavia no ha empezado
6 -> cierra el programa

Si pones otra cosa sale "Opción no válida" y vuelve a salir el menu.

-Ejemplos-

Cargar el CSV de ejemplo:
1
data/solicitudes_ejemplo.csv

Añadir una solicitud (si no hay parametros das a Intro):
2
N001
BUSCAR_TEXTO
data/allison_texto.txt
TaskFlow

Ver como va un trabajo:
3
T1
y sale algo tipo "T1: EN_PROCESO"

Cancelar:
5
T4
Si T4 no habia empezado sale "Trabajo T4 cancelado". Si ya estaba empezado, terminado o no
existe sale "No se ha podido cancelar T4 (no existe o ya ha empezado)".

-Cosas a tener en cuenta-

- A veces los mensajes del trabajador salen en medio del menu. Es normal, son dos hilos
  escribiendo en la misma consola.
- La consola no cambia el estado de los trabajos, solo le pide al sistema que cancele. El
  sistema lo comprueba con synchronized para que no pase que un trabajo empiece y se cancele
  a la vez.
- Mientras espera a que escribas, el hilo de la consola esta parado en nextLine() y no gasta
  CPU.