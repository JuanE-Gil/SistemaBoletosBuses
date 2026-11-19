package model;

public class Maleta {
    public static final double LIMITE_KG = 25;
    public static final double TARIFA_SOBREPESO = 5.0; // S/ por kg excedido

    private double peso;
    private double sobrepeso;

    public Maleta(double peso) {
        this.peso = peso;
        this.sobrepeso = peso > LIMITE_KG ? (peso - LIMITE_KG) * TARIFA_SOBREPESO : 0;
    }

    public double getPeso() {
        return peso;
    }

    public double getSobrepeso() {
        return sobrepeso;
    }

    @Override
    public String toString() {
        return peso + " kg" + (sobrepeso > 0 ? " (sobrepeso: S/ " + sobrepeso + ")" : "");
    }
}
