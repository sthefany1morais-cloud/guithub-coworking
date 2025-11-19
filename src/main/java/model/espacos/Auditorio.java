package main.java.model.espacos;

import main.java.execoes.CapacidadeInvalidaException;
import main.java.execoes.CustoAdicionalInvalidoException;
import main.java.execoes.PrecoPorHoraInvalidoException;

import javax.persistence.Entity;


@Entity

public class Auditorio extends Espaco {
    private double custoAdicional;

    protected Auditorio() {
    }

    public Auditorio(int id, String nome, int capacidade, boolean disponivel, double precoPorHora, double custoAdicional) throws CapacidadeInvalidaException, PrecoPorHoraInvalidoException, CustoAdicionalInvalidoException {
        super(id, nome, capacidade, disponivel, precoPorHora);
        if (custoAdicional <=0){
            throw  new CustoAdicionalInvalidoException("Custo adicional não pode ser zero ou negativo.");
        }
        this.custoAdicional = custoAdicional;
    }

    @Override
    public double calcularCustoReserva(double horas) {
        return (super.getPrecoPorHora()*horas) + this.custoAdicional;
    }

    public double getCustoAdicional() {
        return custoAdicional;
    }

    public void setCustoAdicional(double custoAdicional) throws CustoAdicionalInvalidoException {

        if (custoAdicional <=0){
            throw new CustoAdicionalInvalidoException("Custo adicional não pode ser zero ou negativo.");
        }

        this.custoAdicional = custoAdicional;
    }
}
