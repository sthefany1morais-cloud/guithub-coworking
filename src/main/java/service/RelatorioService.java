package main.java.service;

import main.java.model.espacos.Espaco;
import main.java.model.pagamentos.MetodoDePagamento;
import main.java.model.pagamentos.Pagamento;
import main.java.model.reservas.Reserva;

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

    /* ============================================================
                       MÉTODOS AUXILIARES PRIVADOS
       ============================================================ */

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

    private double calcularHorasNoPeriodo(Reserva r, LocalDateTime inicio, LocalDateTime fim) {
        if (r.getInicio() == null || r.getFim() == null) return 0;

        LocalDateTime from = r.getInicio().isAfter(inicio) ? r.getInicio() : inicio;
        LocalDateTime to   = r.getFim().isBefore(fim) ? r.getFim() : fim;

        if (!from.isBefore(to)) return 0;

        return Duration.between(from, to).toMinutes() / 60.0;
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


    /* ============================================================
                        RELATÓRIOS SOBRE RESERVAS
       ============================================================ */

    /** Lista todas as reservas, com filtro opcional de status */
    public List<Reserva> reservas(Boolean ativo) {
        return reservasFiltradas(ativo);
    }

    /** Listar reservas por ID do espaço */
    public List<Reserva> reservasPorEspaco(int idEspaco, Boolean ativo) {
        return reservasFiltradas(ativo).stream()
                .filter(r -> r.getEspaco() != null && r.getEspaco().getId() == idEspaco)
                .collect(Collectors.toList());
    }

    /** Listar reservas filtrando pelo tipo de espaço (SalaDeReuniao, CabineIndividual, Auditorio) */
    public <T extends Espaco> List<Reserva> reservasPorTipoEspaco(Class<T> tipo, Boolean ativo) {
        return reservasFiltradas(ativo).stream()
                .filter(r -> r.getEspaco() != null && tipo.isAssignableFrom(r.getEspaco().getClass()))
                .collect(Collectors.toList());
    }

    /** Listar reservas dentro de um período */
    public List<Reserva> reservasPorPeriodo(LocalDateTime inicio, LocalDateTime fim, Boolean ativo) {
        return reservasFiltradas(ativo).stream()
                .filter(r -> reservaNoPeriodo(r, inicio, fim))
                .collect(Collectors.toList());
    }

    /** Contar reservas por espaço */
    public Map<Integer, Long> totalReservasPorEspaco() {
        return reservasFiltradas(null).stream()
                .filter(r -> r.getEspaco() != null)
                .collect(Collectors.groupingBy(r -> r.getEspaco().getId(), Collectors.counting()));
    }

    /** Contar reservas por espaço em um período */
    public Map<Integer, Long> totalReservasPorEspaco(LocalDateTime inicio, LocalDateTime fim) {
        return reservasFiltradas(null).stream()
                .filter(r -> r.getEspaco() != null)
                .filter(r -> reservaNoPeriodo(r, inicio, fim))
                .collect(Collectors.groupingBy(r -> r.getEspaco().getId(), Collectors.counting()));
    }

    /** Top N espaços mais utilizados */
    public List<Map.Entry<Integer, Long>> topEspacosMaisUsados(int topN) {
        return totalReservasPorEspaco().entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }

    /** Horas reservadas por espaço (considerando sobreposição com o período informado) */
    public Map<Integer, Double> horasReservadas(LocalDateTime inicio, LocalDateTime fim) {
        return reservasFiltradas(true).stream()  // Sempre só ativas
                .filter(r -> r.getEspaco() != null)
                .filter(r -> reservaNoPeriodo(r, inicio, fim))  // Só totalmente dentro
                .collect(Collectors.groupingBy(
                        r -> r.getEspaco().getId(),
                        Collectors.summingDouble(r -> Duration.between(r.getInicio(), r.getFim()).toMinutes() / 60.0)
                ));
    }

    /* ============================================================
                      RELATÓRIOS SOBRE PAGAMENTOS
       ============================================================ */

    /** Pagamentos dentro de um período */
    public List<Pagamento> pagamentosPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return pagamentosNoPeriodo(inicio, fim);
    }

    /** Contar pagamentos realizados no período */
    public long totalPagamentos(LocalDateTime inicio, LocalDateTime fim) {
        return pagamentosNoPeriodo(inicio, fim).size();
    }

    /** Faturamento por método (Pix, Cartão, Dinheiro) */
    public double faturamentoPorMetodo(MetodoDePagamento metodo, LocalDateTime inicio, LocalDateTime fim) {
        return pagamentosNoPeriodo(inicio, fim).stream()
                .filter(p -> p.getMetodo() == metodo)
                .mapToDouble(Pagamento::getValorPago)
                .sum();
    }

    /** Faturamento total */
    public double faturamentoTotal() {
        return pagamentoService.listarTodos().stream()
                .mapToDouble(Pagamento::getValorPago)
                .sum();
    }


    /* ============================================================
                 RELATÓRIOS DE UTILIZAÇÃO E FATURAMENTO POR ESPAÇOS
       ============================================================ */

    /** Faturamento por espaço (ID → valor total) */
    public Map<Integer, Double> faturamentoPorEspaco(LocalDateTime inicio, LocalDateTime fim) {

        Map<Integer, Double> mapa = new HashMap<>();

        for (Pagamento p : pagamentosNoPeriodo(inicio, fim)) {
            Reserva r = reservaService.buscarPorIdTodos(p.getIdDaReserva());
            if (r == null || r.getEspaco() == null) continue;

            mapa.merge(r.getEspaco().getId(), p.getValorPago(), Double::sum);
        }

        return mapa;
    }

    /** Faturamento por tipo de espaço (SalaDeReuniao, CabineIndividual, Auditorio) */
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

