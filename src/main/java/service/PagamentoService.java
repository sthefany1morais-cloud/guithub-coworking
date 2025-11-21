package main.java.service;

import main.java.dao.adaptacao.PagamentoDAO;
import main.java.execoes.PagamentoInexistenteException;
import main.java.model.pagamentos.Pagamento;
import main.java.model.reservas.Reserva;

import java.time.LocalDateTime;
import java.util.*;

public class PagamentoService {

    private PagamentoDAO pagamentoDAO;

    public PagamentoService(PagamentoDAO pagamentoDAO) {
        this.pagamentoDAO = pagamentoDAO;
    }

    public Pagamento buscarPorId(int id) throws PagamentoInexistenteException {
        Pagamento p = pagamentoDAO.buscarPorId(id);
        if (p == null){
            throw new PagamentoInexistenteException("Pagamento inexistente");
        }
        return p;
    }

    public List<Pagamento> listarTodos() {
        return pagamentoDAO.carregarTodos();
    }

    public void atualizar(Pagamento p) {
        pagamentoDAO.atualizar(p);
    }

    protected void cancelarPagamento(Reserva reserva, double valor) {

        Pagamento pagamento = reserva.getPagamento();
            pagamento.setValorPago(valor);
            pagamento.setData(LocalDateTime.now());
            pagamento.setIdDaReserva(reserva.getId());
            pagamentoDAO.atualizar(pagamento);
    }
}

