package main.java.util;

import main.java.service.EspacoService;
import main.java.service.ReservaService;

public class VerificacaoUtil {

    /**
     * Verifica estado de dados para menus (ex.: se há espaços ou reservas).
     */
    public static String verificarEstadoDados(EspacoService espacoService, ReservaService reservaService) {
        if (espacoService.listarTodos().isEmpty()) {
            return "Nenhum espaço cadastrado. Comece criando espaços.";
        } else if (reservaService.listarTodos().isEmpty()) {
            return "Nenhuma reserva encontrada. Crie reservas.";
        } else {
            return "";
        }
    }

    /**
     * Verifica se há espaços existentes.
     */
    public static String verificarEspacos(EspacoService espacoService) {
        if (espacoService.listarTodos().isEmpty()) {
            return "Nenhum espaço encontrado. Crie um novo espaço primeiro.";
        } else {
            return "";
        }
    }

    /**
     * Verifica se há reservas.
     */
    public static String verificarReservas(ReservaService reservaService) {
        if (reservaService.listarTodos().isEmpty()) {
            return "Nenhuma reserva encontrada. Crie uma reserva primeiro.";
        } else {
            return "";
        }
    }
}
