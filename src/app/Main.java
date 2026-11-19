package app;

import model.Ruta;
import service.GestorVentas;
import service.Reportes;
import util.Validador;

import java.util.Scanner;

public class Main {

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
                    submenuConsultas(sc, gestor);
                    break;
                case 2:
                    submenuBoletos(sc, gestor);
                    break;
                case 3:
                    submenuRegistro(sc, gestor);
                    break;
                case 4:
                    submenuReportes(sc, gestor);
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
        System.out.println("\n=== SISTEMA DE VENTA DE BOLETOS ===");
        System.out.println("1. Consultas");
        System.out.println("2. Boletos");
        System.out.println("3. Registro");
        System.out.println("4. Reportes");
        System.out.println("0. Salir");
    }

    // imprime un submenú con sus opciones numeradas (0 = volver) y devuelve la elegida
    private static int submenu(Scanner sc, String titulo, String... opciones) {
        System.out.println("\n--- " + titulo + " ---");
        for (int i = 0; i < opciones.length; i++)
            System.out.println((i + 1) + ". " + opciones[i]);
        System.out.println("0. Volver");
        return Validador.leerEntero(sc, "Opción: ");
    }

    private static void submenuConsultas(Scanner sc, GestorVentas g) {
        int op;
        do {
            op = submenu(sc, "CONSULTAS", "Ver viajes", "Buscar viajes (origen/destino)", "Ver mapa de asientos");
            switch (op) {
                case 1:
                    System.out.println(g.listarViajes());
                    break;
                case 2:
                    Consultas.buscar(sc, g);
                    break;
                case 3:
                    Consultas.verMapa(sc, g);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    private static void submenuBoletos(Scanner sc, GestorVentas g) {
        int op;
        do {
            op = submenu(sc, "BOLETOS", "Vender boleto", "Anular boleto", "Reprogramar boleto");
            switch (op) {
                case 1:
                    Boletos.vender(sc, g);
                    break;
                case 2:
                    Boletos.anular(sc, g);
                    break;
                case 3:
                    Boletos.reprogramar(sc, g);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    private static void submenuRegistro(Scanner sc, GestorVentas g) {
        int op;
        do {
            op = submenu(sc, "REGISTRO", "Registrar bus", "Registrar viaje");
            switch (op) {
                case 1:
                    Registro.registrarBus(sc, g);
                    break;
                case 2:
                    Registro.registrarViaje(sc, g);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    private static void submenuReportes(Scanner sc, GestorVentas g) {
        int op;
        do {
            op = submenu(sc, "REPORTES", "Reporte de ventas", "Reporte por ruta", "Reporte por fecha");
            switch (op) {
                case 1:
                    System.out.println(Reportes.total(g.getBoletos()));
                    break;
                case 2:
                    System.out.println(Reportes.porRuta(g.getBoletos()));
                    break;
                case 3:
                    System.out.println(Reportes.porFecha(g.getBoletos()));
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    private static void cargarDatosDemo(GestorVentas g) {
        g.agregarBus("ABC-123", 5, 4);
        g.agregarBus("XYZ-789", 5, 4);
        g.agregarViaje("V001", new Ruta("Lima", "Arequipa", 80), "ABC-123", "15/11/2026", "20:00");
        g.agregarViaje("V002", new Ruta("Lima", "Trujillo", 60), "XYZ-789", "16/11/2026", "22:30");
    }
}
