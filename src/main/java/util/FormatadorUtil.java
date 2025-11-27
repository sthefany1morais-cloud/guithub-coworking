package main.java.util;

import main.java.model.espacos.Auditorio;
import main.java.model.espacos.Espaco;
import main.java.model.espacos.SalaDeReuniao;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class FormatadorUtil {

    private static final DecimalFormat DINHEIRO_FORMAT = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));

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

    public static String formatarDinheiro(double valor) {
        return DINHEIRO_FORMAT.format(valor);
    }

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
}

