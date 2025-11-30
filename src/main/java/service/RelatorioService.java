package main.java.service;

import main.java.model.espacos.Espaco;
import main.java.model.pagamentos.Pagamento;
import main.java.model.reservas.Reserva;
import main.java.util.CalculoReservaUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class RelatorioService {

    private final ReservaService reservaService;
    private final PagamentoService pagamentoService;
    private final EspacoService espacoService;

    public RelatorioService(ReservaService reservaService, PagamentoService pagamentoService, EspacoService espacoService) {
        this.reservaService = reservaService;
        this.pagamentoService = pagamentoService;
        this.espacoService = espacoService;
    }

    private boolean statusAtende(Reserva r, Boolean ativo) {
        if (ativo == null) return true;
        return ativo ? r.isAtivo() : !r.isAtivo();
    }

    private boolean reservaNoPeriodo(Reserva r, LocalDateTime inicio, LocalDateTime fim) {
        if (r.getInicio() == null || r.getFim() == null) return false;
        return !r.getInicio().isBefore(inicio) && !r.getFim().isAfter(fim);  // Totalmente dentro
    }

    private boolean pagamentoNoPeriodo(Pagamento p, LocalDateTime inicio, LocalDateTime fim) {
        return !p.getData().isBefore(inicio) && !p.getData().isAfter(fim);
    }

    private List<Reserva> reservasFiltradas(Boolean ativo) {
        return reservaService.listarTodos().stream()
                .filter(r -> statusAtende(r, ativo))
                .collect(Collectors.toList());
    }

    private List<Pagamento> pagamentosNoPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return pagamentoService.listarTodos().stream()
                .filter(p -> pagamentoNoPeriodo(p, inicio, fim))
                .collect(Collectors.toList());
    }

    public List<Reserva> reservas(Boolean ativo) {
        return reservasFiltradas(ativo);
    }

    public List<Reserva> reservasPorPeriodo(LocalDateTime inicio, LocalDateTime fim, Boolean ativo) {
        return reservasFiltradas(ativo).stream()
                .filter(r -> reservaNoPeriodo(r, inicio, fim))
                .collect(Collectors.toList());
    }

    public Map<Integer, Long> totalReservasPorEspaco() {
        return reservasFiltradas(null).stream()
                .filter(r -> r.getEspaco() != null)
                .collect(Collectors.groupingBy(r -> r.getEspaco().getId(), Collectors.counting()));
    }

    public List<Map.Entry<Integer, Long>> topEspacosMaisUsados(int topN) {
        Map<Integer, Long> totalReservas = totalReservasPorEspaco();

        // Incluir todos os espaços, com 0 reservas se não tiverem
        Map<Integer, Long> todosEspacos = new HashMap<>();
        for (Espaco espaco : espacoService.listarTodos()) {
            todosEspacos.put(espaco.getId(), 0L);
        }

        // Merge com reservas reais
        for (Map.Entry<Integer, Long> entry : totalReservas.entrySet()) {
            todosEspacos.put(entry.getKey(), entry.getValue());
        }

        return todosEspacos.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }

    public Map<Integer, Double> horasReservadas(LocalDateTime inicio, LocalDateTime fim) {
        return reservasFiltradas(true).stream()  // Sempre só ativas
                .filter(r -> r.getEspaco() != null)
                .filter(r -> reservaNoPeriodo(r, inicio, fim))  // Só totalmente dentro
                .collect(Collectors.groupingBy(
                        r -> r.getEspaco().getId(),
                        Collectors.summingDouble(r -> CalculoReservaUtil.calcularHoras(r.getInicio(), r.getFim()))
                ));
    }

    public Map<String, Double> faturamentoPorTipoEspaco(LocalDateTime inicio, LocalDateTime fim) {

        Map<String, Double> mapa = new HashMap<>();

        for (Pagamento p : pagamentosNoPeriodo(inicio, fim)) {
            Reserva r = reservaService.buscarPorIdTodos(p.getIdDaReserva());
            if (r == null || r.getEspaco() == null) continue;

            String tipo = r.getEspaco().getClass().getSimpleName();
            mapa.merge(tipo, p.getValorPago(), Double::sum);
        }

        return mapa;
    }
}