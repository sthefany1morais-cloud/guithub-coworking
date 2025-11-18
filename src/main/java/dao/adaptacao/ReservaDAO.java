package main.java.dao.adaptacao;

import main.java.dao.base.DAOBase;
import main.java.model.reservas.Reserva;

public class ReservaDAO extends DAOBase<Reserva> {
    public ReservaDAO() {
        super(Reserva.class);
    }
}
