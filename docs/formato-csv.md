-Formato del CSV de solicitudes-

El CSV de solicitudes es el que se carga con la opcion 1 del menu. En cada linea va un
trabajo. No es el archivo que se analiza, solo dice que hay que hacer y con que archivo.

Cada linea tiene 4 cosas separadas por ;

id;operacion;ruta;parametros

El id no se puede repetir. La operacion puede ser ANALIZAR_TEXTO, BUSCAR_TEXTO o
ESTADISTICAS_CSV. La ruta es el archivo (por ejemplo data/allison_texto.txt) y los
parametros solo se usan en BUSCAR_TEXTO para poner lo que buscas, en las otras se deja
vacio pero hay que poner el ; del final igual.

La primera linea es la cabecera y las lineas vacias no se cuentan. Si algo lleva ; dentro
se pone entre comillas.

Ejemplo (data/solicitudes_ejemplo.csv):

id;operacion;ruta;parametros
E001;ANALIZAR_TEXTO;data/allison_texto.txt;
E002;BUSCAR_TEXTO;data/allison_texto.txt;TaskFlow
E003;ESTADISTICAS_CSV;data/allison_numeros.csv;
E004;BUSCAR_TEXTO;data/allison_texto.txt;hilo
E005;ANALIZAR_TEXTO;data/allison_texto.txt;

-Lineas mal-

Si una linea esta mal el programa no se para, dice en que linea esta el fallo y por que,
y sigue con las demas. Esas no cuentan como aceptadas.

Lo que se rechaza:
- id repetido
- operacion que no existe
- sin id o sin ruta
- archivo que no existe
- lineas con mas o menos de 4 campos
- comillas sin cerrar

-Archivos que he hecho-

solicitudes_ejemplo.csv -> 5 bien
solicitudes_100.csv -> 100 bien (A001 hasta A100)
solicitudes_100_con_errores.csv -> las 100 de antes y 8 mal al final (lineas 102 a 109)
allison_texto.txt -> texto corto para buscar TaskFlow y hilo
allison_numeros.csv -> 10, 20, -5, 2.5 y hola. Tienen que salir 4 validos, suma 27.5,
media 6.875, min -5, max 20 y 1 descartado (el hola)

Para probarlo hice PruebaCsv, que lee los archivos linea a linea con el SolicitudMapper y el
ValidadorSolicitudes. Con el de 100 salen 100 aceptadas y con el de errores 100 aceptadas y
8 rechazadas.