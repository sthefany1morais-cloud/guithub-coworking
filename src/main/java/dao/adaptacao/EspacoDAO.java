package main.java.dao.adaptacao;

import main.java.dao.base.DAOBase;
import main.java.model.espacos.Espaco;

public class EspacoDAO extends DAOBase<Espaco> {
    public EspacoDAO() {
        super(Espaco.class);
    }
}
