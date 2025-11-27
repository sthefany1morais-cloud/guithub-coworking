package main.java.view.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.execoes.EspacoComReservasAtivasException;
import main.java.execoes.ValidacaoException;
import main.java.model.espacos.Espaco;
import main.java.service.EspacoService;
import main.java.service.ReservaService;
import main.java.util.FiltroUtil;
import main.java.util.MensagemUtil;
import main.java.util.TabelaUtil;
import main.java.util.VerificacaoUtil;
import main.java.view.MainCoworking;

public class EditarEspacosController {
    @FXML private ComboBox<String> filtroTipoComboBox;
    @FXML private TextField buscaField;
    @FXML private TableView<Espaco> espacosTableView;
    @FXML private TableColumn<Espaco, Integer> idColumn;
    @FXML private TableColumn<Espaco, String> nomeColumn;
    @FXML private TableColumn<Espaco, String> tipoColumn;
    @FXML private TableColumn<Espaco, Integer> capacidadeColumn;
    @FXML private TableColumn<Espaco, Double> precoColumn;
    @FXML private TableColumn<Espaco, Boolean> disponivelColumn;
    @FXML private Button editarButton;
    @FXML private Button excluirButton;
    @FXML private Button voltarButton;
    @FXML private CheckBox disponiveisCheckBox;
    @FXML private CheckBox indisponiveisCheckBox;
    @FXML private Label mensagemLabel;
    @FXML private Label errosLabel;

    private MainCoworking mainApp;
    private EspacoService espacoService;
    private ReservaService reservaService;
    private ObservableList<Espaco> espacosList;
    private FilteredList<Espaco> filteredList;

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setEspacoService(EspacoService espacoService) {
        this.espacoService = espacoService;
        carregarEspacos();
    }

    public void setReservaService(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @FXML
    private void initialize() {
        filtroTipoComboBox.setItems(FXCollections.observableArrayList("Todos", "Sala de Reunião", "Cabine Individual", "Auditório"));
        filtroTipoComboBox.setValue("Todos");
        // Usar TabelaUtil
        TabelaUtil.configurarColunasEspacos(espacosTableView, idColumn, nomeColumn, tipoColumn, capacidadeColumn, precoColumn, disponivelColumn);
        buscaField.textProperty().addListener((obs, oldText, newText) -> filtrar());
        filtroTipoComboBox.setOnAction(e -> filtrar());
        disponiveisCheckBox.setOnAction(e -> filtrar());
        indisponiveisCheckBox.setOnAction(e -> filtrar());
        editarButton.disableProperty().bind(espacosTableView.getSelectionModel().selectedItemProperty().isNull());
        excluirButton.disableProperty().bind(espacosTableView.getSelectionModel().selectedItemProperty().isNull());
        disponiveisCheckBox.setSelected(true);
        indisponiveisCheckBox.setSelected(true);
    }

    private void carregarEspacos() {
        espacosList = FXCollections.observableArrayList(espacoService.listarExistentes());
        filteredList = new FilteredList<>(espacosList, p -> true);
        espacosTableView.setItems(filteredList);
        // Usar VerificacaoUtil
        mensagemLabel.setText(VerificacaoUtil.verificarEspacos(espacoService));
    }

    private void filtrar() {
        String busca = buscaField.getText().toLowerCase();
        String tipo = filtroTipoComboBox.getValue();
        boolean mostrarDisponiveis = disponiveisCheckBox.isSelected();
        boolean mostrarIndisponiveis = indisponiveisCheckBox.isSelected();
        filteredList.setPredicate(espaco -> {
            boolean matchesBusca = busca.isEmpty() || String.valueOf(espaco.getId()).contains(busca) || espaco.getNome().toLowerCase().contains(busca);
            boolean matchesTipo = "Todos".equals(tipo) || espaco.getClass().getSimpleName().equals(mapTipo(tipo));
            boolean matchesDisponivel = (mostrarDisponiveis && espaco.isDisponivel()) || (mostrarIndisponiveis && !espaco.isDisponivel());
            return matchesBusca && matchesTipo && matchesDisponivel;
        });
    }

    private String mapTipo(String tipo) {
        switch (tipo) {
            case "Sala de Reunião": return "SalaDeReuniao";
            case "Cabine Individual": return "CabineIndividual";
            case "Auditório": return "Auditorio";
            default: return tipo;
        }
    }

    @FXML
    private void editar() {
        Espaco selecionado = espacosTableView.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            EditarEspacoDetalhesController.setEspacoSelecionado(selecionado);
            mainApp.mudarScene("EditarEspacoDetalhes.fxml");
        }
    }

    @FXML
    private void excluir() {
        Espaco selecionado = espacosTableView.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            try {
                boolean possuiReservas = reservaService.possuiReservasAtivas(selecionado);
                espacoService.removerEspaco(selecionado.getId(), possuiReservas);
                MensagemUtil.mostrarAlertaInformacao("Sucesso", "Espaço removido!");
                carregarEspacos();
            } catch (EspacoComReservasAtivasException e) {
                MensagemUtil.mostrarAlertaErro("Erro", "Erro: " + e.getMessage());
            } catch (Exception e) {
                MensagemUtil.mostrarAlertaErro("Erro", "Erro inesperado: " + e.getMessage());
            }
        }
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("MenuEspacos.fxml");
    }
}
