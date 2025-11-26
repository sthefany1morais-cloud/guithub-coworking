package main.java.view.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import main.java.execoes.ValidacaoException;
import main.java.model.espacos.Espaco;
import main.java.model.pagamentos.MetodoDePagamento;
import main.java.model.reservas.Reserva;
import main.java.service.EspacoService;
import main.java.service.RelatorioService;
import main.java.view.MainCoworking;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.util.Arrays;
import java.util.function.UnaryOperator;
import javafx.scene.control.TextFormatter;

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
    @FXML private CheckBox disponiveisCheckBox;
    @FXML private CheckBox indisponiveisCheckBox;

    private MainCoworking mainApp;
    private RelatorioService relatorioService;
    private EspacoService espacoService;

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setRelatorioService(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    public void setEspacoService(EspacoService espacoService) {
        this.espacoService = espacoService;
    }

    @FXML
    private void initialize() {
        tipoRelatorioComboBox.setItems(FXCollections.observableArrayList(
                "Reservas realizadas em um período", "Faturamento por tipo de espaço", "Utilização por espaço", "Top espaços mais utilizados"
        ));
        topSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3));

        // Desabilitar edição manual nos DatePickers
        dataInicioPicker.setEditable(false);
        dataFimPicker.setEditable(false);

        // Por padrão, ambas checkboxes marcadas (mostrar todos)
        ativasCheckBox.setSelected(true);
        inativasCheckBox.setSelected(true);
        disponiveisCheckBox.setSelected(true);
        indisponiveisCheckBox.setSelected(true);

        // Listener para impedir desmarcar ambas
        ativasCheckBox.setOnAction(e -> {
            if (!ativasCheckBox.isSelected() && !inativasCheckBox.isSelected()) {
                inativasCheckBox.setSelected(true);
            }
        });
        inativasCheckBox.setOnAction(e -> {
            if (!ativasCheckBox.isSelected() && !inativasCheckBox.isSelected()) {
                ativasCheckBox.setSelected(true);
            }
        });
        disponiveisCheckBox.setOnAction(e -> {
            if (!disponiveisCheckBox.isSelected() && !indisponiveisCheckBox.isSelected()) {
                indisponiveisCheckBox.setSelected(true);
            }
        });
        indisponiveisCheckBox.setOnAction(e -> {
            if (!disponiveisCheckBox.isSelected() && !indisponiveisCheckBox.isSelected()) {
                disponiveisCheckBox.setSelected(true);
            }
        });

        tipoRelatorioComboBox.setOnAction(e -> atualizarCampos());
    }

    private void atualizarCampos() {
        String tipo = tipoRelatorioComboBox.getValue();
        periodoHBox.setVisible(false);
        statusHBox.setVisible(false);
        topHBox.setVisible(false);
        HBox disponibilidadeHBox = new HBox(10, new Label("Disponibilidade:"), disponiveisCheckBox, indisponiveisCheckBox);
        disponibilidadeHBox.setVisible(false);

        if ("Reservas realizadas em um período".equals(tipo)) {
            descricaoLabel.setText("Exibe reservas em um período específico, com filtros por status de reservas.");
            periodoHBox.setVisible(true);
            statusHBox.setVisible(true);
        } else if ("Faturamento por tipo de espaço".equals(tipo)) {
            descricaoLabel.setText("Exibe faturamento total geral por tipo de espaço (sempre considerando valores reais de pagamentos).");
        } else if ("Utilização por espaço".equals(tipo)) {
            descricaoLabel.setText("Exibe horas de utilização por espaço em um período (sempre considerando reservas ativas).");
            periodoHBox.setVisible(true);
            // statusHBox não mostrado, pois horas sempre ativas
        } else if ("Top espaços mais utilizados".equals(tipo)) {
            descricaoLabel.setText("Exibe top espaços mais utilizados, com filtros por disponibilidade de espaços.");
            topHBox.setVisible(true);
            disponibilidadeHBox.setVisible(true);
        }
    }

    @FXML
    private void gerar() {
        try {
            String tipo = tipoRelatorioComboBox.getValue();
            ObservableList<ObservableList<String>> dados = FXCollections.observableArrayList();
            String resumo = "";
            List<String> nomesColunas = Arrays.asList("Coluna 1", "Coluna 2", "Coluna 3", "Coluna 4", "Coluna 5");
            if ("Reservas realizadas em um período".equals(tipo)) {
                // Igual ao anterior, mas nome ajustado
                if (dataInicioPicker.getValue() == null || dataFimPicker.getValue() == null) {
                    throw new ValidacaoException(List.of("Datas de início e fim são obrigatórias."));
                }
                LocalDateTime inicio = dataInicioPicker.getValue().atStartOfDay();
                LocalDateTime fim = dataFimPicker.getValue().atTime(23, 59);
                Boolean ativo = ativasCheckBox.isSelected() && inativasCheckBox.isSelected() ? null : (ativasCheckBox.isSelected() ? true : false);
                List<Reserva> reservas = relatorioService.reservasPorPeriodo(inicio, fim, ativo);
                if (reservas.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Nenhuma reserva encontrada no período selecionado.");
                    alert.showAndWait();
                    return;
                }
                nomesColunas = Arrays.asList("ID Reserva", "Nome Espaço", "Início", "Fim", "Status", "Disponibilidade");
                for (Reserva r : reservas) {
                    ObservableList<String> linha = FXCollections.observableArrayList(
                            String.valueOf(r.getId()), r.getEspaco().getNome(), r.getInicio().toString(), r.getFim().toString(),
                            r.isAtivo() ? "Ativa" : "Inativa", r.getEspaco().isDisponivel() ? "Disponível" : "Indisponível"
                    );
                    dados.add(linha);
                }
                resumo = "Total de reservas: " + reservas.size();
            } else if ("Faturamento por tipo de espaço".equals(tipo)) {
                Map<String, Double> faturamento = relatorioService.faturamentoPorTipoEspaco(LocalDateTime.MIN, LocalDateTime.MAX);  // Total geral
                if (faturamento.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Nenhum faturamento encontrado.");
                    alert.showAndWait();
                    return;
                }
                nomesColunas = Arrays.asList("Tipo de Espaço", "Faturamento", "", "", "");
                for (Map.Entry<String, Double> entry : faturamento.entrySet()) {
                    dados.add(FXCollections.observableArrayList(entry.getKey(), String.format("%.2f", entry.getValue()), "", "", ""));
                }
                resumo = "Faturamento total geral.";
            } else if ("Utilização por espaço".equals(tipo)) {
                if (dataInicioPicker.getValue() == null || dataFimPicker.getValue() == null) {
                    throw new ValidacaoException(List.of("Datas de início e fim são obrigatórias."));
                }
                LocalDateTime inicio = dataInicioPicker.getValue().atStartOfDay();
                LocalDateTime fim = dataFimPicker.getValue().atTime(23, 59);
                Map<Integer, Double> utilizacao = relatorioService.horasReservadas(inicio, fim);
                if (utilizacao.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Nenhuma utilização encontrada no período selecionado.");
                    alert.showAndWait();
                    return;
                }
                nomesColunas = Arrays.asList("ID Espaço", "Nome Espaço", "Horas Utilizadas", "", "");
                for (Map.Entry<Integer, Double> entry : utilizacao.entrySet()) {
                    String nomeEspaco = espacoService.buscarPorId(entry.getKey()).getNome();
                    dados.add(FXCollections.observableArrayList(String.valueOf(entry.getKey()), nomeEspaco, String.format("%.2f", entry.getValue()), "", ""));
                }
                resumo = "Utilização por espaço no período.";
            } else if ("Top espaços mais utilizados".equals(tipo)) {
                int topN = topSpinner.getValue();
                if (topN <= 0) throw new ValidacaoException(List.of("Top N deve ser maior que 0."));
                Boolean disponivel = disponiveisCheckBox.isSelected() && indisponiveisCheckBox.isSelected() ? null : (disponiveisCheckBox.isSelected() ? true : false);
                List<Map.Entry<Integer, Long>> top = relatorioService.topEspacosMaisUsados(topN);
                if (top.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Nenhum espaço encontrado para o top selecionado.");
                    alert.showAndWait();
                    return;
                }
                nomesColunas = Arrays.asList("ID Espaço", "Nome Espaço", "Reservas", "", "");
                for (Map.Entry<Integer, Long> entry : top) {
                    String nomeEspaco = espacoService.buscarPorId(entry.getKey()).getNome();
                    dados.add(FXCollections.observableArrayList(String.valueOf(entry.getKey()), nomeEspaco, String.valueOf(entry.getValue()), "", ""));
                }
                resumo = "Top " + Math.min(topN, top.size()) + " espaços.";
            }

            // Abrir popup
            abrirPopup(dados, resumo, nomesColunas);
        } catch (ValidacaoException e) {
            errosLabel.setText("Erros encontrados:\n" + String.join("\n", e.getErros()));
        } catch (Exception e) {
            errosLabel.setText("Erro inesperado: " + e.getMessage());
        }
    }

    private void abrirPopup(ObservableList<ObservableList<String>> dados, String resumo, List<String> nomesColunas) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RelatorioPopup.fxml"));
            Parent root = loader.load();
            RelatorioPopupController popupController = loader.getController();
            popupController.setRelatorioService(relatorioService);
            popupController.setDados(dados, resumo, nomesColunas);

            Stage popupStage = new Stage();
            popupStage.setTitle("Relatório");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.showAndWait();
        } catch (Exception e) {
            errosLabel.setText("Erro ao abrir popup: " + e.getMessage());
        }
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("MenuInicial.fxml");
    }
}
