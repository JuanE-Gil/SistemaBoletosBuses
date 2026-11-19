package model;

public class Ruta {
    private String origen;
    private String destino;
    private double tarifa;

    public Ruta(String origen, String destino, double tarifa) {
        this.origen = origen;
        this.destino = destino;
        this.tarifa = tarifa;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public double getTarifa() {
        return tarifa;
    }

    @Override
    public String toString() {
        return origen + " - " + destino + " (S/ " + tarifa + ")";
    }
}
