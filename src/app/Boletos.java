package app;

import exceptions.AsientoOcupadoException;
import model.*;
import service.GestorVentas;
import util.Validador;

import java.util.Scanner;

// flujos del submenú Boletos
public class Boletos {
    private static final String[] MEDIOS_DE_PAGO = {"EFECTIVO", "TARJETA", "YAPE"};

    public static void vender(Scanner sc, GestorVentas g) {
        System.out.print("Código de viaje: ");
        String cod = sc.nextLine().trim();
        System.out.print("DNI: ");
        String dni = sc.nextLine().trim();
        if (!Validador.validarDni(dni)) {
            System.out.println("DNI inválido (8 dígitos).");
            return;
        }
        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Apellido: ");
        String apellido = sc.nextLine().trim();
        System.out.print("Correo: ");
        String correo = sc.nextLine().trim();
        if (!Validador.validarCorreo(correo)) {
            System.out.println("Correo inválido.");
            return;
        }
        System.out.print("Teléfono: ");
        String telefono = sc.nextLine().trim();
        int fila = Validador.leerEntero(sc, "Fila: ");
        int col = Validador.leerEntero(sc, "Columna: ");
        Maleta[] maletas = pedirMaletas(sc, g, cod);
        int medio = elegirMedioPago(sc);

        try {
            Cliente cliente = new Cliente(dni, nombre, apellido, correo, telefono);
            Boleto b = g.venderBoleto(cod, cliente, fila, col, MEDIOS_DE_PAGO[medio - 1], maletas);
            System.out.println("\n" + b);
        } catch (AsientoOcupadoException | IllegalArgumentException | IllegalStateException e) {
            System.out.println("No se pudo vender: " + e.getMessage());
        }
    }

    private static Maleta[] pedirMaletas(Scanner sc, GestorVentas g, String codViaje) {
        double peso1 = leerPeso(sc, "Peso de la maleta 1 (kg): ");
        Maleta m1 = new Maleta(peso1);
        System.out.println("Maleta 1: " + m1);

        // solo califica para maleta adicional si está dentro del límite y hay cupo para 2 en el bus
        if (peso1 > Maleta.LIMITE_KG) return new Maleta[]{m1};

        Viaje viaje = g.buscarViaje(codViaje);
        if (viaje == null || viaje.getBus().maletasDisponibles() < 2) {
            System.out.println("Sin disponibilidad para maleta adicional.");
            return new Maleta[]{m1};
        }
        System.out.print("¿Desea agregar una maleta adicional? (sí/no): ");
        String resp = sc.nextLine().trim();
        if (!resp.equalsIgnoreCase("sí") && !resp.equalsIgnoreCase("si")) return new Maleta[]{m1};

        double peso2 = leerPeso(sc, "Peso de la maleta 2 (kg): ");
        Maleta m2 = new Maleta(peso2);
        System.out.println("Maleta 2: " + m2);
        return new Maleta[]{m1, m2};
    }

    private static double leerPeso(Scanner sc, String mensaje) {
        while (true) {
            double peso = Validador.leerDecimal(sc, mensaje);
            if (Validador.validarPeso(peso)) return peso;
            System.out.println("Peso inválido (debe ser mayor a 0).");
        }
    }

    public static void anular(Scanner sc, GestorVentas g) {
        System.out.print("Código de boleto: ");
        boolean ok = g.anularBoleto(sc.nextLine().trim());
        System.out.println(ok ? "Boleto anulado." : "Boleto no encontrado o ya anulado.");
    }

    public static void reprogramar(Scanner sc, GestorVentas g) {
        System.out.print("Código de boleto: ");
        String codBol = sc.nextLine().trim();
        System.out.print("Código del nuevo viaje: ");
        String codViaje = sc.nextLine().trim();

        Viaje nuevoViaje = g.buscarViaje(codViaje);
        if (nuevoViaje == null) {
            System.out.println("Viaje no encontrado.");
            return;
        }
        System.out.println(nuevoViaje.getBus().mostrarMapaAsientos());
        int nuevaFila = Validador.leerEntero(sc, "Nueva fila: ");
        int nuevaCol = Validador.leerEntero(sc, "Nueva columna: ");

        try {
            Boleto b = g.reprogramarBoleto(codBol, codViaje, nuevaFila, nuevaCol);
            System.out.println("\nBoleto reprogramado:\n" + b);
        } catch (AsientoOcupadoException | IllegalArgumentException | IllegalStateException e) {
            System.out.println("No se pudo reprogramar: " + e.getMessage());
        }
    }

    private static int elegirMedioPago(Scanner sc) {
        for (int i = 0; i < MEDIOS_DE_PAGO.length; i++)
            System.out.println((i + 1) + ". " + MEDIOS_DE_PAGO[i]);
        int op;
        do {
            op = Validador.leerEntero(sc, "Medio de pago: ");
        } while (op < 1 || op > MEDIOS_DE_PAGO.length);
        return op;
    }
}
