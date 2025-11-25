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
import main.java.model.pagamentos.MetodoDePagamento;
import main.java.model.reservas.Reserva;
import main.java.service.EspacoService;
import main.java.service.ReservaService;
import main.java.view.MainCoworking;
import javafx.beans.binding.BooleanBinding;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    private ReservaService reservaService;  // Adicionado
    private ObservableList<Espaco> espacosList;
    private FilteredList<Espaco> filteredList;

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setEspacoService(EspacoService espacoService) {
        this.espacoService = espacoService;
        carregarEspacos();
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
        // Adicionar listeners para todos os filtros
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
        espacosList = FXCollections.observableArrayList(espacoService.listarExistentes());  // Lista apenas existentes
        filteredList = new FilteredList<>(espacosList, p -> true);
        espacosTableView.setItems(filteredList);
        if (espacosList.isEmpty()) {
            mensagemLabel.setText("Nenhum espaço encontrado. Crie um novo espaço primeiro.");
        } else {
            mensagemLabel.setText("");
        }
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Espaço removido!");
                alert.showAndWait();
                carregarEspacos();
            } catch (EspacoComReservasAtivasException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erro: " + e.getMessage());
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erro inesperado: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("MenuEspacos.fxml");
    }
}
