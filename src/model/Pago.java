package model;

public class Pago {
    private double monto;
    private String medioPago;
    private String fecha; // fecha de venta dd/MM/yyyy

    public Pago(double monto, String medioPago, String fecha) {
        this.monto = monto;
        this.medioPago = medioPago;
        this.fecha = fecha;
    }

    public double getMonto() {
        return monto;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public String getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return "S/ " + monto + " (" + medioPago + ") " + fecha;
    }
}
