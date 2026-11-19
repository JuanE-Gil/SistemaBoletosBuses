package model;

public class Boleto {
    private String codigo;
    private Cliente cliente;
    private Viaje viaje;
    private int fila;
    private int columna;
    private Pago pago;
    private boolean activo = true;

    public Boleto(String codigo, Cliente cliente, Viaje viaje, int fila, int columna, Pago pago) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.viaje = viaje;
        this.fila = fila;
        this.columna = columna;
        this.pago = pago;
    }

    public String getCodigo() {
        return codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public Pago getPago() {
        return pago;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void reprogramar(Viaje nuevoViaje, int nuevaFila, int nuevaColumna) {
        this.viaje = nuevoViaje;
        this.fila = nuevaFila;
        this.columna = nuevaColumna;
    }

    @Override
    public String toString() {
        return "Boleto " + codigo + "\n" + cliente + "\n" + viaje.getRuta()
                + "\nAsiento: fila " + fila + ", columna " + columna
                + "\nPago: " + pago + (activo ? "" : "  [ANULADO]");
    }
}
