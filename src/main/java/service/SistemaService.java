package main.java.service;

import main.java.dao.base.DAOBase;

public class SistemaService {

    public void persistirDados() {
        // Chama o método do DAO para sincronizar dados com ObjectDB
        DAOBase.persistirDadosGlobais();
    }
}
