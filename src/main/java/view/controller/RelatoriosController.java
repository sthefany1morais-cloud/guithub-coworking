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
                "Reservas por Período", "Reservas por Tipo", "Faturamento por Método", "Top Espaços", "Faturamento Total"
        ));
        tipoEspacoComboBox.setItems(FXCollections.observableArrayList("Sala de Reunião", "Cabine Individual", "Auditório"));
        metodoComboBox.setItems(FXCollections.observableArrayList("PIX", "CARTAO", "DINHEIRO"));
        topSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3));

        // Por padrão, ambas checkboxes marcadas (mostrar todos)
        ativasCheckBox.setSelected(true);
        inativasCheckBox.setSelected(true);

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
            List<String> nomesColunas = Arrays.asList("Coluna 1", "Coluna 2", "Coluna 3", "Coluna 4", "Coluna 5");
            if ("Reservas por Período".equals(tipo)) {
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
            } else if ("Reservas por Tipo".equals(tipo)) {
                if (tipoEspacoComboBox.getValue() == null) {
                    throw new ValidacaoException(List.of("Tipo de espaço é obrigatório."));
                }
                Class<? extends Espaco> tipoEspaco = switch (tipoEspacoComboBox.getValue()) {
                    case "Sala de Reunião" -> main.java.model.espacos.SalaDeReuniao.class;
                    case "Cabine Individual" -> main.java.model.espacos.CabineIndividual.class;
                    case "Auditório" -> main.java.model.espacos.Auditorio.class;
                    default -> throw new ValidacaoException(List.of("Tipo de espaço inválido."));
                };
                List<Reserva> reservas = relatorioService.reservasPorTipoEspaco(tipoEspaco, null);
                if (reservas.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Nenhuma reserva encontrada para o tipo de espaço selecionado.");
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
            } else if ("Faturamento por Método".equals(tipo)) {
                if (dataInicioPicker.getValue() == null || dataFimPicker.getValue() == null || metodoComboBox.getValue() == null) {
                    throw new ValidacaoException(List.of("Datas e método de pagamento são obrigatórios."));
                }
                LocalDateTime inicio = dataInicioPicker.getValue().atStartOfDay();
                LocalDateTime fim = dataFimPicker.getValue().atTime(23, 59);
                MetodoDePagamento metodo = MetodoDePagamento.valueOf(metodoComboBox.getValue());
                double faturamento = relatorioService.faturamentoPorMetodo(metodo, inicio, fim);
                if (faturamento == 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Nenhum faturamento encontrado para o método e período selecionados.");
                    alert.showAndWait();
                    return;
                }
                nomesColunas = Arrays.asList("Método", "Faturamento", "", "", "");
                dados.add(FXCollections.observableArrayList(metodo.toString(), String.format("%.2f", faturamento), "", "", ""));
                resumo = "Faturamento total: R$" + String.format("%.2f", faturamento);
            } else if ("Top Espaços".equals(tipo)) {
                int topN = topSpinner.getValue();
                if (topN <= 0) throw new ValidacaoException(List.of("Top N deve ser maior que 0."));
                List<Map.Entry<Integer, Long>> top = relatorioService.topEspacosMaisUsados(topN);
                if (top.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Nenhum espaço encontrado para o top selecionado.");
                    alert.showAndWait();
                    return;
                }
                nomesColunas = Arrays.asList("ID Espaço", "Nome Espaço", "Reservas", "", "");
                for (Map.Entry<Integer, Long> entry : top) {
                    String nomeEspaco = espacoService.buscarPorId(entry.getKey()).getNome();  // Buscar nome
                    dados.add(FXCollections.observableArrayList(String.valueOf(entry.getKey()), nomeEspaco, String.valueOf(entry.getValue()), "", ""));
                }
                resumo = "Top " + Math.min(topN, top.size()) + " espaços.";
            } else if ("Faturamento Total".equals(tipo)) {
                double total = relatorioService.faturamentoTotal();
                if (total == 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Nenhum faturamento encontrado no sistema.");
                    alert.showAndWait();
                    return;
                }
                nomesColunas = Arrays.asList("Total", "", "", "", "");
                dados.add(FXCollections.observableArrayList(String.format("%.2f", total), "", "", "", ""));
                resumo = "Faturamento total do sistema.";
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
