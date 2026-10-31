package pruebas;

import factory.ProcessorFactory;
import processors.OperacionStrategy;

public class PruebasJuanOperaciones {

    public static void main(String[] args) {

//        Solicitud solicitud = new Solicitud(
//            "1",
//            "ANALIZAR_TEXTO",
//            "data/pruebaTextoJuan.txt",
//            ""
//        );
//
//        AnalizadorTexto analizador =
//                new AnalizadorTexto();
//
//        System.out.println(analizador.ejecutar(solicitud));
    	// PRUEBA BUSCAR_TEXTO

//    	Solicitud solicitudBusqueda = new Solicitud(
//    	    "2",
//    	    "BUSCAR_TEXTO",
//    	    "data/pruebaTextoJuan.txt",
//    	    "7"//palabra para buscar en la prueba para ver si el buscador funciona
//    	);
//
//    	System.out.println(
//    	    new BuscadorTexto().ejecutar(solicitudBusqueda)
//    	);
    	
    	ProcessorFactory factory = new ProcessorFactory();

    	OperacionStrategy analizador =
    	        factory.crear("ANALIZAR_TEXTO");

    	OperacionStrategy buscador =
    	        factory.crear("BUSCAR_TEXTO");

    	System.out.println(
    	    "Procesador 1: " + analizador.getClass().getSimpleName()
    	);

    	System.out.println(
    	    "Procesador 2: " + buscador.getClass().getSimpleName()
    	);
    }
}