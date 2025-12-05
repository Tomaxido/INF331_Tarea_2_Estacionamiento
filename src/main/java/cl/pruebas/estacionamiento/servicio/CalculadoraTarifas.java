package cl.pruebas.estacionamiento.servicio;

import cl.pruebas.estacionamiento.modelo.TipoVehiculo;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;

public class CalculadoraTarifas {

    private static final int MINUTOS_POR_BLOQUE = 30;
    private static final int TARIFA_AUTO = 800;
    private static final int TARIFA_MOTO = 500;
    private static final int TARIFA_CAMIONETA = 1000;
    private static final int TOPE_DIARIO = 15000;
    private static final int PORCENTAJE_DESCUENTO_FIN_DE_SEMANA = 10;

    public int calcularMonto(TipoVehiculo tipoVehiculo, LocalDateTime fechaHoraEntrada, LocalDateTime fechaHoraSalida) {
        long minutos = Duration.between(fechaHoraEntrada, fechaHoraSalida).toMinutes();
        if (minutos <= 0) {
            throw new IllegalArgumentException("Duración inválida");
        }

        int bloques = calcularBloques(minutos);
        int tarifaPorBloque = obtenerTarifaPorBloque(tipoVehiculo);
        int monto = bloques * tarifaPorBloque;

        if (monto > TOPE_DIARIO) {
            monto = TOPE_DIARIO;
        }

        if (esFinDeSemana(fechaHoraEntrada)) {
            int descuento = monto * PORCENTAJE_DESCUENTO_FIN_DE_SEMANA / 100;
            monto = monto - descuento;
        }

        return monto;
    }

    int calcularBloques(long minutos) {
        return (int) ((minutos + MINUTOS_POR_BLOQUE - 1) / MINUTOS_POR_BLOQUE);
    }

    int obtenerTarifaPorBloque(TipoVehiculo tipoVehiculo) {
        return switch (tipoVehiculo) {
            case AUTO -> TARIFA_AUTO;
            case MOTO -> TARIFA_MOTO;
            case CAMIONETA -> TARIFA_CAMIONETA;
        };
    }

    boolean esFinDeSemana(LocalDateTime fechaHoraEntrada) {
        DayOfWeek dia = fechaHoraEntrada.getDayOfWeek();
        return dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY;
    }

    public int getTopeDiario() {
        return TOPE_DIARIO;
    }
}
