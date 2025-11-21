package main.java.execoes;

public class PagamentoInexistenteException extends Exception{
    public PagamentoInexistenteException(String message) {
        super(message);
    }
}
