package main.java.model.espacos;

import main.java.execoes.CapacidadeInvalidaException;
import main.java.execoes.PrecoPorHoraInvalidoException;

import javax.persistence.Entity;

@Entity

public class CabineIndividual extends Espaco {

    protected CabineIndividual() {
    }

    public CabineIndividual(int id, String nome, int capacidade, boolean disponivel, double precoPorHora) throws CapacidadeInvalidaException, PrecoPorHoraInvalidoException {
        super(id, nome, capacidade, disponivel, precoPorHora);
    }

    @Override
    public double calcularCustoReserva(double horas) {
        if (horas > 4){
            return (super.getPrecoPorHora()*horas)*0.9;
        }
        else {
            return (super.getPrecoPorHora()*horas);
        }
    }
}
