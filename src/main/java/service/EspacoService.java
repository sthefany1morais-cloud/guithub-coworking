package main.java.service;

import main.java.dao.adaptacao.EspacoDAO;
import main.java.execoes.*;
import main.java.model.espacos.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EspacoService {

    private final EspacoDAO espacoDAO;

    public EspacoService(EspacoDAO espacoDAO) {
        this.espacoDAO = espacoDAO;
    }

    private void validarNomeUnico(String nome) throws EspacoJaExistenteException {
        boolean existe = listarTodos().stream()
                .anyMatch(e -> e.isExistente() &&
                        e.isDisponivel() &&
                        e.getNome().equalsIgnoreCase(nome));

        if (existe) {
            throw new EspacoJaExistenteException(
                    "Já existe um espaço com o nome: \"" + nome + "\"."
            );
        }
    }

    public Espaco buscarPorId(int id) throws EspacoInexistenteException {
        Espaco espaco = espacoDAO.buscarPorId(id);
        if (espaco == null){
            throw new EspacoInexistenteException("Espaço inexistente.");
        }
        return espaco;
    }

    public Espaco buscarPorIdTodos(int id) {
        return espacoDAO.buscarPorId(id);
    }

    public SalaDeReuniao cadastrarSalaDeReuniao(String nome, int capacidade, double precoPorHora,
                                                double taxaFixa)
            throws PrecoPorHoraInvalidoException, CapacidadeInvalidaException,
            TaxaFixaInvalidaException, EspacoJaExistenteException {

        validarNomeUnico(nome);

        SalaDeReuniao sala = new SalaDeReuniao(nome, capacidade, precoPorHora, taxaFixa);
        espacoDAO.salvar(sala);
        return sala;
    }

    public CabineIndividual cadastrarCabineIndividual(String nome, int capacidade, double precoPorHora)
            throws PrecoPorHoraInvalidoException, CapacidadeInvalidaException,
            EspacoJaExistenteException {

        validarNomeUnico(nome);

        CabineIndividual cabine = new CabineIndividual(nome, capacidade, precoPorHora);
        espacoDAO.salvar(cabine);
        return cabine;
    }

    public Auditorio cadastrarAuditorio(String nome, int capacidade,
                                        double precoPorHora, double custoAdicional)
            throws PrecoPorHoraInvalidoException, CapacidadeInvalidaException,
            CustoAdicionalInvalidoException, EspacoJaExistenteException {

        validarNomeUnico(nome);

        Auditorio auditorio = new Auditorio(nome, capacidade, precoPorHora, custoAdicional);
        espacoDAO.salvar(auditorio);
        return auditorio;
    }

    public List<Espaco> listarTodos() {
        return espacoDAO.carregarTodos();
    }

    public List<Espaco> listarExistentes() {
        ArrayList<Espaco> espacos = new ArrayList<>();
        for (Espaco espaco: listarTodos()){
            if (espaco.isExistente()){
                espacos.add(espaco);
            }
        }
        return espacos;
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
        espaco.setDisponivel(novoAtivo);
    }

    public void atualizarSalaDeReuniao(int id, String novoNome, int novaCapacidade,
                                       double novoPreco, double novaTaxa, boolean novoAtivo)
            throws EspacoJaExistenteException, CapacidadeInvalidaException,
            PrecoPorHoraInvalidoException, EspacoInexistenteException, TaxaFixaInvalidaException,
            EspacoIndisponivelException {

        SalaDeReuniao sala = (SalaDeReuniao) buscarPorId(id);
        atualizarEspaco(sala, novoNome, novaCapacidade, novoPreco, novoAtivo);

        sala.setTaxaFixa(novaTaxa);

        espacoDAO.atualizar(sala);
    }

    public void atualizarCabineIndividual(int id, String novoNome, int novaCapacidade,
                                          double novoPreco, boolean novoAtivo)
            throws EspacoJaExistenteException, CapacidadeInvalidaException,
            PrecoPorHoraInvalidoException, EspacoInexistenteException, EspacoIndisponivelException {

        CabineIndividual cabine = (CabineIndividual) buscarPorId(id);

        atualizarEspaco(cabine, novoNome, novaCapacidade, novoPreco, novoAtivo);

        espacoDAO.atualizar(cabine);
    }

    public void atualizarAuditorio(int id, String novoNome, int novaCapacidade,
                                   double novoPreco, double novoCustoAdicional, boolean novoAtivo)
            throws EspacoJaExistenteException, CapacidadeInvalidaException,
            PrecoPorHoraInvalidoException, EspacoInexistenteException, CustoAdicionalInvalidoException,
            EspacoIndisponivelException {

        Auditorio auditorio = (Auditorio) buscarPorId(id);

        atualizarEspaco(auditorio, novoNome, novaCapacidade, novoPreco, novoAtivo);

        auditorio.setCustoAdicional(novoCustoAdicional);

        espacoDAO.atualizar(auditorio);
    }

    public void removerEspaco(int id, boolean possuiReservasAtivas) throws EspacoComReservasAtivasException,
            EspacoInexistenteException, EspacoIndisponivelException {

        if (possuiReservasAtivas) {
            throw new EspacoComReservasAtivasException("Não é possível remover o espaço pois ele possui reservas ativas.");
        }

        Espaco espaco = buscarPorId(id);

        espaco.setDisponivel(false);
        espaco.setExistente(false);

        espacoDAO.atualizar(espaco);
    }
}
