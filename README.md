# INF331_Tarea_2_Estacionamiento
Desarrollo de un sistema que gestiona el cobro de un estacionamiento para distintos tipos de vehículos.

# Calculadora de Tarifas de Estacionamiento

Aplicación de consola en Java que gestiona tickets de estacionamiento y calcula el monto a pagar según tipo de vehículo, tiempo estacionado, tope diario y descuento de fin de semana.

## Diseño general

Se modelan los tickets y la lógica de negocio de manera separada:

- `modelo.Ticket`: representa un ticket con id, patente, tipo de vehículo, fecha/hora de entrada y salida, estado y monto cobrado.
- `modelo.TipoVehiculo`: enum con AUTO, MOTO, CAMIONETA.
- `modelo.EstadoTicket`: enum con ABIERTO y CERRADO.
- `servicio.CalculadoraTarifas`: encapsula las reglas de cálculo de minutos, bloques de 30 minutos, tarifas por tipo de vehículo, tope diario y descuento de fin de semana.

## Requisitos

- Java 21
- Maven 3.x

Para verificar versiones:

```bash
java -version
mvn -version