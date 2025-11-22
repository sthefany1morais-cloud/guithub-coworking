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
import main.java.model.reservas.Reserva;
import main.java.service.ReservaService;
import main.java.view.MainCoworking;

public class CancelarReservaController {
    @FXML private ComboBox<String> filtroTipoComboBox;
    @FXML private TextField buscaField;
    @FXML private TableView<Reserva> reservasTableView;
    @FXML private TableColumn<Reserva, Integer> idColumn;
    @FXML private TableColumn<Reserva, String> espacoColumn;
    @FXML private TableColumn<Reserva, String> inicioColumn;
    @FXML private TableColumn<Reserva, String> fimColumn;
    @FXML private TableColumn<Reserva, Double> valorColumn;
    @FXML private Button cancelarButton;
    @FXML private Button voltarButton;

    private MainCoworking mainApp;
    private ReservaService reservaService;
    private ObservableList<Reserva> reservasList;
    private FilteredList<Reserva> filteredList;

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setReservaService(ReservaService reservaService) {
        this.reservaService = reservaService;
        carregarReservas();
    }

    @FXML
    private void initialize() {
        filtroTipoComboBox.setItems(FXCollections.observableArrayList("Todos", "Sala de Reunião", "Cabine Individual", "Auditório"));
        filtroTipoComboBox.setValue("Todos");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        espacoColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEspaco().getNome()));
        inicioColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getInicio().toString()));
        fimColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFim().toString()));
        valorColumn.setCellValueFactory(new PropertyValueFactory<>("valorCalculado"));

        buscaField.textProperty().addListener((obs, oldText, newText) -> filtrar());
        filtroTipoComboBox.setOnAction(e -> filtrar());
        reservasTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> cancelarButton.setDisable(newSelection == null));
    }

    private void carregarReservas() {
        reservasList = FXCollections.observableArrayList(reservaService.listarTodos().stream().filter(Reserva::isAtivo).toList());
        filteredList = new FilteredList<>(reservasList, p -> true);
        reservasTableView.setItems(filteredList);
    }

    private void filtrar() {
        String busca = buscaField.getText().toLowerCase();
        String tipo = filtroTipoComboBox.getValue();

        filteredList.setPredicate(reserva -> {
            boolean matchesBusca = busca.isEmpty() || String.valueOf(reserva.getId()).contains(busca) || reserva.getEspaco().getNome().toLowerCase().contains(busca);
            boolean matchesTipo = "Todos".equals(tipo) || reserva.getEspaco().getClass().getSimpleName().equals(tipo.replace(" ", ""));
            return matchesBusca && matchesTipo;
        });
    }

    @FXML
    private void cancelar() {
        Reserva selecionada = reservasTableView.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            try {
                double reembolso = reservaService.cancelarReserva(selecionada.getId());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Reserva cancelada! Reembolso: R$" + String.format("%.2f", reembolso));
                alert.showAndWait();
                carregarReservas();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erro: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("ReservaMenu.fxml");
    }
}