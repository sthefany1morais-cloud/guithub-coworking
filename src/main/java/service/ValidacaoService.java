// Nova classe: ValidacaoService (em main.java.service)
package main.java.service;

import main.java.execoes.EspacoJaExistenteException;
import main.java.execoes.ValidacaoException;
import main.java.model.espacos.Espaco;

import java.time.LocalDateTime;
import java.util.List;

public class ValidacaoService {

    /**
     * Valida se um nome é único entre espaços existentes e disponíveis (lógica de negócio).
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
     * Valida período de datas (lógica de negócio).
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

// Classe ajustada: ValidacaoUtil (em main.java.util) - Apenas auxiliares leves de UI
