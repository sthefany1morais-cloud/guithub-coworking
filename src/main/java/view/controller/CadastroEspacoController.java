package main.java.view.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import main.java.service.EspacoService;
import main.java.view.MainCoworking;

public class CadastroEspacoController {
    @FXML private ComboBox<String> tipoComboBox;
    @FXML private TextField nomeField;
    @FXML private TextField capacidadeField;
    @FXML private TextField precoField;
    @FXML private TextField campoEspecificoField;
    @FXML private Button salvarButton;
    @FXML private Button limparButton;
    @FXML private Button voltarButton;

    private MainCoworking mainApp;
    private EspacoService espacoService;

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setEspacoService(EspacoService espacoService) {
        this.espacoService = espacoService;
    }

    @FXML
    private void initialize() {
        tipoComboBox.setItems(FXCollections.observableArrayList("Sala de Reunião", "Cabine Individual", "Auditório"));
        tipoComboBox.setOnAction(e -> atualizarCamposEspecificos());
    }
    @FXML
    private void atualizarCamposEspecificos() {
        String tipo = tipoComboBox.getValue();
        if ("Sala de Reunião".equals(tipo)) {
            campoEspecificoField.setPromptText("Taxa Fixa");
            campoEspecificoField.setVisible(true);
        } else if ("Auditório".equals(tipo)) {
            campoEspecificoField.setPromptText("Custo Adicional");
            campoEspecificoField.setVisible(true);
        } else {
            campoEspecificoField.setVisible(false);
        }
    }

    @FXML
    private void salvar() {
        try {
            String tipo = tipoComboBox.getValue();
            String nome = nomeField.getText();
            int capacidade = Integer.parseInt(capacidadeField.getText());
            double preco = Double.parseDouble(precoField.getText());

            if ("Sala de Reunião".equals(tipo)) {
                double taxa = Double.parseDouble(campoEspecificoField.getText());
                espacoService.cadastrarSalaDeReuniao(nome, capacidade, preco, taxa);
            } else if ("Cabine Individual".equals(tipo)) {
                espacoService.cadastrarCabineIndividual(nome, capacidade, preco);
            } else if ("Auditório".equals(tipo)) {
                double custo = Double.parseDouble(campoEspecificoField.getText());
                espacoService.cadastrarAuditorio(nome, capacidade, preco, custo);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Espaço criado com sucesso!");
            alert.showAndWait();
            limpar();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void limpar() {
        nomeField.clear();
        capacidadeField.clear();
        precoField.clear();
        campoEspecificoField.clear();
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("MenuEspacos.fxml");
    }
}
