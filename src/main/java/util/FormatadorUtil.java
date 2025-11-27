package main.java.util;

import main.java.model.espacos.Auditorio;
import main.java.model.espacos.Espaco;
import main.java.model.espacos.SalaDeReuniao;
import main.java.model.reservas.Reserva;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormatadorUtil {

    private static final DecimalFormat DINHEIRO_FORMAT = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));

    /**
     * Formata um valor numérico como dinheiro brasileiro (ex.: "1234" -> "12,34").
     */
    public static String formatarDinheiro(String input) {
        String digits = input.replaceAll("\\D", "");
        if (digits.isEmpty()) return "0,00";
        if (digits.length() == 1) return "0,0" + digits;
        if (digits.length() == 2) return "0," + digits;
        String integerPart = digits.substring(0, digits.length() - 2).replaceFirst("^0+", "");
        if (integerPart.isEmpty()) integerPart = "0";
        String decimalPart = digits.substring(digits.length() - 2);
        return integerPart + "," + decimalPart;
    }

    /**
     * Formata um valor double como dinheiro brasileiro (ex.: 1234.56 -> "1.234,56").
     */
    public static String formatarDinheiro(double valor) {
        return DINHEIRO_FORMAT.format(valor);
    }

    /**
     * Formata uma data/hora para exibição em português (dd/MM/yyyy HH:mm).
     */
    public static String formatarDataHora(LocalDateTime dataHora) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dataHora.format(formatter);
    }

    /**
     * Formata detalhes de um espaço para exibição (ex.: em labels).
     */
    public static String formatarDetalhesEspaco(Espaco espaco) {
        String detalhes = "Nome: " + espaco.getNome() + " | Tipo: " + espaco.getClass().getSimpleName() +
                " | Capacidade: " + espaco.getCapacidade() + " | Preço: R$" + formatarDinheiro(espaco.getPrecoPorHora());
        if (espaco instanceof SalaDeReuniao) {
            detalhes += " | Taxa Fixa: R$" + formatarDinheiro(((SalaDeReuniao) espaco).getTaxaFixa());
        } else if (espaco instanceof Auditorio) {
            detalhes += " | Custo Adicional: R$" + formatarDinheiro(((Auditorio) espaco).getCustoAdicional());
        }
        return detalhes;
    }

    /**
     * Formata detalhes de uma reserva para exibição (ex.: em relatórios).
     */
    public static String formatarDetalhesReserva(Reserva reserva) {
        return "ID: " + reserva.getId() + " | Espaço: " + reserva.getEspaco().getNome() +
                " | Início: " + formatarDataHora(reserva.getInicio()) + " | Fim: " + formatarDataHora(reserva.getFim()) +
                " | Valor: R$" + formatarDinheiro(reserva.getValorCalculado());
    }

    /**
     * Formata uma tabela simples como string (para console ou logs).
     */
    public static String formatarTabelaSimples(String[] cabecalhos, String[][] dados) {
        StringBuilder tabela = new StringBuilder();
        // Adicionar cabeçalhos
        for (String cabecalho : cabecalhos) {
            tabela.append(String.format("%-20s", cabecalho));
        }
        tabela.append("\n");
        // Adicionar linhas
        for (String[] linha : dados) {
            for (String celula : linha) {
                tabela.append(String.format("%-20s", celula));
            }
            tabela.append("\n");
        }
        return tabela.toString();
    }
}

