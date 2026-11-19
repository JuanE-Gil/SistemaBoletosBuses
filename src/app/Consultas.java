package app;

import model.Viaje;
import service.GestorVentas;

import java.util.Scanner;

// flujos del submenú Consultas
public class Consultas {

    public static void buscar(Scanner sc, GestorVentas g) {
        System.out.print("Origen: ");
        String origen = sc.nextLine().trim();
        System.out.print("Destino: ");
        String destino = sc.nextLine().trim();
        System.out.print("Fecha (vacío = cualquier fecha): ");
        String fecha = sc.nextLine().trim();

        Viaje[] resultados = fecha.isEmpty()
                ? g.buscarViaje(origen, destino)
                : g.buscarViaje(origen, destino, fecha);

        if (resultados.length == 0) {
            System.out.println("No se encontraron viajes.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Viaje v : resultados) sb.append(v).append("\n");
        System.out.println(sb);
    }

    public static void verMapa(Scanner sc, GestorVentas g) {
        System.out.print("Código de viaje: ");
        Viaje viaje = g.buscarViaje(sc.nextLine().trim());
        if (viaje == null) System.out.println("Viaje no encontrado.");
        else System.out.println(viaje.getBus().mostrarMapaAsientos());
    }
}
