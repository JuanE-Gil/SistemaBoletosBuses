package app;

import exceptions.AsientoOcupadoException;
import model.*;
import service.GestorVentas;
import service.Reportes;
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

    private static void submenuConsultas(Scanner sc, GestorVentas g) {
        int op;
        do {
            System.out.println("\n--- CONSULTAS ---");
            System.out.println("1. Ver viajes");
            System.out.println("2. Buscar viajes (origen/destino)");
            System.out.println("3. Ver mapa de asientos");
            System.out.println("0. Volver");
            op = Validador.leerEntero(sc, "Opción: ");
            switch (op) {
                case 1:
                    System.out.println(g.listarViajes());
                    break;
                case 2:
                    buscar(sc, g);
                    break;
                case 3:
                    verMapa(sc, g);
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
            System.out.println("\n--- BOLETOS ---");
            System.out.println("1. Vender boleto");
            System.out.println("2. Anular boleto");
            System.out.println("3. Reprogramar boleto");
            System.out.println("0. Volver");
            op = Validador.leerEntero(sc, "Opción: ");
            switch (op) {
                case 1:
                    vender(sc, g);
                    break;
                case 2:
                    anular(sc, g);
                    break;
                case 3:
                    reprogramar(sc, g);
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
            System.out.println("\n--- REGISTRO ---");
            System.out.println("1. Registrar bus");
            System.out.println("2. Registrar viaje");
            System.out.println("0. Volver");
            op = Validador.leerEntero(sc, "Opción: ");
            switch (op) {
                case 1:
                    registrarBus(sc, g);
                    break;
                case 2:
                    registrarViaje(sc, g);
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
            System.out.println("\n--- REPORTES ---");
            System.out.println("1. Reporte de ventas");
            System.out.println("2. Reporte por ruta");
            System.out.println("3. Reporte por fecha");
            System.out.println("0. Volver");
            op = Validador.leerEntero(sc, "Opción: ");
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

    private static void registrarBus(Scanner sc, GestorVentas g) {
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

    private static void registrarViaje(Scanner sc, GestorVentas g) {
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
            Viaje viaje = g.agregarViaje(cod, new Ruta(origen, destino, tarifa), placa, fecha, hora);
            System.out.println("\nViaje registrado:\n" + viaje);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("No se pudo registrar: " + e.getMessage());
        }
    }

    private static void cargarDatosDemo(GestorVentas g) {
        g.agregarBus("ABC-123", 5, 4);
        g.agregarBus("XYZ-789", 5, 4);
        g.agregarViaje("V001", new Ruta("Lima", "Arequipa", 80), "ABC-123", "15/11/2026", "20:00");
        g.agregarViaje("V002", new Ruta("Lima", "Trujillo", 60), "XYZ-789", "16/11/2026", "22:30");
    }
}
