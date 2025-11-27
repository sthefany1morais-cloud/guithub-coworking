package main.java.util;

import main.java.execoes.EspacoJaExistenteException;
import main.java.execoes.HoraInvalidaException;
import main.java.execoes.ValidacaoException;
import main.java.model.espacos.Espaco;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ValidacaoUtil {

    /**
     * Valida se um nome é único entre espaços existentes e disponíveis.
     */
    public static void validarNomeUnico(String nome, List<Espaco> espacosExistentes) throws EspacoJaExistenteException {
        boolean existe = espacosExistentes.stream()
                .anyMatch(e -> e.isExistente() &&
                        e.isDisponivel() &&
                        e.getNome().equalsIgnoreCase(nome));

        if (existe) {
            throw new EspacoJaExistenteException(
                    "Já existe um espaço com o nome: \"" + nome + "\"."
            );
        }
    }

    /**
     * Valida se um ID é positivo.
     */
    public static boolean isIdValido(int id) {
        return id > 0;
    }

    /**
     * Valida se uma string não é nula ou vazia.
     */
    public static boolean isStringValida(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Valida campos para cadastro/edição de espaços.
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
     * Valida formato de hora (HH:MM).
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

    /**
     * Valida período de datas.
     */
    public static void validarPeriodo(LocalDateTime inicio, LocalDateTime fim) throws ValidacaoException {
        if (inicio == null || fim == null) {
            throw new ValidacaoException(List.of("Datas de início e fim são obrigatórias."));
        }
        if (inicio.isAfter(fim)) {
            throw new ValidacaoException(List.of("Data de início deve ser anterior à de fim."));
        }
    }
}