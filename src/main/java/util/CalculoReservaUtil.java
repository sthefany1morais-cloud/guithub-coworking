package main.java.util;

import main.java.model.espacos.Espaco;
import main.java.model.espacos.SalaDeReuniao;

import java.time.Duration;
import java.time.LocalDateTime;

public class CalculoReservaUtil {

    /**
     * Calcula horas entre datas.
     */
    public static double calcularHoras(LocalDateTime inicio, LocalDateTime fim) {
        long minutos = Duration.between(inicio, fim).toMinutes();
        return minutos / 60.0;
    }

    /**
     * Calcula valor de reserva.
     */
    public static double calcularValor(Espaco espaco, double horas, boolean projetor) {
        if (espaco instanceof SalaDeReuniao) {
            return ((SalaDeReuniao) espaco).calcularCustoReserva(horas, projetor);
        }
        return espaco.calcularCustoReserva(horas);
    }

    /**
     * Calcula reembolso.
     */
    public static double calcularReembolso(double valorOriginal, LocalDateTime agora, LocalDateTime inicio) {
        if (agora.isBefore(inicio.minusHours(24))) {
            return valorOriginal;
        } else {
            return valorOriginal * 0.8;
        }
    }

    /**
     * Calcula custo para exibição em view.
     */
    public static double calcularCustoParaView(Espaco espaco, LocalDateTime inicio, LocalDateTime fim, boolean projetor) {
        double horas = calcularHoras(inicio, fim);
        return calcularValor(espaco, horas, projetor);
    }
}
