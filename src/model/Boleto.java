package model;

public class Boleto {
    private final String codigo;
    private final Cliente cliente;
    private Viaje viaje;
    private int fila;
    private int columna;
    private final Pago pago;
    private boolean activo = true;
    private final Maleta[] maletas;

    public Boleto(String codigo, Cliente cliente, Viaje viaje, int fila, int columna, Pago pago, Maleta[] maletas) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.viaje = viaje;
        this.fila = fila;
        this.columna = columna;
        this.pago = pago;
        this.maletas = maletas;
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

    public Maleta[] getMaletas() {
        return maletas;
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
        StringBuilder sb = new StringBuilder();
        sb.append("Boleto ").append(codigo).append("\n")
          .append(cliente).append("\n")
          .append(viaje.getRuta()).append("\n")
          .append("Asiento: fila ").append(fila).append(", columna ").append(columna).append("\n")
          .append("Equipaje:");
        for (Maleta m : maletas) sb.append("\n  - ").append(m);
        sb.append("\nPago: ").append(pago);
        if (!activo) sb.append("  [ANULADO]");
        return sb.toString();
    }
}
