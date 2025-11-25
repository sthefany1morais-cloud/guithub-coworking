package main.java.view.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.java.execoes.DataInvalidaExeption;
import main.java.execoes.EspacoIndisponivelException;
import main.java.execoes.EspacoInexistenteException;
import main.java.execoes.ReservaSobrepostaException;
import main.java.model.espacos.Espaco;
import main.java.model.espacos.SalaDeReuniao;
import main.java.model.pagamentos.MetodoDePagamento;
import main.java.service.EspacoService;
import main.java.service.ReservaService;
import main.java.view.MainCoworking;
import main.java.service.SistemaService;
import javafx.beans.binding.BooleanBinding;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class DetalhesReservaController {
    @FXML private Label espacoInfoLabel;
    @FXML private DatePicker dataInicioPicker;
    @FXML private TextField horaInicioField;
    @FXML private DatePicker dataFimPicker;
    @FXML private TextField horaFimField;
    @FXML private CheckBox projetorCheckBox;
    @FXML private ComboBox<String> metodoComboBox;
    @FXML private Label custoLabel;
    @FXML private Button salvarButton;
    @FXML private Button voltarButton;
    @FXML private Label errosLabel;

    private static Espaco espacoSelecionado;
    private MainCoworking mainApp;
    private EspacoService espacoService;
    private ReservaService reservaService;
    private SistemaService sistemaService;

    public static void setEspacoSelecionado(Espaco espaco) {
        espacoSelecionado = espaco;
    }

    public void setSistemaService(SistemaService sistemaService) {
        this.sistemaService = sistemaService;
    }

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setServices(EspacoService espacoService, ReservaService reservaService) {
        this.espacoService = espacoService;
        this.reservaService = reservaService;
        if (espacoSelecionado != null) {
            carregarEspaco();
        }
    }

    @FXML
    private void initialize() {

        metodoComboBox.setItems(FXCollections.observableArrayList("PIX", "CARTAO", "DINHEIRO"));
        projetorCheckBox.setVisible(espacoSelecionado instanceof SalaDeReuniao);
        // Inicializa campos de hora com 00:00
        horaInicioField.setText("00:00");
        horaFimField.setText("00:00");
        // Listeners para formatação em tempo real de horas
        horaInicioField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.equals(sistemaService.formatarHoras(newText))) {
                horaInicioField.setText(sistemaService.formatarHoras(newText));
            }
        });
        horaFimField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.equals(sistemaService.formatarHoras(newText))) {
                horaFimField.setText(sistemaService.formatarHoras(newText));
            }
        });

        // Desabilitar salvar até tudo preenchido
        BooleanBinding tudoPreenchido = dataInicioPicker.valueProperty().isNotNull()
                .and(dataFimPicker.valueProperty().isNotNull())
                .and(horaInicioField.textProperty().isNotEmpty())
                .and(horaFimField.textProperty().isNotEmpty())
                .and(metodoComboBox.valueProperty().isNotNull());
        salvarButton.disableProperty().bind(tudoPreenchido.not());
        dataInicioPicker.setOnAction(e -> calcularCusto());
        horaInicioField.setOnKeyReleased(e -> calcularCusto());
        dataFimPicker.setOnAction(e -> calcularCusto());
        horaFimField.setOnKeyReleased(e -> calcularCusto());
        projetorCheckBox.setOnAction(e -> calcularCusto());
    }

    private void carregarEspaco() {
        espacoInfoLabel.setText("Espaço: " + espacoSelecionado.getNome() + " - Tipo: " + espacoSelecionado.getClass().getSimpleName() + " - Capacidade: " + espacoSelecionado.getCapacidade() + " - Preço: R$" + espacoSelecionado.getPrecoPorHora());
    }

    private void calcularCusto() {
        try {
            LocalDateTime inicio = LocalDateTime.of(dataInicioPicker.getValue(), LocalTime.parse(horaInicioField.getText()));
            LocalDateTime fim = LocalDateTime.of(dataFimPicker.getValue(), LocalTime.parse(horaFimField.getText()));
            double horas = (double) java.time.Duration.between(inicio, fim).toMinutes() / 60;
            double custo = espacoSelecionado.calcularCustoReserva(horas);
            if (espacoSelecionado instanceof SalaDeReuniao && projetorCheckBox.isSelected()) {
                custo += ((SalaDeReuniao) espacoSelecionado).getTaxaFixa();
            }
            custoLabel.setText("Custo: R$" + String.format("%.2f", custo));
        } catch (Exception e) {
            custoLabel.setText("Custo: R$0,00");
        }
    }

    @FXML
    private void salvar() {
        try {
            LocalDateTime inicio = LocalDateTime.of(dataInicioPicker.getValue(), LocalTime.parse(horaInicioField.getText()));
            LocalDateTime fim = LocalDateTime.of(dataFimPicker.getValue(), LocalTime.parse(horaFimField.getText()));
            // Validações: data início <= data fim, horas válidas
            if (inicio.isAfter(fim)) {
                errosLabel.setText("Erro: Data/hora de início deve ser antes da data/hora de fim.");
                return;
            }
            if (horaInicioField.getText().matches("\\d{2}:\\d{2}") && LocalTime.parse(horaInicioField.getText()).isAfter(LocalTime.of(23, 59))) {
                errosLabel.setText("Erro: Hora de início inválida (00:00-23:59).");
                return;
            }
            if (horaFimField.getText().matches("\\d{2}:\\d{2}") && LocalTime.parse(horaFimField.getText()).isAfter(LocalTime.of(23, 59))) {
                errosLabel.setText("Erro: Hora de fim inválida (00:00-23:59).");
                return;
            }

            MetodoDePagamento metodo = MetodoDePagamento.valueOf(metodoComboBox.getValue());
            boolean projetor = projetorCheckBox.isSelected();
            reservaService.criarReserva(espacoSelecionado.getId(), inicio, fim, metodo, projetor);
            errosLabel.setText("");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Reserva criada com sucesso!");
            alert.showAndWait();
            voltar();
        } catch (EspacoInexistenteException | EspacoIndisponivelException | DataInvalidaExeption |
                 ReservaSobrepostaException e) {
            errosLabel.setText("Erro: " + e.getMessage());
        } catch (Exception e) {
            errosLabel.setText("Erro inesperado: " + e.getMessage());
        }
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("CriarReserva.fxml");
    }
}
