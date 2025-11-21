package main.java.service;

import main.java.dao.adaptacao.PagamentoDAO;
import main.java.execoes.PagamentoInexistenteException;
import main.java.model.pagamentos.Pagamento;
import main.java.model.reservas.Reserva;

import java.time.LocalDateTime;
import java.util.*;

public class PagamentoService {

    private final PagamentoDAO pagamentoDAO = new PagamentoDAO();

    public PagamentoService() {}

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

    public void cancelarPagamento(Reserva reserva) {

        Pagamento pagamento = reserva.getPagamento();
            pagamento.setValorPago(reserva.getValorCalculado());
            pagamento.setData(LocalDateTime.now());
            pagamento.setIdDaReserva(reserva.getId());
            pagamentoDAO.atualizar(pagamento);
    }
}

