package cl.pruebas.estacionamiento;

import cl.pruebas.estacionamiento.modelo.Ticket;
import cl.pruebas.estacionamiento.modelo.TipoVehiculo;
import cl.pruebas.estacionamiento.servicio.CalculadoraTarifas;
import cl.pruebas.estacionamiento.servicio.GestorTickets;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class AplicacionEstacionamiento {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        CalculadoraTarifas calculadoraTarifas = new CalculadoraTarifas();
        GestorTickets gestorTickets = new GestorTickets(calculadoraTarifas);
        Scanner scanner = new Scanner(System.in);

        boolean seguir = true;
        while (seguir) {
            mostrarMenu();
            String linea = scanner.nextLine();
            int opcion;
            try {
                opcion = Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida");
                continue;
            }

            switch (opcion) {
                case 1 -> registrarEntrada(scanner, gestorTickets);
                case 2 -> registrarSalida(scanner, gestorTickets);
                case 3 -> listarTicketsAbiertos(gestorTickets);
                case 4 -> listarTicketsCerrados(gestorTickets);
                case 5 -> mostrarDetalleTicket(scanner, gestorTickets);
                case 6 -> mostrarTotalRecaudadoDiaActual(gestorTickets);
                case 7 -> {
                    System.out.println("Saliendo del sistema");
                    seguir = false;
                }
                default -> System.out.println("Opción inválida");
            }
        }

        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("=== Menú Estacionamiento ===");
        System.out.println("1. Registrar entrada de vehículo");
        System.out.println("2. Registrar salida de vehículo");
        System.out.println("3. Listar tickets abiertos");
        System.out.println("4. Listar tickets cerrados");
        System.out.println("5. Mostrar detalle de un ticket");
        System.out.println("6. Mostrar total recaudado del día");
        System.out.println("7. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void registrarEntrada(Scanner scanner, GestorTickets gestor) {
        System.out.print("Patente: ");
        String patente = scanner.nextLine();
        TipoVehiculo tipo = leerTipoVehiculo(scanner);

        LocalDateTime ahora = LocalDateTime.now();
        try {
            Ticket ticket = gestor.registrarEntrada(patente, tipo, ahora);
            System.out.println("Ticket creado con id " + ticket.getId() + " a las " + ahora.format(FORMATO_FECHA_HORA));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void registrarSalida(Scanner scanner, GestorTickets gestor) {
        System.out.print("Id de ticket a cerrar: ");
        String linea = scanner.nextLine();
        int id;
        try {
            id = Integer.parseInt(linea);
        } catch (NumberFormatException e) {
            System.out.println("Id inválido");
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();
        try {
            Ticket ticket = gestor.registrarSalida(id, ahora);
            System.out.println("Ticket " + ticket.getId() + " cerrado.");
            System.out.println("Monto cobrado: $" + ticket.getMontoCobrado());
            System.out.println("Hora salida: " + ahora.format(FORMATO_FECHA_HORA));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listarTicketsAbiertos(GestorTickets gestor) {
        List<Ticket> abiertos = gestor.obtenerTicketsAbiertos();
        if (abiertos.isEmpty()) {
            System.out.println("No hay tickets abiertos.");
            return;
        }
        System.out.println("Tickets abiertos:");
        for (Ticket ticket : abiertos) {
            System.out.println("Id: " + ticket.getId()
                    + " | Patente: " + ticket.getPatente()
                    + " | Tipo: " + ticket.getTipoVehiculo()
                    + " | Entrada: " + ticket.getFechaHoraEntrada().format(FORMATO_FECHA_HORA));
        }
    }

    private static void listarTicketsCerrados(GestorTickets gestor) {
        List<Ticket> cerrados = gestor.obtenerTicketsCerrados();
        if (cerrados.isEmpty()) {
            System.out.println("No hay tickets cerrados.");
            return;
        }
        System.out.println("Tickets cerrados:");
        for (Ticket ticket : cerrados) {
            System.out.println("Id: " + ticket.getId()
                    + " | Patente: " + ticket.getPatente()
                    + " | Tipo: " + ticket.getTipoVehiculo()
                    + " | Entrada: " + ticket.getFechaHoraEntrada().format(FORMATO_FECHA_HORA)
                    + " | Salida: " + ticket.getFechaHoraSalida().format(FORMATO_FECHA_HORA)
                    + " | Monto: $" + ticket.getMontoCobrado());
        }
    }

    private static void mostrarDetalleTicket(Scanner scanner, GestorTickets gestor) {
        System.out.print("Id de ticket: ");
        String linea = scanner.nextLine();
        int id;
        try {
            id = Integer.parseInt(linea);
        } catch (NumberFormatException e) {
            System.out.println("Id inválido");
            return;
        }

        Ticket ticket = gestor.buscarPorId(id);
        if (ticket == null) {
            System.out.println("No se encontró el ticket.");
            return;
        }

        System.out.println("Detalle ticket " + ticket.getId());
        System.out.println("Patente: " + ticket.getPatente());
        System.out.println("Tipo: " + ticket.getTipoVehiculo());
        System.out.println("Entrada: " + ticket.getFechaHoraEntrada().format(FORMATO_FECHA_HORA));
        if (ticket.getFechaHoraSalida() == null) {
            System.out.println("Estado: ABIERTO");
            System.out.println("Tiempo estacionado: en curso");
        } else {
            System.out.println("Salida: " + ticket.getFechaHoraSalida().format(FORMATO_FECHA_HORA));
            long minutos = java.time.Duration.between(ticket.getFechaHoraEntrada(), ticket.getFechaHoraSalida()).toMinutes();
            System.out.println("Tiempo estacionado: " + minutos + " minutos");
            System.out.println("Estado: CERRADO");
            System.out.println("Monto cobrado: $" + ticket.getMontoCobrado());
        }
    }

    private static void mostrarTotalRecaudadoDiaActual(GestorTickets gestor) {
        LocalDate hoy = LocalDate.now();
        int total = gestor.calcularTotalRecaudado(hoy);
        System.out.println("Total recaudado para el día " + hoy + ": $" + total);
    }

    private static TipoVehiculo leerTipoVehiculo(Scanner scanner) {
        System.out.println("Tipo de vehículo:");
        System.out.println("1. Auto");
        System.out.println("2. Moto");
        System.out.println("3. Camioneta");
        System.out.print("Seleccione una opción: ");
        String linea = scanner.nextLine();
        switch (linea) {
            case "1":
                return TipoVehiculo.AUTO;
            case "2":
                return TipoVehiculo.MOTO;
            case "3":
                return TipoVehiculo.CAMIONETA;
            default:
                System.out.println("Opción inválida, se asumirá AUTO.");
                return TipoVehiculo.AUTO;
        }
    }
}
