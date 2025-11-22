package main.java.service;

import main.java.dao.adaptacao.ReservaDAO;
import main.java.execoes.*;
import main.java.model.espacos.Espaco;
import main.java.model.pagamentos.MetodoDePagamento;
import main.java.model.reservas.Reserva;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReservaService {

    private final ReservaDAO reservaDAO;
    private final EspacoService espacoService;
    private final PagamentoService pagamentoService;

    public ReservaService(ReservaDAO reservaDAO, EspacoService espacoService, PagamentoService pagamentoService) {
        this.reservaDAO = reservaDAO;
        this.espacoService = espacoService;
        this.pagamentoService = pagamentoService;
    }

    public Reserva criarReserva(int idEspaco,
                                LocalDateTime inicio,
                                LocalDateTime fim,
                                MetodoDePagamento metodo,
                                boolean projetor)
            throws EspacoInexistenteException,
            EspacoIndisponivelException,
            DataInvalidaExeption,
            ReservaSobrepostaException {

        Espaco espaco = espacoService.buscarPorId(idEspaco);

        verificarDisponibilidade(idEspaco, inicio, fim);

        Reserva reserva = new Reserva(espaco, inicio, fim, metodo, projetor);

        reservaDAO.salvar(reserva);

        return reserva;
    }

    public void verificarDisponibilidade(int IdEspaco, LocalDateTime inicio, LocalDateTime fim) throws ReservaSobrepostaException {

        List<Reserva> reservas = listarTodos().stream()
                .filter(Reserva::isAtivo)
                .filter(r -> r.getEspaco().getId() == IdEspaco)
                .collect(Collectors.toList());

        for (Reserva r : reservas) {
            LocalDateTime rInicio = r.getInicio();
            LocalDateTime rFim = r.getFim();

            if (inicio.isBefore(rFim) && rInicio.isBefore(fim)) {
                DateTimeFormatter formatar = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                String mensagem = String.format(
                        "Já existe uma reserva no espaço '%s' no horário de %s às %s.",
                        r.getEspaco().getNome(),
                        rInicio.format(formatar),
                        rFim.format(formatar));
                throw new ReservaSobrepostaException(mensagem);
            }
        }
    }

    public boolean possuiReservasAtivas(Espaco espaco){
        return this.listarTodos().stream()
                .filter(r -> r.getEspaco() == espaco)
                .anyMatch(Reserva::isAtivo);
    }

    public double cancelarReserva(int idReserva)
            throws ReservaInexistenteException, ReservaInativaException {

        Reserva reserva = buscarPorId(idReserva);

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicio = reserva.getInicio();

        double valorOriginal = reserva.getValorCalculado();
        double valorReembolsado;

        if (agora.isBefore(inicio.minusHours(24))) {
            valorReembolsado = valorOriginal;
        } else {
            valorReembolsado = valorOriginal * 0.8;
        }

        reserva.setAtivo(false);

        this.pagamentoService.cancelarPagamento(reserva,(valorOriginal - valorReembolsado));
        reservaDAO.atualizar(reserva);
        return valorReembolsado;
    }

    public Reserva buscarPorId(int id) throws ReservaInexistenteException, ReservaInativaException {
        Reserva r = reservaDAO.buscarPorId(id);
        if (r == null) {
            throw new ReservaInexistenteException("Reserva inexistente.");
        } else if (!r.isAtivo()) {
            throw new ReservaInativaException("Reserva cancelada.");
        }
        return r;
    }

    protected Reserva buscarPorIdTodos(int id) {
        return reservaDAO.buscarPorId(id);
    }

    public List<Reserva> listarTodos(){
        return reservaDAO.carregarTodos();
    }
}