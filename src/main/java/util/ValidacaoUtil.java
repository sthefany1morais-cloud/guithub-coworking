package main.java.util;

import main.java.execoes.HoraInvalidaException;

import java.util.ArrayList;
import java.util.List;

public class ValidacaoUtil {

    /**
     * Valida se um ID é positivo (auxiliar leve).
     */
    public static boolean isIdValido(int id) {
        return id > 0;
    }

    /**
     * Valida se uma string não é nula ou vazia (auxiliar leve).
     */
    public static boolean isStringValida(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Valida campos para cadastro/edição de espaços (auxiliar leve para UI).
     */
    public static List<String> validarCamposEspaco(String nome, String capText, String precoText, String tipo, String especificoText) {
        List<String> erros = new ArrayList<>();
        if (nome.isEmpty()) erros.add("Nome não pode ser vazio.");
        if (capText.isEmpty() || !capText.matches("\\d+")) erros.add("Capacidade deve ser um número positivo.");
        else if (Integer.parseInt(capText) <= 0) erros.add("Capacidade não pode ser zero ou negativa.");
        if (precoText.isEmpty() || !precoText.matches("\\d+\\,?\\d*")) erros.add("Preço por hora deve ser um número positivo.");
        else if (Double.parseDouble(precoText.replace(",", ".")) <= 0) erros.add("Preço por hora não pode ser zero ou negativo.");
        if ("Sala de Reunião".equals(tipo) && (especificoText.isEmpty() || !especificoText.matches("\\d+\\,?\\d*"))) erros.add("Taxa fixa deve ser um número positivo.");
        else if ("Sala de Reunião".equals(tipo) && Double.parseDouble(especificoText.replace(",", ".")) <= 0) erros.add("Taxa fixa não pode ser zero ou negativa.");
        if ("Auditório".equals(tipo) && (especificoText.isEmpty() || !especificoText.matches("\\d+\\,?\\d*"))) erros.add("Custo adicional deve ser um número positivo.");
        else if ("Auditório".equals(tipo) && Double.parseDouble(especificoText.replace(",", ".")) <= 0) erros.add("Custo adicional não pode ser zero ou negativa.");
        return erros;
    }

    /**
     * Valida formato de hora (HH:MM) (auxiliar leve para UI).
     */
    public static void validarHora(String hora) throws HoraInvalidaException {
        if (!hora.matches("\\d{2}:\\d{2}")) {
            throw new HoraInvalidaException("Hora inválida. Deve seguir o formato HH:MM (00:00-23:59).");
        }
        int h = Integer.parseInt(hora.substring(0, 2));
        int m = Integer.parseInt(hora.substring(3));
        if (h > 23 || m > 59) {
            throw new HoraInvalidaException("Hora inválida. Deve seguir o formato HH:MM (00:00-23:59).");
        }
    }
}