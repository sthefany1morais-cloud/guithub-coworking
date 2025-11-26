package main.java.view.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.service.RelatorioService;

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
            mensagemLabel.setText("Nenhum dado encontrado para o relatório.");
            relatorioPopupTableView.setItems(FXCollections.emptyObservableList());
        } else {
            // Definir nomes das colunas dinamicamente
            coluna1.setText(nomesColunas.size() > 0 ? nomesColunas.get(0) : "Coluna 1");
            coluna2.setText(nomesColunas.size() > 1 ? nomesColunas.get(1) : "Coluna 2");
            coluna3.setText(nomesColunas.size() > 2 ? nomesColunas.get(2) : "Coluna 3");
            coluna4.setText(nomesColunas.size() > 3 ? nomesColunas.get(3) : "Coluna 4");
            coluna5.setText(nomesColunas.size() > 4 ? nomesColunas.get(4) : "Coluna 5");
            // Usar lambdas para acessar índices da ObservableList<String>
            coluna1.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
            coluna2.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().size() > 1 ? data.getValue().get(1) : ""));
            coluna3.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().size() > 2 ? data.getValue().get(2) : ""));
            coluna4.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().size() > 3 ? data.getValue().get(3) : ""));
            coluna5.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().size() > 4 ? data.getValue().get(4) : ""));
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