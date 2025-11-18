package main.java.dao.adaptacao;

import main.java.dao.base.DAOBase;
import main.java.model.pagamentos.Pagamento;

public class PagamentoDAO extends DAOBase<Pagamento> {
    public PagamentoDAO() {
        super(Pagamento.class);
    }
}
