package cl.pruebas.estacionamiento.servicio;

import cl.pruebas.estacionamiento.modelo.EstadoTicket;
import cl.pruebas.estacionamiento.modelo.Ticket;
import cl.pruebas.estacionamiento.modelo.TipoVehiculo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class GestorTicketsTest {

    @Test
    void registrarEntradaCreaTicketAbierto() {
        CalculadoraTarifas calculadora = new CalculadoraTarifas();
        GestorTickets gestor = new GestorTickets(calculadora);
        LocalDateTime entrada = LocalDateTime.of(2024, 6, 10, 10, 0);

        Ticket ticket = gestor.registrarEntrada("ABC123", TipoVehiculo.AUTO, entrada);

        Assertions.assertEquals(1, ticket.getId());
        Assertions.assertEquals("ABC123", ticket.getPatente());
        Assertions.assertEquals(TipoVehiculo.AUTO, ticket.getTipoVehiculo());
        Assertions.assertEquals(entrada, ticket.getFechaHoraEntrada());
        Assertions.assertEquals(EstadoTicket.ABIERTO, ticket.getEstado());
    }

    @Test
    void registrarSalidaCierraTicketYCalculaMonto() {
        CalculadoraTarifas calculadora = new CalculadoraTarifas();
        GestorTickets gestor = new GestorTickets(calculadora);
        LocalDateTime entrada = LocalDateTime.of(2024, 6, 10, 10, 0);
        LocalDateTime salida = LocalDateTime.of(2024, 6, 10, 11, 0);

        Ticket ticket = gestor.registrarEntrada("DEF456", TipoVehiculo.MOTO, entrada);
        Ticket cerrado = gestor.registrarSalida(ticket.getId(), salida);

        Assertions.assertEquals(EstadoTicket.CERRADO, cerrado.getEstado());
        Assertions.assertEquals(salida, cerrado.getFechaHoraSalida());
        Assertions.assertTrue(cerrado.getMontoCobrado() > 0);
    }

    @Test
    void registrarSalidaTicketInexistenteLanzaExcepcion() {
        CalculadoraTarifas calculadora = new CalculadoraTarifas();
        GestorTickets gestor = new GestorTickets(calculadora);
        LocalDateTime salida = LocalDateTime.of(2024, 6, 10, 11, 0);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> gestor.registrarSalida(99, salida));
    }

    @Test
    void registrarSalidaTicketYaCerradoLanzaExcepcion() {
        CalculadoraTarifas calculadora = new CalculadoraTarifas();
        GestorTickets gestor = new GestorTickets(calculadora);
        LocalDateTime entrada = LocalDateTime.of(2024, 6, 10, 10, 0);
        LocalDateTime salida = LocalDateTime.of(2024, 6, 10, 11, 0);

        Ticket ticket = gestor.registrarEntrada("GHI789", TipoVehiculo.AUTO, entrada);
        gestor.registrarSalida(ticket.getId(), salida);

        Assertions.assertThrows(IllegalStateException.class,
                () -> gestor.registrarSalida(ticket.getId(), salida.plusMinutes(30)));
    }

    @Test
    void listasAbiertosYCerradosSeparanCorrectamente() {
        CalculadoraTarifas calculadora = new CalculadoraTarifas();
        GestorTickets gestor = new GestorTickets(calculadora);
        LocalDateTime entrada1 = LocalDateTime.of(2024, 6, 10, 10, 0);
        LocalDateTime entrada2 = LocalDateTime.of(2024, 6, 10, 11, 0);
        LocalDateTime salida1 = LocalDateTime.of(2024, 6, 10, 12, 0);

        Ticket t1 = gestor.registrarEntrada("AAA111", TipoVehiculo.AUTO, entrada1);
        Ticket t2 = gestor.registrarEntrada("BBB222", TipoVehiculo.MOTO, entrada2);
        gestor.registrarSalida(t1.getId(), salida1);

        List<Ticket> abiertos = gestor.obtenerTicketsAbiertos();
        List<Ticket> cerrados = gestor.obtenerTicketsCerrados();

        Assertions.assertEquals(1, abiertos.size());
        Assertions.assertEquals(t2.getId(), abiertos.get(0).getId());
        Assertions.assertEquals(1, cerrados.size());
        Assertions.assertEquals(t1.getId(), cerrados.get(0).getId());
    }

    @Test
    void totalRecaudadoUsaFechaDeSalida() {
        CalculadoraTarifas calculadora = new CalculadoraTarifas();
        GestorTickets gestor = new GestorTickets(calculadora);

        LocalDateTime entrada1 = LocalDateTime.of(2024, 6, 10, 10, 0);
        LocalDateTime salida1 = LocalDateTime.of(2024, 6, 10, 11, 0);

        LocalDateTime entrada2 = LocalDateTime.of(2024, 6, 9, 23, 0);
        LocalDateTime salida2 = LocalDateTime.of(2024, 6, 10, 1, 0);

        Ticket t1 = gestor.registrarEntrada("AAA111", TipoVehiculo.AUTO, entrada1);
        Ticket t2 = gestor.registrarEntrada("BBB222", TipoVehiculo.CAMIONETA, entrada2);

        gestor.registrarSalida(t1.getId(), salida1);
        gestor.registrarSalida(t2.getId(), salida2);

        int totalDia10 = gestor.calcularTotalRecaudado(LocalDate.of(2024, 6, 10));
        int totalDia9 = gestor.calcularTotalRecaudado(LocalDate.of(2024, 6, 9));

        Assertions.assertTrue(totalDia10 > 0);
        Assertions.assertEquals(0, totalDia9);
    }

    @Test
    void registrarEntradaConPatenteMuyLargaLanzaExcepcion() {
        CalculadoraTarifas calculadora = new CalculadoraTarifas();
        GestorTickets gestor = new GestorTickets(calculadora);
        LocalDateTime entrada = LocalDateTime.of(2024, 6, 10, 10, 0);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> gestor.registrarEntrada("123ABCD", TipoVehiculo.AUTO, entrada));
    }
}
