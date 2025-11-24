package main.java.view.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.service.RelatorioService;

public class RelatorioPopupController {
    @FXML private Label tituloPopupLabel;
    @FXML private TableView<ObservableList<String>> relatorioPopupTableView;
    @FXML private TableColumn<ObservableList<String>, String> coluna1;
    @FXML private TableColumn<ObservableList<String>, String> coluna2;
    @FXML private TableColumn<ObservableList<String>, String> coluna3;
    @FXML private TableColumn<ObservableList<String>, String> coluna4;
    @FXML private Label resumoPopupLabel;
    @FXML private Button fecharButton;
    @FXML private Label mensagemLabel;

    private RelatorioService relatorioService;

    public void setRelatorioService(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    public void setDados(ObservableList<ObservableList<String>> dados, String resumo) {
        if (dados.isEmpty()) {
            mensagemLabel.setText("Nenhum dado encontrado para o relatório.");
            relatorioPopupTableView.setItems(FXCollections.emptyObservableList());
        } else {
            coluna1.setCellValueFactory(new PropertyValueFactory<>("0"));
            coluna2.setCellValueFactory(new PropertyValueFactory<>("1"));
            coluna3.setCellValueFactory(new PropertyValueFactory<>("2"));
            coluna4.setCellValueFactory(new PropertyValueFactory<>("3"));
            relatorioPopupTableView.setItems(dados);
            mensagemLabel.setText("");
        }
        resumoPopupLabel.setText(resumo);
    }

    @FXML
    private void fechar() {
        // Fechar popup
        fecharButton.getScene().getWindow().hide();
    }
}