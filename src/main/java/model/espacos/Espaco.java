package main.java.model.espacos;

import main.java.execoes.CapacidadeInvalidaException;
import main.java.execoes.PrecoPorHoraInvalidoException;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)

public abstract class Espaco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nome;
    private int capacidade;
    private boolean disponivel;
    private double precoPorHora;

    protected Espaco() {
    }

    public Espaco(int id, String nome, int capacidade, boolean disponivel, double precoPorHora) throws CapacidadeInvalidaException, PrecoPorHoraInvalidoException {

        if (capacidade <=0){
            throw  new CapacidadeInvalidaException("Capacidade não pode ser zero ou negativa.");
        } else if (precoPorHora <= 0) {
            throw  new PrecoPorHoraInvalidoException("Preço por hora não pode ser zero ou negativo.");
        }
        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
        this.disponivel = disponivel;
        this.precoPorHora = precoPorHora;
    }

    public abstract double calcularCustoReserva(double horas);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) throws CapacidadeInvalidaException {
        if (capacidade <=0){
            throw new CapacidadeInvalidaException("Capacidade não pode ser zero ou negativa.");
        }
        this.capacidade = capacidade;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public double getPrecoPorHora() {
        return precoPorHora;
    }

    public void setPrecoPorHora(double precoPorHora) throws PrecoPorHoraInvalidoException {

        if (precoPorHora <=0){
            throw new PrecoPorHoraInvalidoException("Preço por hora não pode ser zero ou negativo.");
        }

        this.precoPorHora = precoPorHora;
    }
}