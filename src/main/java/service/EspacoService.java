package main.java.service;

import main.java.dao.adaptacao.EspacoDAO;
import main.java.execoes.*;
import main.java.model.espacos.*;
import java.util.List;
import java.util.stream.Collectors;

public class EspacoService {

    private final EspacoDAO espacoDAO = new EspacoDAO();

    private void validarNomeUnico(String nome) throws EspacoJaExistenteException {
        boolean existe = listarTodos().stream()
                .anyMatch(e -> e.isExistente() &&
                        e.isAtivo() &&
                        e.getNome().equalsIgnoreCase(nome));

        if (existe) {
            throw new EspacoJaExistenteException(
                    "Já existe um espaço com o nome: \"" + nome + "\"."
            );
        }
    }

    public Espaco buscarPorId(int id) throws EspacoInexistenteException, EspacoInativoException{
        Espaco espaco = espacoDAO.buscarPorId(id);
        if (espaco == null || !espaco.isExistente()){
            throw new EspacoInexistenteException("Espaço inexistente.");
        } else if (espaco.isAtivo()) {
            throw new EspacoInativoException("Espaço inativo");
        }
        return espaco;
    }

    public SalaDeReuniao cadastrarSalaDeReuniao(int id, String nome, int capacidade,
                                                boolean disponivel, double precoPorHora,
                                                double taxaFixa)
            throws PrecoPorHoraInvalidoException, CapacidadeInvalidaException,
            TaxaFixaInvalidaException, EspacoJaExistenteException {

        validarNomeUnico(nome);

        SalaDeReuniao sala = new SalaDeReuniao(id, nome, capacidade, disponivel, precoPorHora, taxaFixa);
        espacoDAO.salvar(sala);
        return sala;
    }

    public CabineIndividual cadastrarCabineIndividual(int id, String nome, int capacidade,
                                                      boolean disponivel, double precoPorHora)
            throws PrecoPorHoraInvalidoException, CapacidadeInvalidaException,
            EspacoJaExistenteException {

        validarNomeUnico(nome);

        CabineIndividual cabine = new CabineIndividual(id, nome, capacidade, disponivel, precoPorHora);
        espacoDAO.salvar(cabine);
        return cabine;
    }

    public Auditorio cadastrarAuditorio(int id, String nome, int capacidade, boolean disponivel,
                                        double precoPorHora, double custoAdicional)
            throws PrecoPorHoraInvalidoException, CapacidadeInvalidaException,
            CustoAdicionalInvalidoException, EspacoJaExistenteException {

        validarNomeUnico(nome);

        Auditorio auditorio = new Auditorio(id, nome, capacidade, disponivel, precoPorHora, custoAdicional);
        espacoDAO.salvar(auditorio);
        return auditorio;
    }

    public List<Espaco> listarTodos() {
        return espacoDAO.carregarTodos();
    }

    public <T extends Espaco> List<T> listarPorTipo(Class<T> tipo) {
        return espacoDAO.carregarTodos().stream()
                .filter(e -> tipo.isAssignableFrom(e.getClass()))
                .map(e -> (T)e)
                .collect(Collectors.toList());
    }

    private void atualizarEspaco(Espaco espaco, String novoNome,
                                 int novaCapacidade, double novoPreco, boolean novoAtivo)
            throws EspacoJaExistenteException, CapacidadeInvalidaException, PrecoPorHoraInvalidoException {

        if (!espaco.getNome().equalsIgnoreCase(novoNome)) {
            validarNomeUnico(novoNome);
        }

        espaco.setNome(novoNome);
        espaco.setCapacidade(novaCapacidade);
        espaco.setPrecoPorHora(novoPreco);
        espaco.setAtivo(novoAtivo);
    }

    public void atualizarSalaDeReuniao(int id, String novoNome, int novaCapacidade,
                                       double novoPreco, double novaTaxa, boolean novoAtivo)
            throws EspacoJaExistenteException, CapacidadeInvalidaException,
            PrecoPorHoraInvalidoException, EspacoInexistenteException, TaxaFixaInvalidaException,
            EspacoInativoException {

        SalaDeReuniao sala = (SalaDeReuniao) buscarPorId(id);
        atualizarEspaco(sala, novoNome, novaCapacidade, novoPreco, novoAtivo);

        sala.setTaxaFixa(novaTaxa);

        espacoDAO.atualizar(sala);
    }

    public void atualizarCabineIndividual(int id, String novoNome, int novaCapacidade,
                                          double novoPreco, boolean novoAtivo)
            throws EspacoJaExistenteException, CapacidadeInvalidaException,
            PrecoPorHoraInvalidoException, EspacoInexistenteException, EspacoInativoException {

        CabineIndividual cabine = (CabineIndividual) buscarPorId(id);

        atualizarEspaco(cabine, novoNome, novaCapacidade, novoPreco, novoAtivo);

        espacoDAO.atualizar(cabine);
    }

    public void atualizarAuditorio(int id, String novoNome, int novaCapacidade,
                                   double novoPreco, double novoCustoAdicional, boolean novoAtivo)
            throws EspacoJaExistenteException, CapacidadeInvalidaException,
            PrecoPorHoraInvalidoException, EspacoInexistenteException, CustoAdicionalInvalidoException,
            EspacoInativoException {

        Auditorio auditorio = (Auditorio) buscarPorId(id);

        atualizarEspaco(auditorio, novoNome, novaCapacidade, novoPreco, novoAtivo);

        auditorio.setCustoAdicional(novoCustoAdicional);

        espacoDAO.atualizar(auditorio);
    }

    public void removerEspaco(int id, boolean possuiReservasAtivas) throws EspacoComReservasAtivasException,
            EspacoInexistenteException, EspacoInativoException {

        if (possuiReservasAtivas) {
            throw new EspacoComReservasAtivasException("Não é possível remover o espaço pois ele possui reservas ativas.");
        }

        Espaco espaco = buscarPorId(id);

        espaco.setAtivo(false);
        espaco.setExistente(false);

        espacoDAO.atualizar(espaco);
    }
}
