package model;

public class Cliente {
    private String dni;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;

    public Cliente(String dni, String nombre, String apellido, String correo, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    @Override
    public String toString() {
        return apellido + ", " + nombre + " | DNI: " + dni + " | " + correo + " | " + telefono;
    }
}
