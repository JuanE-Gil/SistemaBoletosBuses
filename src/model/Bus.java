package model;

public class Bus {
    private String placa;
    private Asiento[][] asientos;

    public Bus(String placa, int filas, int columnas) {
        this.placa = placa;
        this.asientos = new Asiento[filas][columnas];
        for (int i = 0; i < filas; i++)
            for (int j = 0; j < columnas; j++)
                asientos[i][j] = new Asiento("F" + (i + 1) + "C" + (j + 1));
    }

    public String getPlaca() {
        return placa;
    }

    public boolean existe(int fila, int columna) {
        return fila >= 1 && fila <= asientos.length && columna >= 1 && columna <= asientos[0].length;
    }

    // delegación al Asiento correspondiente: Bus solo localiza la celda
    public boolean asientoLibre(int fila, int columna) {
        return asientos[fila - 1][columna - 1].estaLibre();
    }

    public void ocuparAsiento(int fila, int columna) {
        asientos[fila - 1][columna - 1].ocupar();
    }

    public void liberarAsiento(int fila, int columna) {
        asientos[fila - 1][columna - 1].liberar();
    }

    public int contarAsientosLibres() {
        int libres = 0;
        for (int i = 0; i < asientos.length; i++)
            for (int j = 0; j < asientos[0].length; j++)
                if (asientos[i][j].estaLibre())
                    libres++;
        return libres;
    }

    public String mostrarMapaAsientos() {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < asientos[0].length; j++)
            sb.append(" C").append(j + 1).append(" ");
        sb.append("\n");
        for (int i = 0; i < asientos.length; i++) {
            sb.append("Fila ").append(i + 1).append(" ");
            for (int j = 0; j < asientos[i].length; j++)
                sb.append(asientos[i][j].estaLibre() ? "[ ]" : "[X]");
            sb.append("\n");
        }
        return sb.toString();
    }
}
