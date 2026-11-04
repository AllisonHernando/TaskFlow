package processors;

import modelo.Solicitud;

public interface OperacionStrategy {
    String ejecutar(Solicitud solicitud) throws Exception;
}