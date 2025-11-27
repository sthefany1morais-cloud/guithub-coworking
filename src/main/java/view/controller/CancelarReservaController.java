package main.java.view.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.execoes.ReservaInativaException;
import main.java.execoes.ReservaInexistenteException;
import main.java.model.reservas.Reserva;
import main.java.service.ReservaService;
import main.java.util.FiltroUtil;
import main.java.util.FormatadorUtil;
import main.java.util.MensagemUtil;
import main.java.util.TabelaUtil;
import main.java.view.MainCoworking;

public class CancelarReservaController {
    @FXML private ComboBox<String> filtroTipoComboBox;
    @FXML private TextField buscaField;
    @FXML private TableView<Reserva> reservasTableView;
    @FXML private TableColumn<Reserva, Integer> idColumn;
    @FXML private TableColumn<Reserva, String> espacoColumn;
    @FXML private TableColumn<Reserva, String> tipoColumn;
    @FXML private TableColumn<Reserva, String> inicioColumn;
    @FXML private TableColumn<Reserva, String> fimColumn;
    @FXML private TableColumn<Reserva, Double> valorColumn;
    @FXML private Button cancelarButton;
    @FXML private Button voltarButton;
    @FXML private Label mensagemLabel;

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
        TabelaUtil.configurarColunasReservas(reservasTableView, idColumn, espacoColumn, tipoColumn, inicioColumn, fimColumn, valorColumn);
        buscaField.textProperty().addListener((obs, oldText, newText) -> filtrar());
        filtroTipoComboBox.setOnAction(e -> filtrar());  // Adicionado listener para o ComboBox
        cancelarButton.disableProperty().bind(reservasTableView.getSelectionModel().selectedItemProperty().isNull());
    }

    private void carregarReservas() {
        reservasList = FXCollections.observableArrayList(reservaService.listarTodos().stream().filter(Reserva::isAtivo).toList());
        filteredList = new FilteredList<>(reservasList, p -> true);
        reservasTableView.setItems(filteredList);
        if (reservasList.isEmpty()) {
            MensagemUtil.definirErro(mensagemLabel, "Nenhuma reserva ativa encontrada.");
        } else {
            MensagemUtil.limparMensagens(mensagemLabel);
        }
    }

    private void filtrar() {
        String busca = buscaField.getText().toLowerCase();
        String tipo = filtroTipoComboBox.getValue();
        FiltroUtil.aplicarFiltroReservas(filteredList, busca, tipo);
    }

    @FXML
    private void cancelar() {
        Reserva selecionada = reservasTableView.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            try {
                double reembolso = reservaService.cancelarReserva(selecionada.getId());
                MensagemUtil.limparMensagens(mensagemLabel);
                MensagemUtil.mostrarAlertaInformacao("Sucesso", "Reserva cancelada! Reembolso: R$" + FormatadorUtil.formatarDinheiro(reembolso));  // Corrigido para formatarDinheiro
                carregarReservas();
            } catch (ReservaInexistenteException | ReservaInativaException e) {
                MensagemUtil.definirErro(mensagemLabel, "Erro: " + e.getMessage());
            } catch (Exception e) {
                MensagemUtil.definirErro(mensagemLabel, "Erro inesperado: " + e.getMessage());
            }
        }
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("ReservaMenu.fxml");
    }
}
