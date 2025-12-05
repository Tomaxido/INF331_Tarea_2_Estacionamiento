package cl.pruebas.estacionamiento.servicio;

import cl.pruebas.estacionamiento.modelo.EstadoTicket;
import cl.pruebas.estacionamiento.modelo.Ticket;
import cl.pruebas.estacionamiento.modelo.TipoVehiculo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorTickets {

    private final Map<Integer, Ticket> tickets;
    private final CalculadoraTarifas calculadoraTarifas;
    private int siguienteId;

    public GestorTickets(CalculadoraTarifas calculadoraTarifas) {
        this.calculadoraTarifas = calculadoraTarifas;
        this.tickets = new HashMap<>();
        this.siguienteId = 1;
    }

    public Ticket registrarEntrada(String patente, TipoVehiculo tipoVehiculo, LocalDateTime fechaHoraEntrada) {
        String patenteLimpia = validarPatente(patente);
        int id = siguienteId;
        siguienteId++;
        Ticket ticket = new Ticket(id, patenteLimpia, tipoVehiculo, fechaHoraEntrada);
        tickets.put(id, ticket);
        return ticket;
    }

    public Ticket registrarSalida(int idTicket, LocalDateTime fechaHoraSalida) {
        Ticket ticket = tickets.get(idTicket);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket no existe");
        }
        if (ticket.getEstado() == EstadoTicket.CERRADO) {
            throw new IllegalStateException("Ticket ya cerrado");
        }
        int monto = calculadoraTarifas.calcularMonto(ticket.getTipoVehiculo(),
                ticket.getFechaHoraEntrada(), fechaHoraSalida);
        ticket.cerrar(fechaHoraSalida, monto);
        return ticket;
    }

    public Ticket buscarPorId(int idTicket) {
        return tickets.get(idTicket);
    }

    public List<Ticket> obtenerTicketsAbiertos() {
        List<Ticket> resultado = new ArrayList<>();
        for (Ticket ticket : tickets.values()) {
            if (ticket.getEstado() == EstadoTicket.ABIERTO) {
                resultado.add(ticket);
            }
        }
        resultado.sort(Comparator.comparingInt(Ticket::getId));
        return resultado;
    }

    public List<Ticket> obtenerTicketsCerrados() {
        List<Ticket> resultado = new ArrayList<>();
        for (Ticket ticket : tickets.values()) {
            if (ticket.getEstado() == EstadoTicket.CERRADO) {
                resultado.add(ticket);
            }
        }
        resultado.sort(Comparator.comparingInt(Ticket::getId));
        return resultado;
    }

    public int calcularTotalRecaudado(LocalDate fecha) {
        int total = 0;
        for (Ticket ticket : tickets.values()) {
            if (ticket.getEstado() == EstadoTicket.CERRADO
                    && ticket.getFechaHoraSalida() != null
                    && ticket.getFechaHoraSalida().toLocalDate().equals(fecha)) {
                total += ticket.getMontoCobrado();
            }
        }
        return total;
    }

    private String validarPatente(String patente) {
        if (patente == null) {
            throw new IllegalArgumentException("Patente inválida");
        }
        String patenteLimpia = patente.trim();
        if (patenteLimpia.isEmpty() || patenteLimpia.length() > 6) {
            throw new IllegalArgumentException("Patente inválida");
        }
        return patenteLimpia;
    }
}
