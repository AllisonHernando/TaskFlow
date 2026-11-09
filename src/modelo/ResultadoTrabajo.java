package modelo;

public class ResultadoTrabajo {
    private final String idTrabajo;
    private final boolean exito;
    private final String mensajeResumen;
    private final long tiempoEjecucionMs;

    public ResultadoTrabajo(String idTrabajo, boolean exito, String mensajeResumen, long tiempoEjecucionMs) {
        this.idTrabajo = idTrabajo;
        this.exito = exito;
        this.mensajeResumen = mensajeResumen;
        this.tiempoEjecucionMs = tiempoEjecucionMs;
    }

    public String getIdTrabajo() {
        return idTrabajo;
    }

    public boolean isExito() {
        return exito;
    }

    public String getMensajeResumen() {
        return mensajeResumen;
    }

    public long getTiempoEjecucionMs() {
        return tiempoEjecucionMs;
    }
}