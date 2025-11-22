package main.java.view.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import main.java.service.RelatorioService;
import main.java.view.MainCoworking;

public class RelatoriosController {
    @FXML private ComboBox<String> tipoRelatorioComboBox;
    @FXML private Label descricaoLabel;
    @FXML private HBox periodoHBox;
    @FXML private HBox statusHBox;
    @FXML private HBox tipoEspacoHBox;
    @FXML private HBox metodoHBox;
    @FXML private HBox topHBox;
    @FXML private CheckBox ativasCheckBox;
    @FXML private CheckBox inativasCheckBox;
    @FXML private ComboBox<String> tipoEspacoComboBox;
    @FXML private ComboBox<String> metodoComboBox;
    @FXML private Spinner<Integer> topSpinner;
    @FXML private DatePicker dataInicioPicker;
    @FXML private DatePicker dataFimPicker;
    @FXML private Button gerarButton;
    @FXML private Button voltarButton;

    private MainCoworking mainApp;
    private RelatorioService relatorioService;

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setRelatorioService(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @FXML
    private void initialize() {
        tipoRelatorioComboBox.setItems(FXCollections.observableArrayList(
                "Reservas por Período", "Reservas por Tipo", "Faturamento por Método", "Top Espaços", "Faturamento Total"
        ));
        tipoEspacoComboBox.setItems(FXCollections.observableArrayList("Sala de Reunião", "Cabine Individual", "Auditório"));
        metodoComboBox.setItems(FXCollections.observableArrayList("PIX", "CARTAO", "DINHEIRO"));
        topSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3));

        tipoRelatorioComboBox.setOnAction(e -> atualizarCampos());
    }

    private void atualizarCampos() {
        String tipo = tipoRelatorioComboBox.getValue();
        periodoHBox.setVisible(false);
        statusHBox.setVisible(false);
        tipoEspacoHBox.setVisible(false);
        metodoHBox.setVisible(false);
        topHBox.setVisible(false);

        if ("Reservas por Período".equals(tipo)) {
            descricaoLabel.setText("Exibe reservas em um período específico, com filtros por status.");
            periodoHBox.setVisible(true);
            statusHBox.setVisible(true);
        } else if ("Reservas por Tipo".equals(tipo)) {
            descricaoLabel.setText("Exibe reservas filtradas por tipo de espaço.");
            tipoEspacoHBox.setVisible(true);
        } else if ("Faturamento por Método".equals(tipo)) {
            descricaoLabel.setText("Exibe faturamento por método de pagamento em um período.");
            periodoHBox.setVisible(true);
            metodoHBox.setVisible(true);
        } else if ("Top Espaços".equals(tipo)) {
            descricaoLabel.setText("Exibe top espaços mais utilizados.");
            topHBox.setVisible(true);
        } else if ("Faturamento Total".equals(tipo)) {
            descricaoLabel.setText("Exibe faturamento total do sistema.");
        }
    }

    @FXML
    private void gerar() {
        // Lógica para chamar RelatorioService e abrir popup com dados
        // Exemplo: abrir popup com TableView populada
        // (Implementar abertura de popup aqui, similar ao que discutimos)
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("MenuInicial.fxml");
    }
}
