package model;

public class Viaje {
    private final String codigo;
    private final Ruta ruta;
    private final Bus bus;
    private final String fecha;
    private final String hora;

    public Viaje(String codigo, Ruta ruta, Bus bus, String fecha, String hora) {
        this.codigo = codigo;
        this.ruta = ruta;
        this.bus = bus;
        this.fecha = fecha;
        this.hora = hora;
    }

    public String getCodigo() {
        return codigo;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public Bus getBus() {
        return bus;
    }

    public String getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return codigo + " | " + ruta + " | " + fecha + " " + hora
                + " | Libres: " + bus.contarAsientosLibres();
    }
}

