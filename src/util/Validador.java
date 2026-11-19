package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public class Validador {

    public static boolean validarDni(String dni) {
        return dni != null && dni.matches("\\d{8}");
    }

    public static boolean validarCorreo(String correo) {
        return correo != null && correo.matches("[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}");
    }

    public static boolean validarFecha(String fecha) {
        try {
            // uuuu + STRICT rechaza fechas inexistentes como 31/02/2026 o 99/99/9999
            LocalDate.parse(fecha, DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean validarHora(String hora) {
        return hora != null && hora.matches("([01]\\d|2[0-3]):[0-5]\\d");
    }

    public static double leerDecimal(Scanner sc, String mensaje) {
        while (true) {
            System.out.println(mensaje);
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido");
            }
        }
    }

    public static boolean validarPeso(double peso) {
        // isFinite excluye "Infinity"/"NaN" que Double.parseDouble acepta como número
        return Double.isFinite(peso) && peso > 0;
    }

    public static int leerEntero(Scanner sc, String mensaje) {
        while (true) {
            System.out.println(mensaje);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido");
            }
        }
    }

}
