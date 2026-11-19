package util;

import java.util.Scanner;

public class Validador {

    public static boolean validarDni(String dni) {
        return dni != null && dni.matches("\\d{8}");
    }

    public static boolean validarCorreo(String correo) {
        return correo != null && correo.matches("[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}");
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
        return peso > 0;
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
