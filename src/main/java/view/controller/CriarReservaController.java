package main.java.view.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.model.espacos.Espaco;
import main.java.service.EspacoService;
import main.java.service.ReservaService;
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
    private ReservaService reservaService;
    private ObservableList<Espaco> espacosList;
    private FilteredList<Espaco> filteredList;

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setServices(EspacoService espacoService, ReservaService reservaService) {
        this.espacoService = espacoService;
        this.reservaService = reservaService;
        carregarEspacos();
    }

    @FXML
    private void initialize() {
        filtroTipoComboBox.setItems(FXCollections.observableArrayList("Todos", "Sala de Reunião", "Cabine Individual", "Auditório"));
        filtroTipoComboBox.setValue("Todos");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        tipoColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClass().getSimpleName()));
        capacidadeColumn.setCellValueFactory(new PropertyValueFactory<>("capacidade"));
        disponivelColumn.setCellValueFactory(new PropertyValueFactory<>("disponivel"));

        buscaField.textProperty().addListener((obs, oldText, newText) -> filtrar());
        filtroTipoComboBox.setOnAction(e -> filtrar());
        disponiveisCheckBox.setOnAction(e -> filtrar());
        espacosTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> atualizarDetalhes());
    }

    private void carregarEspacos() {
        espacosList = FXCollections.observableArrayList(espacoService.listarTodos());
        filteredList = new FilteredList<>(espacosList, p -> true);
        espacosTableView.setItems(filteredList);
        if (espacosList.isEmpty()) {
            detalhesLabel.setText("Nenhum espaço disponível. Crie um espaço primeiro.");
            criarReservaButton.setDisable(true);
            mensagemLabel.setText("Nenhum espaço encontrado. Crie um novo espaço primeiro.");
        } else {
            detalhesLabel.setText("Selecione um espaço para ver detalhes.");
            criarReservaButton.setDisable(true);
            mensagemLabel.setText("");
        }
    }

    private void filtrar() {
        String busca = buscaField.getText().toLowerCase();
        String tipo = filtroTipoComboBox.getValue();
        boolean somenteDisponiveis = disponiveisCheckBox.isSelected();

        filteredList.setPredicate(espaco -> {
            boolean matchesBusca = busca.isEmpty() || String.valueOf(espaco.getId()).contains(busca) || espaco.getNome().toLowerCase().contains(busca);
            boolean matchesTipo = "Todos".equals(tipo) || espaco.getClass().getSimpleName().equals(tipo.replace(" ", ""));
            boolean matchesDisponivel = !somenteDisponiveis || espaco.isDisponivel();
            return matchesBusca && matchesTipo && matchesDisponivel;
        });
    }

    private void atualizarDetalhes() {
        Espaco selecionado = espacosTableView.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            detalhesLabel.setText("Nome: " + selecionado.getNome() + " | Tipo: " + selecionado.getClass().getSimpleName() + " | Capacidade: " + selecionado.getCapacidade() + " | Preço: R$" + selecionado.getPrecoPorHora());
            criarReservaButton.setDisable(false);
        } else {
            detalhesLabel.setText("Selecione um espaço para ver detalhes.");
            criarReservaButton.setDisable(true);
        }
    }

    @FXML
    private void criarReserva() {
        Espaco selecionado = espacosTableView.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            DetalhesReservaController.setEspacoSelecionado(selecionado);
            mainApp.mudarScene("DetalhesReserva.fxml");
        }
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("ReservaMenu.fxml");
    }
}