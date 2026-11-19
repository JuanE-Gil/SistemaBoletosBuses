package model;

public class Asiento {
    private String numeroAsiento;
    private boolean estado; // true = ocupado

    public Asiento(String numeroAsiento) {
        this.numeroAsiento = numeroAsiento;
        this.estado = false;
    }

    public String getNumeroAsiento() {
        return numeroAsiento;
    }

    public boolean getEstado() {
        return estado;
    }

    public void ocupar() {
        estado = true;
    }

    public void liberar() {
        estado = false;
    }

    public boolean estaLibre() {
        return !estado;
    }
}
