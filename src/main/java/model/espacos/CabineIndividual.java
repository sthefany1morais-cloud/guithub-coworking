package main.java.model.espacos;

import main.java.execoes.CapacidadeInvalidaException;
import main.java.execoes.PrecoPorHoraInvalidoException;

import javax.persistence.Entity;

@Entity

public class CabineIndividual extends Espaco {

    protected CabineIndividual() {
    }

    public CabineIndividual(String nome, int capacidade, double precoPorHora) throws CapacidadeInvalidaException, PrecoPorHoraInvalidoException {
        super(nome, capacidade, precoPorHora);
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
