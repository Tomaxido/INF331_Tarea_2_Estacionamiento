package cl.pruebas.estacionamiento.servicio;

import cl.pruebas.estacionamiento.modelo.TipoVehiculo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class CalculadoraTarifasTest {

    @Test
    void calcularMontoAutoUnaHora() {
        CalculadoraTarifas calculadora = new CalculadoraTarifas();
        LocalDateTime entrada = LocalDateTime.of(2024, 6, 10, 10, 0);
        LocalDateTime salida = LocalDateTime.of(2024, 6, 10, 11, 0);

        int monto = calculadora.calcularMonto(TipoVehiculo.AUTO, entrada, salida);

        Assertions.assertEquals(1600, monto);
    }

    @Test
    void calcularMontoRedondeoHaciaArriba() {
        CalculadoraTarifas calculadora = new CalculadoraTarifas();
        LocalDateTime entrada = LocalDateTime.of(2024, 6, 10, 10, 0);
        LocalDateTime salida = LocalDateTime.of(2024, 6, 10, 10, 5);

        int monto = calculadora.calcularMonto(TipoVehiculo.AUTO, entrada, salida);

        Assertions.assertEquals(800, monto);
    }

    @Test
    void aplicarTopeDiario() {
        CalculadoraTarifas calculadora = new CalculadoraTarifas();
        LocalDateTime entrada = LocalDateTime.of(2024, 6, 10, 8, 0);
        LocalDateTime salida = LocalDateTime.of(2024, 6, 10, 22, 0);

        int monto = calculadora.calcularMonto(TipoVehiculo.CAMIONETA, entrada, salida);

        Assertions.assertEquals(calculadora.getTopeDiario(), monto);
    }

    @Test
    void aplicarDescuentoFinDeSemana() {
        CalculadoraTarifas calculadora = new CalculadoraTarifas();
        LocalDateTime entrada = LocalDateTime.of(2024, 6, 15, 10, 0);
        LocalDateTime salida = LocalDateTime.of(2024, 6, 15, 12, 0);

        int monto = calculadora.calcularMonto(TipoVehiculo.AUTO, entrada, salida);

        Assertions.assertEquals(2880, monto);
    }

    @Test
    void duracionInvalidaLanzaExcepcion() {
        CalculadoraTarifas calculadora = new CalculadoraTarifas();
        LocalDateTime entrada = LocalDateTime.of(2024, 6, 10, 10, 0);
        LocalDateTime salida = LocalDateTime.of(2024, 6, 10, 9, 59);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcularMonto(TipoVehiculo.AUTO, entrada, salida));
    }

    @Test
    void calcularBloquesBordes() {
        CalculadoraTarifas calculadora = new CalculadoraTarifas();
        Assertions.assertEquals(1, calculadora.calcularBloques(1));
        Assertions.assertEquals(1, calculadora.calcularBloques(30));
        Assertions.assertEquals(2, calculadora.calcularBloques(31));
        Assertions.assertEquals(2, calculadora.calcularBloques(60));
        Assertions.assertEquals(3, calculadora.calcularBloques(61));
    }
}
