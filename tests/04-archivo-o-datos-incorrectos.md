------- Prueba 4: Archivo o datos incorrectos

------- Objetivo

Comprobar que las operaciones detectan rutas, archivos y parametros incorrectos, comunican el error mediante una excepcion y permiten ejecutar otros trabajos despues del fallo.

------- Datos de entrada

data/hola.txt (correcto: 2 lineas, 5 palabras y 5 apariciones de "hola"), data/pruebaTextoJuan.txt (otro archivo correcto) y data/no_existe_04.txt (no existe). Tambien se prueban rutas vacias, una ruta mal formada, un parametro de busqueda vacio y una lista de rutas incompleta.

------- Procedimiento

Opcion A (sin Main, directamente con los procesadores):

1. Ejecutar la clase pruebas.PruebasJuanArchivoDatosIncorrectos (Run As, Java Application).

2. Comprobar los errores de ANALIZAR_TEXTO, BUSCAR_TEXTO y COMPARAR_ARCHIVOS.

3. Verificar que los errores lanzan IOException o IllegalArgumentException, en lugar de devolver un texto como si fuera un resultado correcto.

4. Comprobar que siguen funcionando las operaciones validas despues de los errores.

Opcion B (programa completo, cuando Main funcione):

1. Ejecutar controlador.Main.

2. Cargar data/solicitudes_prueba4.csv.

3. Comprobar que las solicitudes invalidas se rechazan con motivo, que los trabajos aceptados que fallen al ejecutarse terminan FALLIDO, y que los correctos terminan COMPLETADO.

------- Resultado esperado

Las rutas vacias o mal formadas, los archivos inexistentes y los parametros incorrectos deben detectarse sin detener el programa. Los procesadores deben comunicar el fallo mediante una excepcion. Los casos correctos deben seguir funcionando. En el programa integrado, una solicitud rechazada por el validador no cuenta como trabajo aceptado; si un trabajo ya aceptado falla durante su ejecucion, debe quedar FALLIDO.

------- Resultado obtenido

Ejecutado con la opcion A (PruebasJuanArchivoDatosIncorrectos), en Java 21, despues de aplicar las correcciones a los tres procesadores:

[OK] Analizar: archivo inexistente (IOException)
[OK] Analizar: ruta vacia (IllegalArgumentException)
[OK] Analizar: ruta mal formada (IllegalArgumentException)
[OK] Analizar: archivo correcto despues de errores
[OK] Buscar: texto vacio (IllegalArgumentException)
[OK] Buscar: uno de los archivos no existe (IOException)
[OK] Buscar: ruta vacia al final (IllegalArgumentException)
[OK] Buscar: archivo correcto despues de errores
[OK] Comparar: segundo archivo inexistente (IOException)
[OK] Comparar: falta una segunda ruta (IllegalArgumentException)
[OK] Comparar: dos archivos iguales despues de errores
[OK] Comparar: archivos diferentes

Resultado: 12/12 comprobaciones correctas.
