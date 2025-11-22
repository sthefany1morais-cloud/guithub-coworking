package main.java.model.reservas;

import main.java.execoes.DataInvalidaExeption;
import main.java.model.espacos.Espaco;
import main.java.model.espacos.SalaDeReuniao;
import main.java.model.pagamentos.MetodoDePagamento;
import main.java.model.pagamentos.Pagamento;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity

public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Espaco espaco;

    private LocalDateTime inicio;
    private LocalDateTime fim;
    private double valorCalculado;

    @OneToOne(cascade = CascadeType.ALL)
    private Pagamento pagamento;

    private boolean projetor;

    private boolean ativo;

    protected Reserva() {
    }

    public Reserva(Espaco espaco, LocalDateTime inicio, LocalDateTime fim, MetodoDePagamento metodo, boolean projetor) throws DataInvalidaExeption{
        this.espaco = espaco;
        this.inicio = inicio;
        this.fim = fim;
        this.projetor = projetor;
        double horas = calcularHoras(inicio, fim);
        this.valorCalculado = calcularValor(horas);
        this.pagamento = new Pagamento(0, this.valorCalculado, LocalDateTime.now(),metodo);
        this.ativo = true;
    }

    @PostPersist
    private void atualizarPagamentoComIdReserva() {
        if (pagamento != null) {
            pagamento.setIdDaReserva(this.id);
        }
    }

    private double calcularHoras(LocalDateTime inicio, LocalDateTime fim) throws DataInvalidaExeption {

        if (!inicio.isBefore(fim)){
            throw new DataInvalidaExeption("A data inicial deve ser anterior à final.");
        }

        long minutos = Duration.between(inicio, fim).toMinutes();
        return minutos/60.0;
    }

    private double calcularValor(double horas){
        if (espaco instanceof SalaDeReuniao){
            return ((SalaDeReuniao) espaco).calcularCustoReserva(horas, projetor);
        }
        return this.espaco.calcularCustoReserva(horas);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Espaco getEspaco() {
        return espaco;
    }

    public void setEspaco(Espaco espaco) {
        this.espaco = espaco;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public void setFim(LocalDateTime fim) {
        this.fim = fim;
    }

    public double getValorCalculado() {
        return valorCalculado;
    }

    public void setValorCalculado(double valorCalculado) {
        this.valorCalculado = valorCalculado;
    }

    public boolean isProjetor() {
        return projetor;
    }

    public void setProjetor(boolean projetor) {
        this.projetor = projetor;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}