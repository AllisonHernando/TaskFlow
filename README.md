# TaskFlow

Este proyecto es una aplicación de consola en Java desarrollada como trabajo final de la primera evaluación de la asignatura de Programación de Servicios y Procesos (DAM2, curso 2026-2027). Su objetivo es aplicar la programación concurrente con hilos, la sincronización entre ellos y la ejecución de procesos externos, trabajando en equipo con Git y GitHub.

## Integrantes del grupo

- Allison Hernando 
- Nerea Fresnedo 
- Josué Mateo
- Juan Puertas
- Ekaitz Sanchez

## Descripción y funcionalidades

TaskFlow recibe trabajos sobre archivos, los procesa con varios hilos a la vez y guarda el resultado de cada uno. Sus funciones principales son:

- Carga de solicitudes: las solicitudes se cargan desde un CSV o se añaden a mano desde el menú. Las líneas incorrectas se rechazan indicando la línea y el motivo, sin detener la carga.
- Operaciones sobre archivos: análisis de texto (líneas, palabras y caracteres), búsqueda de texto en uno o varios archivos y estadísticas de un CSV de números. Como ampliación, cálculo del hash SHA-256 y comparación de dos archivos.
- Procesamiento concurrente: los trabajos aceptados entran en una cola con capacidad limitada y los procesa un grupo fijo de hilos trabajadores.
- Proceso hijo: las estadísticas del CSV se calculan en un programa Java independiente lanzado con ProcessBuilder, con un máximo de dos procesos a la vez.
- Consola activa: mientras se procesan los trabajos se puede consultar su estado, ver estadísticas, añadir solicitudes y cancelar las que no han empezado.
- Monitor y cierre: un monitor muestra cada cierto tiempo el estado del sistema y el programa se cierra de forma ordenada, guardando los resultados en un CSV.

## Estructura general del proyecto

El código fuente está en el directorio src, dividido en los siguientes paquetes:

- controlador: punto de entrada (Main), el menú de consola (MenuConsola), el hilo de la consola (ComandoConsola) y la interfaz AccionesSistema.
- modelo: las entidades del programa, como Solicitud, Trabajo, EstadoTrabajo y ResultadoTrabajo.
- core: el motor de trabajos con la cola y el pool de hilos (MotorTrabajos), el trabajador (HiloTrabajador) y el control de estados (GestorEstados).
- processors: las operaciones, que implementan la interfaz OperacionStrategy, y el lanzador del proceso hijo.
- procesohijo: el programa independiente que calcula las estadísticas del CSV.
- persistencia y service: lectura y validación del CSV de solicitudes y guardado de los resultados.
- factory, config y monitor: selección de operaciones y repositorios, lectura de la configuración y monitor del sistema.
- pruebas: clases con main para probar cada parte por separado.

Además, en la raíz están las carpetas config (app.properties), data (archivos de entrada y de prueba), docs (formato del CSV y uso de la consola) y tests (una ficha por cada prueba).

## Instrucciones para la ejecución

Es necesario tener instalado el JDK 21.

1. Clonar el repositorio:
   git clone https://github.com/AllisonHernando/TaskFlow.git
2. Importar el proyecto en Eclipse (File > Import > Existing Projects into Workspace) y seleccionar la carpeta TaskFlow.
3. Ejecutar la clase controlador.Main (Run As > Java Application).
4. Desde el menú, cargar un archivo de solicitudes, por ejemplo data/solicitudes_ejemplo.csv.

Las clases del paquete pruebas se ejecutan de la misma forma. El formato del CSV está explicado en docs/formato-csv.md y el uso del menú en docs/uso.md.

## Reparto del trabajo

Se trabajó con una rama main para la entrega, una rama beta para integrar y una rama feature por cada tarea, que se unía a beta mediante pull request.

- Consola y formato CSV: Allison Hernando, encargada del menú, la consola en un hilo independiente, la cancelación de trabajos, el formato del CSV, los archivos de prueba y la documentación de uso.
- Estados y concurrencia: Nerea, encargada de los estados de los trabajos, la cola de trabajos y el pool de hilos trabajadores.
- Multiproceso: Josué, encargado del programa hijo de estadísticas, su lanzamiento con ProcessBuilder, el límite de procesos con un semáforo y el cálculo del hash.
- Operaciones y monitor: Juanpu, encargado de las operaciones de texto, la comparación de archivos, la configuración, las factorías y el monitor.
- Datos, cierre e integración: Ekaitz, encargado de la lectura y validación del CSV, el guardado de resultados, el cierre ordenado y la integración en Main.

## Problemas relevantes encontrados y soluciones

- Consola bloqueando el programa: si el menú se ejecutaba en el hilo principal, el programa quedaba parado esperando a que el usuario escribiera. Se solucionó ejecutando la consola en su propio hilo con la clase ComandoConsola, de forma que el menú funciona a la vez que los trabajadores.
- Cancelación de trabajos que ya habían empezado: si la consola comprobaba el estado y luego lo cambiaba, un trabajador podía empezar ese trabajo entre los dos pasos. Se solucionó haciendo que la comprobación y el cambio se hagan en un único método synchronized del sistema.
- Proceso hijo bloqueado: si no se leía la salida del proceso hijo mientras se ejecutaba, este podía quedarse bloqueado. Se solucionó leyendo la salida estándar y la de error en dos hilos a la vez y fijando un tiempo máximo de ejecución.
