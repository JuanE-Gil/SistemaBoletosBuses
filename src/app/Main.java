package app;

import exceptions.AsientoOcupadoException;
import model.*;
import service.GestorVentas;
import util.Validador;

import java.util.Scanner;

public class Main {
    private static final String[] MEDIOS_DE_PAGO = {"EFECTIVO", "TARJETA", "YAPE"};

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        GestorVentas gestor = new GestorVentas();
        cargarDatosDemo(gestor);

        int op;
        do {
            mostrarMenu();
            op = Validador.leerEntero(sc, "Opción: ");
            switch (op) {
                case 1:
                    System.out.println(gestor.listarViajes());
                    break;
                case 2:
                    buscar(sc, gestor);
                    break;
                case 3:
                    verMapa(sc, gestor);
                    break;
                case 4:
                    vender(sc, gestor);
                    break;
                case 5:
                    anular(sc, gestor);
                    break;
                case 6:
                    reprogramar(sc, gestor);
                    break;
                case 7:
                    System.out.println(gestor.reporteVentas());
                    break;
                case 8:
                    System.out.println(gestor.reportePorRuta());
                    break;
                case 0:
                    System.out.println("Hasta pronto.");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    private static void mostrarMenu() {
        System.out.println("\n=== VENTA DE BOLETOS ===");
        System.out.println("1. Ver viajes");
        System.out.println("2. Buscar viajes (origen/destino)");
        System.out.println("3. Ver mapa de asientos");
        System.out.println("4. Vender boleto");
        System.out.println("5. Anular boleto");
        System.out.println("6. Reprogramar boleto");
        System.out.println("7. Reporte de ventas");
        System.out.println("8. Reporte por ruta");
        System.out.println("0. Salir");
    }

    private static void buscar(Scanner sc, GestorVentas g) {
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

    private static void verMapa(Scanner sc, GestorVentas g) {
        System.out.print("Código de viaje: ");
        Viaje viaje = g.buscarViaje(sc.nextLine().trim());
        if (viaje == null) System.out.println("Viaje no encontrado.");
        else System.out.println(viaje.getBus().mostrarMapaAsientos());
    }

    private static void vender(Scanner sc, GestorVentas g) {
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
        int medio = elegirMedioPago(sc);

        try {
            Cliente cliente = new Cliente(dni, nombre, apellido, correo, telefono);
            Boleto b = g.venderBoleto(cod, cliente, fila, col, MEDIOS_DE_PAGO[medio - 1]);
            System.out.println("\n" + b);
        } catch (AsientoOcupadoException | IllegalArgumentException | IllegalStateException e) {
            System.out.println("No se pudo vender: " + e.getMessage());
        }
    }

    private static void anular(Scanner sc, GestorVentas g) {
        System.out.print("Código de boleto: ");
        boolean ok = g.anularBoleto(sc.nextLine().trim());
        System.out.println(ok ? "Boleto anulado." : "Boleto no encontrado o ya anulado.");
    }

    private static void reprogramar(Scanner sc, GestorVentas g) {
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
        } catch (AsientoOcupadoException | IllegalArgumentException e) {
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

    private static void cargarDatosDemo(GestorVentas g) {
        g.agregarViaje(new Viaje("V001", new Ruta("Lima", "Arequipa", 80), new Bus("ABC-123", 5, 4), "15/11/2026", "20:00"));
        g.agregarViaje(new Viaje("V002", new Ruta("Lima", "Trujillo", 60), new Bus("XYZ-789", 5, 4), "16/11/2026", "22:30"));
    }
}
