package service;

import model.Boleto;

public class Reportes {

    public static String total(Boleto[] boletos) {
        double total = 0;
        int vendidos = 0;
        for (int i = 0; i < boletos.length; i++) {
            if (boletos[i].isActivo()) {
                total += boletos[i].getPago().getMonto();
                vendidos++;
            }
        }
        return "Boletos vendidos: " + vendidos + " | Total recaudado: S/ " + total;
    }

    public static String porRuta(Boleto[] boletos) {
        String[] rutas = new String[boletos.length];
        int[] cantidades = new int[boletos.length];
        double[] totales = new double[boletos.length];
        int n = 0;

        for (int i = 0; i < boletos.length; i++) {
            Boleto boleto = boletos[i];
            if (!boleto.isActivo()) continue;
            String ruta = boleto.getViaje().getRuta().getOrigen() + " - " + boleto.getViaje().getRuta().getDestino();
            int pos = -1;
            for (int j = 0; j < n; j++)
                if (rutas[j].equals(ruta)) pos = j;
            if (pos == -1) { pos = n; rutas[n++] = ruta; }
            cantidades[pos]++;
            totales[pos] += boleto.getPago().getMonto();
        }

        StringBuilder sb = new StringBuilder("--- Ventas por ruta ---\n");
        if (n == 0) sb.append("Sin ventas.\n");
        for (int j = 0; j < n; j++)
            sb.append(rutas[j]).append(": ").append(cantidades[j])
              .append(" boletos | S/ ").append(totales[j]).append("\n");
        return sb.toString();
    }
}
