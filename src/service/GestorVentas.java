package service;

import exceptions.AsientoOcupadoException;
import model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GestorVentas {
    private Viaje[] viajes = new Viaje[20];
    private Boleto[] boletos = new Boleto[200];
    private Bus[] buses = new Bus[20];
    private int cantidadViajes = 0;
    private int cantidadBoletos = 0;
    private int cantidadBuses = 0;

    // ---- flota ----

    public void agregarBus(String placa, int filas, int columnas) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("La placa no puede estar vacía.");
        }
        if (buscarBus(placa) != null) {
            throw new IllegalArgumentException("La placa ya existe.");
        }
        if (cantidadBuses >= buses.length) {
            throw new IllegalStateException("Máximo de buses alcanzado (" + buses.length + ").");
        }
        // crear primero: si el constructor rechaza las dimensiones, el contador no avanza
        Bus bus = new Bus(placa, filas, columnas);
        buses[cantidadBuses++] = bus;
    }

    public Bus buscarBus(String placa) {
        for (int i = 0; i < cantidadBuses; i++)
            if (buses[i].getPlaca().equalsIgnoreCase(placa)) return buses[i];
        return null;
    }

    public Viaje agregarViaje(String codigo, Ruta ruta, String placa, String fecha, String hora) {
        if (buscarViaje(codigo) != null) {
            throw new IllegalArgumentException("El código de viaje ya existe.");
        }
        Bus plantilla = buscarBus(placa);
        if (plantilla == null) {
            throw new IllegalArgumentException("El bus no está registrado.");
        }
        if (cantidadViajes >= viajes.length) {
            throw new IllegalStateException("Máximo de viajes alcanzado (" + viajes.length + ").");
        }
        // copia(): cada viaje tiene su propio estado de asientos y equipaje
        Viaje viaje = new Viaje(codigo, ruta, plantilla.copia(), fecha, hora);
        viajes[cantidadViajes++] = viaje;
        return viaje;
    }

    // ---- búsqueda de viajes (sobrecarga de parámetros) ----

    public Viaje buscarViaje(String codigo) {
        for (int i = 0; i < cantidadViajes; i++)
            if (viajes[i].getCodigo().equalsIgnoreCase(codigo)) return viajes[i];
        return null;
    }

    public Viaje[] buscarViaje(String origen, String destino) {
        return buscarViaje(origen, destino, null);
    }

    public Viaje[] buscarViaje(String origen, String destino, String fecha) {
        Viaje[] coincidentes = new Viaje[cantidadViajes];
        int n = 0;
        for (int i = 0; i < cantidadViajes; i++) {
            Viaje v = viajes[i];
            Ruta r = v.getRuta();
            if (r.getOrigen().equalsIgnoreCase(origen)
                    && r.getDestino().equalsIgnoreCase(destino)
                    && (fecha == null || v.getFecha().equals(fecha)))
                coincidentes[n++] = v;
        }
        Viaje[] resultados = new Viaje[n];
        System.arraycopy(coincidentes, 0, resultados, 0, n);
        return resultados;
    }

    // ---- boletos ----

    public Boleto venderBoleto(String codViaje, Cliente cliente, int fila, int columna, String medioPago,
                               Maleta[] maletas)
            throws AsientoOcupadoException {
        Viaje viaje = buscarViaje(codViaje);
        if (viaje == null) {
            throw new IllegalArgumentException("El viaje no existe.");
        }
        Bus bus = viaje.getBus();
        if (!bus.existe(fila, columna)) {
            throw new IllegalArgumentException("Asiento fuera de rango.");
        }
        if (!bus.asientoLibre(fila, columna)) {
            throw new AsientoOcupadoException("El asiento ya está ocupado.");
        }
        if (maletas.length < 1 || maletas.length > 2) {
            throw new IllegalArgumentException("Máximo 2 maletas por boleto.");
        }
        for (Maleta m : maletas) {
            if (m.getPeso() <= 0) {
                throw new IllegalArgumentException("Peso de maleta inválido.");
            }
        }
        if (bus.maletasDisponibles() < maletas.length) {
            throw new IllegalStateException("Cupo de equipaje lleno.");
        }
        if (cantidadBoletos >= boletos.length) {
            throw new IllegalStateException("Capacidad de boletos llena.");
        }

        double monto = viaje.getRuta().getTarifa();
        for (Maleta m : maletas) monto += m.getSobrepeso();
        String fechaVenta = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Pago pago = new Pago(monto, medioPago, fechaVenta);

        bus.ocuparAsiento(fila, columna);
        for (int i = 0; i < maletas.length; i++) bus.cargarMaleta();
        String codigo = "B" + String.format("%04d", cantidadBoletos + 1);
        Boleto boleto = new Boleto(codigo, cliente, viaje, fila, columna, pago, maletas);
        boletos[cantidadBoletos++] = boleto;
        return boleto;
    }

    public Boleto reprogramarBoleto(String codBoleto, String codNuevoViaje, int nuevaFila, int nuevaColumna)
            throws AsientoOcupadoException {
        Boleto boleto = buscarBoletoActivo(codBoleto);
        if (boleto == null) {
            throw new IllegalArgumentException("Boleto no encontrado o ya anulado.");
        }
        Viaje nuevoViaje = buscarViaje(codNuevoViaje);
        if (nuevoViaje == null) {
            throw new IllegalArgumentException("El viaje no existe.");
        }
        Bus busNuevo = nuevoViaje.getBus();
        if (!busNuevo.existe(nuevaFila, nuevaColumna)) {
            throw new IllegalArgumentException("Asiento fuera de rango.");
        }
        if (!busNuevo.asientoLibre(nuevaFila, nuevaColumna)) {
            throw new AsientoOcupadoException("El asiento ya está ocupado.");
        }
        Bus busViejo = boleto.getViaje().getBus();
        // si es el mismo bus el equipaje no cambia de cupo (neto 0): solo validar destinos distintos
        if (busNuevo != busViejo && busNuevo.maletasDisponibles() < boleto.getMaletas().length) {
            throw new IllegalStateException("Cupo de equipaje lleno en el bus destino.");
        }
        busNuevo.ocuparAsiento(nuevaFila, nuevaColumna);
        for (int i = 0; i < boleto.getMaletas().length; i++) busNuevo.cargarMaleta();
        busViejo.liberarAsiento(boleto.getFila(), boleto.getColumna());
        for (int i = 0; i < boleto.getMaletas().length; i++) busViejo.descargarMaleta();
        boleto.reprogramar(nuevoViaje, nuevaFila, nuevaColumna);
        return boleto;
    }

    public boolean anularBoleto(String codigo) {
        Boleto boleto = buscarBoletoActivo(codigo);
        if (boleto == null) return false;
        Bus bus = boleto.getViaje().getBus();
        bus.liberarAsiento(boleto.getFila(), boleto.getColumna());
        for (int i = 0; i < boleto.getMaletas().length; i++) bus.descargarMaleta();
        boleto.setActivo(false);
        return true;
    }

    private Boleto buscarBoletoActivo(String codigo) {
        for (int i = 0; i < cantidadBoletos; i++)
            if (boletos[i].getCodigo().equalsIgnoreCase(codigo) && boletos[i].isActivo())
                return boletos[i];
        return null;
    }

    // ---- reportes ----

    public String listarViajes() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cantidadViajes; i++) sb.append(viajes[i]).append("\n");
        return sb.toString();
    }

    public Boleto[] getBoletos() {
        return java.util.Arrays.copyOf(boletos, cantidadBoletos);
    }
}
