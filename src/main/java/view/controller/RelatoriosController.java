package main.java.view.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import main.java.execoes.ValidacaoException;
import main.java.model.espacos.Espaco;
import main.java.model.pagamentos.MetodoDePagamento;
import main.java.model.reservas.Reserva;
import main.java.service.RelatorioService;
import main.java.view.MainCoworking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    @FXML private Label errosLabel;

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
        try {
            String tipo = tipoRelatorioComboBox.getValue();
            ObservableList<ObservableList<String>> dados = FXCollections.observableArrayList();
            String resumo = "";
            if ("Reservas por Período".equals(tipo)) {
                LocalDateTime inicio = dataInicioPicker.getValue().atStartOfDay();
                LocalDateTime fim = dataFimPicker.getValue().atTime(23, 59);
                Boolean ativo = ativasCheckBox.isSelected() ? true : (inativasCheckBox.isSelected() ? false : null);
                List<Reserva> reservas = relatorioService.reservasPorPeriodo(inicio, fim, ativo);
                for (Reserva r : reservas) {
                    ObservableList<String> linha = FXCollections.observableArrayList(
                            String.valueOf(r.getId()), r.getEspaco().getNome(), r.getInicio().toString(), r.getFim().toString()
                    );
                    dados.add(linha);
                }
                resumo = "Total de reservas: " + reservas.size();
            } else if ("Reservas por Tipo".equals(tipo)) {
                Class<?> tipoEspaco = Class.forName("main.java.model.espacos." + tipoEspacoComboBox.getValue().replace(" ", ""));
                List<Reserva> reservas = relatorioService.reservasPorTipoEspaco((Class<? extends Espaco>) tipoEspaco, null);
                // ... (similar ao acima)
            } else if ("Faturamento por Método".equals(tipo)) {
                LocalDateTime inicio = dataInicioPicker.getValue().atStartOfDay();
                LocalDateTime fim = dataFimPicker.getValue().atTime(23, 59);
                MetodoDePagamento metodo = MetodoDePagamento.valueOf(metodoComboBox.getValue());
                double faturamento = relatorioService.faturamentoPorMetodo(metodo, inicio, fim);
                dados.add(FXCollections.observableArrayList("Método", "Faturamento", metodo.toString(), String.format("%.2f", faturamento)));
                resumo = "Faturamento total: R$" + String.format("%.2f", faturamento);
            } else if ("Top Espaços".equals(tipo)) {
                int topN = topSpinner.getValue();
                if (topN <= 0) throw new ValidacaoException(List.of("Top N deve ser maior que 0."));
                List<Map.Entry<Integer, Long>> top = relatorioService.topEspacosMaisUsados(topN);
                for (Map.Entry<Integer, Long> entry : top) {
                    dados.add(FXCollections.observableArrayList("ID Espaço", "Reservas", String.valueOf(entry.getKey()), String.valueOf(entry.getValue())));
                }
                resumo = "Top " + topN + " espaços.";
            } else if ("Faturamento Total".equals(tipo)) {
                double total = relatorioService.faturamentoTotal();
                dados.add(FXCollections.observableArrayList("Total", String.format("%.2f", total)));
                resumo = "Faturamento total do sistema.";
            }
            if (dados.isEmpty()) {
                errosLabel.setText("Nenhum dado encontrado para o relatório.");
                return;
            }
            // Abrir popup
            RelatorioPopupController popup = new RelatorioPopupController();
            popup.setRelatorioService(relatorioService);
            popup.setDados(dados, resumo);
            // ... (abrir Stage para popup)
        } catch (ValidacaoException e) {
            errosLabel.setText("Erros encontrados:\n" + String.join("\n", e.getErros()));
        } catch (Exception e) {
            errosLabel.setText("Erro inesperado: " + e.getMessage());
        }
    }


    @FXML
    private void voltar() {
        mainApp.mudarScene("MenuInicial.fxml");
    }
}
