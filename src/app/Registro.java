package app;

import model.Ruta;
import service.GestorVentas;
import util.Validador;

import java.util.Scanner;

// flujos del submenú Registro
public class Registro {

    public static void registrarBus(Scanner sc, GestorVentas g) {
        System.out.print("Placa: ");
        String placa = sc.nextLine().trim();
        if (placa.isEmpty()) {
            System.out.println("La placa no puede estar vacía.");
            return;
        }
        if (g.buscarBus(placa) != null) {
            System.out.println("Esa placa ya existe.");
            return;
        }
        int filas = Validador.leerEntero(sc, "Filas de asientos: ");
        int columnas = Validador.leerEntero(sc, "Columnas de asientos: ");
        try {
            g.agregarBus(placa, filas, columnas);
            System.out.println("Bus registrado: " + placa + " (" + filas + " filas x " + columnas + " columnas)");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("No se pudo registrar: " + e.getMessage());
        }
    }

    public static void registrarViaje(Scanner sc, GestorVentas g) {
        System.out.print("Código de viaje: ");
        String cod = sc.nextLine().trim();
        if (cod.isEmpty()) {
            System.out.println("El código no puede estar vacío.");
            return;
        }
        if (g.buscarViaje(cod) != null) {
            System.out.println("Ese código ya existe.");
            return;
        }

        System.out.print("Origen: ");
        String origen = sc.nextLine().trim();
        System.out.print("Destino: ");
        String destino = sc.nextLine().trim();
        if (origen.isEmpty() || destino.isEmpty()) {
            System.out.println("Origen y destino no pueden estar vacíos.");
            return;
        }

        double tarifa;
        while (true) {
            tarifa = Validador.leerDecimal(sc, "Tarifa (S/): ");
            if (Double.isFinite(tarifa) && tarifa > 0) break;
            System.out.println("Tarifa inválida (debe ser mayor a 0).");
        }

        String fecha;
        while (true) {
            System.out.print("Fecha (dd/MM/yyyy): ");
            fecha = sc.nextLine().trim();
            if (Validador.validarFecha(fecha)) break;
            System.out.println("Fecha inválida.");
        }

        String hora;
        while (true) {
            System.out.print("Hora (HH:mm): ");
            hora = sc.nextLine().trim();
            if (Validador.validarHora(hora)) break;
            System.out.println("Hora inválida.");
        }

        System.out.print("Placa del bus: ");
        String placa = sc.nextLine().trim();
        if (g.buscarBus(placa) == null) {
            System.out.println("No existe ese bus. Regístrelo primero (menú Registro).");
            return;
        }

        try {
            var viaje = g.agregarViaje(cod, new Ruta(origen, destino, tarifa), placa, fecha, hora);
            System.out.println("\nViaje registrado:\n" + viaje);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("No se pudo registrar: " + e.getMessage());
        }
    }
}
