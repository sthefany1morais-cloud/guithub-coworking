package main.java.view.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.execoes.*;
import main.java.model.espacos.Auditorio;
import main.java.model.espacos.Espaco;
import main.java.model.espacos.SalaDeReuniao;
import main.java.model.pagamentos.MetodoDePagamento;
import main.java.service.EspacoService;
import main.java.service.ReservaService;
import main.java.util.CalculoReservaUtil;
import main.java.util.CampoUtil;
import main.java.util.FormatadorUtil;
import main.java.util.MensagemUtil;
import main.java.util.ValidacaoUtil;
import main.java.view.MainCoworking;

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
    @FXML private Label projetorLabel;

    private static Espaco espacoSelecionado;
    private MainCoworking mainApp;
    private EspacoService espacoService;
    private ReservaService reservaService;

    public static void setEspacoSelecionado(Espaco espaco) {
        espacoSelecionado = espaco;
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
        projetorLabel.setVisible(espacoSelecionado instanceof SalaDeReuniao);
        dataInicioPicker.setEditable(false);
        dataFimPicker.setEditable(false);
        CampoUtil.configurarCampoHora(horaInicioField);
        CampoUtil.configurarCampoHora(horaFimField);
        dataInicioPicker.setOnAction(e -> calcularCusto());
        horaInicioField.setOnKeyReleased(e -> calcularCusto());
        dataFimPicker.setOnAction(e -> calcularCusto());
        horaFimField.setOnKeyReleased(e -> calcularCusto());
        projetorCheckBox.setOnAction(e -> calcularCusto());
        salvarButton.setDisable(true);
        metodoComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            salvarButton.setDisable(newVal == null || newVal.isEmpty());
        });
    }

    private void carregarEspaco() {
        String detalhes = "Espaço: " + espacoSelecionado.getNome() + " - Tipo: " + espacoSelecionado.getClass().getSimpleName() + " - Capacidade: " + espacoSelecionado.getCapacidade() + " - Preço: R$" + FormatadorUtil.formatarDinheiro(espacoSelecionado.getPrecoPorHora());
        if (espacoSelecionado instanceof SalaDeReuniao) {
            detalhes += " - Taxa Fixa: R$" + FormatadorUtil.formatarDinheiro(((SalaDeReuniao) espacoSelecionado).getTaxaFixa());
        } else if (espacoSelecionado instanceof Auditorio) {
            detalhes += " - Custo Adicional: R$" + FormatadorUtil.formatarDinheiro(((Auditorio) espacoSelecionado).getCustoAdicional());
        }
        espacoInfoLabel.setText(detalhes);
    }

    private void calcularCusto() {
        try {
            LocalDateTime inicio = LocalDateTime.of(dataInicioPicker.getValue(), LocalTime.parse(horaInicioField.getText()));
            LocalDateTime fim = LocalDateTime.of(dataFimPicker.getValue(), LocalTime.parse(horaFimField.getText()));
            boolean projetor = projetorCheckBox.isSelected();
            double custo = CalculoReservaUtil.calcularCustoParaView(espacoSelecionado, inicio, fim, projetor);
            custoLabel.setText("Custo: R$" + FormatadorUtil.formatarDinheiro(custo));
        } catch (Exception e) {
            custoLabel.setText("Custo: R$0,00");
        }
    }

    @FXML
    private void salvar() {
        try {
            String horaInicio = horaInicioField.getText();
            String horaFim = horaFimField.getText();
            ValidacaoUtil.validarHora(horaInicio);
            ValidacaoUtil.validarHora(horaFim);
            int hInicio = Integer.parseInt(horaInicio.substring(0, 2));
            int mInicio = Integer.parseInt(horaInicio.substring(3));
            int hFim = Integer.parseInt(horaFim.substring(0, 2));
            int mFim = Integer.parseInt(horaFim.substring(3));
            LocalDateTime inicio = LocalDateTime.of(dataInicioPicker.getValue(), LocalTime.of(hInicio, mInicio));
            LocalDateTime fim = LocalDateTime.of(dataFimPicker.getValue(), LocalTime.of(hFim, mFim));
            MetodoDePagamento metodo = MetodoDePagamento.valueOf(metodoComboBox.getValue());
            boolean projetor = projetorCheckBox.isSelected();
            reservaService.criarReserva(espacoSelecionado.getId(), inicio, fim, metodo, projetor);
            MensagemUtil.limparMensagens(errosLabel);
            MensagemUtil.mostrarAlertaInformacao("Sucesso", "Reserva criada com sucesso!");
            voltar();
        } catch (EspacoInexistenteException | EspacoIndisponivelException | DataInvalidaExeption |
                 ReservaSobrepostaException | HoraInvalidaException e) {
            MensagemUtil.definirErro(errosLabel, "Erro: " + e.getMessage());
        } catch (Exception e) {
            MensagemUtil.definirErro(errosLabel, "Erro inesperado: " + e.getMessage());
        }
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("CriarReserva.fxml");
    }
}