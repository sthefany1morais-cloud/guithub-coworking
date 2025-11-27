package main.java.util;

import main.java.service.EspacoService;
import main.java.service.ReservaService;

public class VerificacaoUtil {

    public static String verificarEstadoDados(EspacoService espacoService, ReservaService reservaService) {
        if (espacoService.listarTodos().isEmpty()) {
            return "Nenhum espaço cadastrado. Comece criando espaços.";
        } else if (reservaService.listarTodos().isEmpty()) {
            return "Nenhuma reserva encontrada. Crie reservas.";
        } else {
            return "";
        }
    }

    public static String verificarEspacos(EspacoService espacoService) {
        if (espacoService.listarTodos().isEmpty()) {
            return "Nenhum espaço encontrado. Crie um novo espaço primeiro.";
        } else {
            return "";
        }
    }

    public static String verificarReservas(ReservaService reservaService) {
        if (reservaService.listarTodos().isEmpty()) {
            return "Nenhuma reserva encontrada. Crie uma reserva primeiro.";
        } else {
            return "";
        }
    }
}
