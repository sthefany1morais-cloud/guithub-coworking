package main.java.view.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.model.espacos.Espaco;
import main.java.service.EspacoService;
import main.java.service.ReservaService;
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

    private MainCoworking mainApp;
    private EspacoService espacoService;
    private ReservaService reservaService;  // Adicionado
    private ObservableList<Espaco> espacosList;
    private FilteredList<Espaco> filteredList;

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setEspacoService(EspacoService espacoService) {
        this.espacoService = espacoService;
    }

    public void setReservaService(ReservaService reservaService) {  // Adicionado
        this.reservaService = reservaService;
    }

    @FXML
    private void initialize() {
        filtroTipoComboBox.setItems(FXCollections.observableArrayList("Todos", "Sala de Reunião", "Cabine Individual", "Auditório"));
        filtroTipoComboBox.setValue("Todos");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        tipoColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClass().getSimpleName()));
        capacidadeColumn.setCellValueFactory(new PropertyValueFactory<>("capacidade"));
        precoColumn.setCellValueFactory(new PropertyValueFactory<>("precoPorHora"));
        disponivelColumn.setCellValueFactory(new PropertyValueFactory<>("disponivel"));

        buscaField.textProperty().addListener((obs, oldText, newText) -> filtrar());
        filtroTipoComboBox.setOnAction(e -> filtrar());
    }

    private void carregarEspacos() {
        espacosList = FXCollections.observableArrayList(espacoService.listarTodos());
        filteredList = new FilteredList<>(espacosList, p -> true);
        espacosTableView.setItems(filteredList);
    }

    private void filtrar() {
        String busca = buscaField.getText().toLowerCase();
        String tipo = filtroTipoComboBox.getValue();

        filteredList.setPredicate(espaco -> {
            boolean matchesBusca = busca.isEmpty() || String.valueOf(espaco.getId()).contains(busca) || espaco.getNome().toLowerCase().contains(busca);
            boolean matchesTipo = "Todos".equals(tipo) || espaco.getClass().getSimpleName().equals(tipo.replace(" ", ""));
            return matchesBusca && matchesTipo;
        });
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
                boolean possuiReservas = reservaService.possuiReservasAtivas(selecionado);  // Agora funciona
                espacoService.removerEspaco(selecionado.getId(), possuiReservas);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Espaço removido!");
                alert.showAndWait();
                carregarEspacos();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erro: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("MenuEspacos.fxml");
    }
}
