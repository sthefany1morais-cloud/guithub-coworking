package main.java.view.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import main.java.service.RelatorioService;
import main.java.util.MensagemUtil;
import main.java.util.TabelaUtil;

import java.util.List;

public class RelatorioPopupController {
    @FXML private Label tituloPopupLabel;
    @FXML private TableView<ObservableList<String>> relatorioPopupTableView;
    @FXML private TableColumn<ObservableList<String>, String> coluna1;
    @FXML private TableColumn<ObservableList<String>, String> coluna2;
    @FXML private TableColumn<ObservableList<String>, String> coluna3;
    @FXML private TableColumn<ObservableList<String>, String> coluna4;
    @FXML private TableColumn<ObservableList<String>, String> coluna5;
    @FXML private Label resumoPopupLabel;
    @FXML private Button fecharButton;
    @FXML private Label mensagemLabel;

    private RelatorioService relatorioService;

    public void setRelatorioService(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    public void setDados(ObservableList<ObservableList<String>> dados, String resumo, List<String> nomesColunas) {
        if (dados.isEmpty()) {
            MensagemUtil.definirErro(mensagemLabel, "Nenhum dado encontrado para o relatório.");
            relatorioPopupTableView.setItems(FXCollections.emptyObservableList());
        } else {
            // Usar TabelaUtil
            TabelaUtil.configurarColunasDinamicas(relatorioPopupTableView, List.of(coluna1, coluna2, coluna3, coluna4, coluna5), nomesColunas);
            relatorioPopupTableView.setItems(dados);
            MensagemUtil.limparMensagens(mensagemLabel);
        }
        resumoPopupLabel.setText(resumo);
    }

    @FXML
    private void fechar() {
        fecharButton.getScene().getWindow().hide();
    }
}