package cl.pruebas.estacionamiento.modelo;

import java.time.LocalDateTime;

public class Ticket {

    private final int id;
    private final String patente;
    private final TipoVehiculo tipoVehiculo;
    private final LocalDateTime fechaHoraEntrada;
    private LocalDateTime fechaHoraSalida;
    private int montoCobrado;
    private EstadoTicket estado;

    public Ticket(int id, String patente, TipoVehiculo tipoVehiculo, LocalDateTime fechaHoraEntrada) {
        this.id = id;
        this.patente = patente;
        this.tipoVehiculo = tipoVehiculo;
        this.fechaHoraEntrada = fechaHoraEntrada;
        this.estado = EstadoTicket.ABIERTO;
    }

    public int getId() {
        return id;
    }

    public String getPatente() {
        return patente;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public LocalDateTime getFechaHoraEntrada() {
        return fechaHoraEntrada;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public int getMontoCobrado() {
        return montoCobrado;
    }

    public EstadoTicket getEstado() {
        return estado;
    }

    public void cerrar(LocalDateTime fechaHoraSalida, int montoCobrado) {
        this.fechaHoraSalida = fechaHoraSalida;
        this.montoCobrado = montoCobrado;
        this.estado = EstadoTicket.CERRADO;
    }
}
