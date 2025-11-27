package main.java.view.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.model.espacos.Espaco;
import main.java.service.EspacoService;
import main.java.util.FiltroUtil;
import main.java.util.FormatadorUtil;
import main.java.util.MensagemUtil;
import main.java.util.TabelaUtil;
import main.java.view.MainCoworking;

public class CriarReservaController {
    @FXML private ComboBox<String> filtroTipoComboBox;
    @FXML private CheckBox disponiveisCheckBox;
    @FXML private TextField buscaField;
    @FXML private TableView<Espaco> espacosTableView;
    @FXML private TableColumn<Espaco, Integer> idColumn;
    @FXML private TableColumn<Espaco, String> nomeColumn;
    @FXML private TableColumn<Espaco, String> tipoColumn;
    @FXML private TableColumn<Espaco, Integer> capacidadeColumn;
    @FXML private TableColumn<Espaco, Boolean> disponivelColumn;
    @FXML private Label detalhesLabel;
    @FXML private Button criarReservaButton;
    @FXML private Button voltarButton;
    @FXML private Label mensagemLabel;

    private MainCoworking mainApp;
    private EspacoService espacoService;
    private ObservableList<Espaco> espacosList;
    private FilteredList<Espaco> filteredList;

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setServices(EspacoService espacoService) {
        this.espacoService = espacoService;
        carregarEspacos();
    }

    @FXML
    private void initialize() {
        filtroTipoComboBox.setItems(FXCollections.observableArrayList("Todos", "Sala de Reunião", "Cabine Individual", "Auditório"));
        filtroTipoComboBox.setValue("Todos");
        TabelaUtil.configurarColunasEspacos(espacosTableView, idColumn, nomeColumn, tipoColumn, capacidadeColumn, null, disponivelColumn);
        buscaField.textProperty().addListener((obs, oldText, newText) -> filtrar());
        filtroTipoComboBox.setOnAction(e -> filtrar());
        disponiveisCheckBox.setOnAction(e -> filtrar());
        espacosTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> atualizarDetalhes());
    }

    private void carregarEspacos() {
        espacosList = FXCollections.observableArrayList(espacoService.listarExistentes());
        filteredList = new FilteredList<>(espacosList, p -> true);
        espacosTableView.setItems(filteredList);
        if (espacosList.isEmpty()) {
            MensagemUtil.definirErro(detalhesLabel, "Nenhum espaço disponível. Crie um espaço primeiro.");
            criarReservaButton.setDisable(true);
            MensagemUtil.definirErro(mensagemLabel, "Nenhum espaço encontrado. Crie um novo espaço primeiro.");
        } else {
            MensagemUtil.definirErro(detalhesLabel, "Selecione um espaço para ver detalhes.");
            criarReservaButton.setDisable(true);
            MensagemUtil.limparMensagens(mensagemLabel);
        }
    }

    private void filtrar() {
        String busca = buscaField.getText().toLowerCase();
        String tipo = filtroTipoComboBox.getValue();
        boolean somenteDisponiveis = disponiveisCheckBox.isSelected();
        FiltroUtil.aplicarFiltroEspacos(filteredList, busca, tipo, somenteDisponiveis);
    }

    private void atualizarDetalhes() {
        Espaco selecionado = espacosTableView.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            detalhesLabel.setText(FormatadorUtil.formatarDetalhesEspaco(selecionado));
            criarReservaButton.setDisable(false);
        } else {
            MensagemUtil.definirErro(detalhesLabel, "Selecione um espaço para ver detalhes.");
            criarReservaButton.setDisable(true);
        }
    }

    @FXML
    private void criarReserva() {
        Espaco selecionado = espacosTableView.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            if (!selecionado.isDisponivel()) {
                MensagemUtil.mostrarAlertaErro("Erro", "Espaço indisponível para reserva.");
                return;
            }
            DetalhesReservaController.setEspacoSelecionado(selecionado);
            mainApp.mudarScene("DetalhesReserva.fxml");
        }
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("ReservaMenu.fxml");
    }
}