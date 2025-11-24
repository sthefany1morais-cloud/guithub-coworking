package main.java.view.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.execoes.EspacoJaExistenteException;
import main.java.execoes.ValidacaoException;
import main.java.service.EspacoService;
import main.java.view.MainCoworking;

import java.util.ArrayList;
import java.util.List;

public class CadastroEspacoController {
    @FXML private ComboBox<String> tipoComboBox;
    @FXML private TextField nomeField;
    @FXML private TextField capacidadeField;
    @FXML private TextField precoField;
    @FXML private TextField campoEspecificoField;
    @FXML private Button salvarButton;
    @FXML private Button limparButton;
    @FXML private Button voltarButton;
    @FXML private Label errosLabel;

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
            List<String> erros = new ArrayList<>();
            try {
                String tipo = tipoComboBox.getValue();
                String nome = nomeField.getText().trim();
                String capText = capacidadeField.getText().trim();
                String precoText = precoField.getText().trim();
                String especificoText = campoEspecificoField.getText().trim();

                // Validações para múltiplos erros
                if (nome.isEmpty()) erros.add("Nome não pode ser vazio.");
                if (capText.isEmpty() || !capText.matches("\\d+")) erros.add("Capacidade deve ser um número positivo.");
                else if (Integer.parseInt(capText) <= 0) erros.add("Capacidade não pode ser zero ou negativa.");
                if (precoText.isEmpty() || !precoText.matches("\\d+(\\.\\d+)?"))
                    erros.add("Preço por hora deve ser um número positivo.");
                else if (Double.parseDouble(precoText) <= 0) erros.add("Preço por hora não pode ser zero ou negativo.");
                if ("Sala de Reunião".equals(tipo) && (especificoText.isEmpty() || !especificoText.matches("\\d+(\\.\\d+)?")))
                    erros.add("Taxa fixa deve ser um número positivo.");
                if ("Auditório".equals(tipo) && (especificoText.isEmpty() || !especificoText.matches("\\d+(\\.\\d+)?")))
                    erros.add("Custo adicional deve ser um número positivo.");

                if (!erros.isEmpty()) {
                    throw new ValidacaoException(erros);
                }

                int capacidade = Integer.parseInt(capText);
                double preco = Double.parseDouble(precoText);

                if ("Sala de Reunião".equals(tipo)) {
                    double taxa = Double.parseDouble(especificoText);
                    espacoService.cadastrarSalaDeReuniao(nome, capacidade, preco, taxa);
                } else if ("Cabine Individual".equals(tipo)) {
                    espacoService.cadastrarCabineIndividual(nome, capacidade, preco);
                } else if ("Auditório".equals(tipo)) {
                    double custo = Double.parseDouble(especificoText);
                    espacoService.cadastrarAuditorio(nome, capacidade, preco, custo);
                }
                errosLabel.setText("");  // Limpar erros
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Espaço criado com sucesso!");
                alert.showAndWait();
                limpar();
            } catch (ValidacaoException e) {
                errosLabel.setText("Erros encontrados:\n" + String.join("\n", e.getErros()));
            } catch (EspacoJaExistenteException e) {
                errosLabel.setText("Erro: " + e.getMessage());
            } catch (Exception e) {
                errosLabel.setText("Erro inesperado: " + e.getMessage());
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
