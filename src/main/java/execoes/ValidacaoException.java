package main.java.execoes;

import java.util.List;

public class ValidacaoException extends Exception {
    private List<String> erros;

    public ValidacaoException(List<String> erros) {
        super("Erros de validação encontrados.");
        this.erros = erros;
    }

    public List<String> getErros() {
        return erros;
    }
}
