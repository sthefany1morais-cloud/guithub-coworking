package main.java.model.pagamentos;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int idDaReserva;

    private double valorPago;
    private LocalDateTime data;

    @Enumerated(EnumType.STRING)
    private MetodoDePagamento metodo;

    protected Pagamento() {}

    public Pagamento(int idDaReserva, double valorPago, LocalDateTime data, MetodoDePagamento metodo) {
        this.idDaReserva = idDaReserva;
        this.valorPago = valorPago;
        this.data = data;
        this.metodo = metodo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDaReserva() {
        return idDaReserva;
    }

    public void setIdDaReserva(int idDaReserva) {
        this.idDaReserva = idDaReserva;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public MetodoDePagamento getMetodo() {
        return metodo;
    }

    public void setMetodo(MetodoDePagamento metodo) {
        this.metodo = metodo;
    }
}
