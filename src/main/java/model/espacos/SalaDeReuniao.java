package main.java.model.espacos;

import main.java.execoes.CapacidadeInvalidaException;
import main.java.execoes.PrecoPorHoraInvalidoException;
import main.java.execoes.TaxaFixaInvalidaException;

import javax.persistence.Entity;

@Entity

public class SalaDeReuniao extends Espaco {

    private double taxaFixa;

    protected SalaDeReuniao() {
    }

    public SalaDeReuniao(String nome, int capacidade, double precoPorHora, double taxaFixa) throws CapacidadeInvalidaException, PrecoPorHoraInvalidoException, TaxaFixaInvalidaException {
        super(nome, capacidade, precoPorHora);
        if (taxaFixa <= 0){
            throw  new TaxaFixaInvalidaException("Taxa fixa não pode ser zero ou negativa.");
        }
        this.taxaFixa = taxaFixa;
    }

    @Override
    public double calcularCustoReserva(double horas) {
        return (super.getPrecoPorHora()*horas);
    }

    public double calcularCustoReserva(double horas, boolean projetor) {
        double custo = this.calcularCustoReserva(horas);
        if (projetor){
            custo+= this.taxaFixa;
        }
        return custo;
    }

    public double getTaxaFixa() {
        return taxaFixa;
    }

    public void setTaxaFixa(double taxaFixa) throws TaxaFixaInvalidaException {

        if (taxaFixa <=0){
            throw new TaxaFixaInvalidaException("Taxa fixa não pode ser zero ou negativa.");
        }

        this.taxaFixa = taxaFixa;
    }
}
