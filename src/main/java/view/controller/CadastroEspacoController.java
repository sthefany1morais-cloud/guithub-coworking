package main.java.view.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.execoes.EspacoJaExistenteException;
import main.java.execoes.ValidacaoException;
import main.java.service.EspacoService;
import main.java.util.CampoUtil;
import main.java.util.MensagemUtil;
import main.java.util.ValidacaoUtil;
import main.java.view.MainCoworking;
import main.java.service.SistemaService;

import java.util.Arrays;
import java.util.List;

public class CadastroEspacoController {

    private SistemaService sistemaService;

    @FXML private ComboBox<String> tipoComboBox;
    @FXML private TextField nomeField;
    @FXML private TextField capacidadeField;
    @FXML private TextField precoField;
    @FXML private TextField campoEspecificoField;
    @FXML private Button salvarButton;
    @FXML private Button limparButton;
    @FXML private Button voltarButton;
    @FXML private Label errosLabel;
    @FXML private Label campoEspecificoLabel;

    private MainCoworking mainApp;
    private EspacoService espacoService;

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setEspacoService(EspacoService espacoService) {
        this.espacoService = espacoService;
    }

    public void setSistemaService(SistemaService sistemaService) {
        this.sistemaService = sistemaService;
        CampoUtil.adicionarListenerFormatacaoDinheiro(precoField);
        CampoUtil.adicionarListenerFormatacaoDinheiro(campoEspecificoField);
    }

    @FXML
    private void initialize() {
        tipoComboBox.setItems(FXCollections.observableArrayList("Sala de Reunião", "Cabine Individual", "Auditório"));
        tipoComboBox.setOnAction(e -> {
            atualizarCamposEspecificos();
            verificarHabilitarBotao();
        });
        CampoUtil.configurarCampoInteiro(capacidadeField);
        precoField.setText("0,00");
        campoEspecificoField.setText("0,00");

        CampoUtil.habilitarBotaoSeCamposPreenchidos(
                salvarButton,
                Arrays.asList(),
                Arrays.asList(nomeField, capacidadeField, precoField),
                Arrays.asList(tipoComboBox),
                Arrays.asList()
        );
    }

    private void verificarHabilitarBotao() {
        String tipo = tipoComboBox.getValue();
        boolean camposBasicosPreenchidos = nomeField.getText() != null && !nomeField.getText().trim().isEmpty() &&
                capacidadeField.getText() != null && !capacidadeField.getText().trim().isEmpty() &&
                precoField.getText() != null && !precoField.getText().trim().isEmpty() &&
                tipo != null;
        boolean campoEspecificoPreenchido = true;
        if ("Sala de Reunião".equals(tipo) || "Auditório".equals(tipo)) {
            campoEspecificoPreenchido = campoEspecificoField.getText() != null && !campoEspecificoField.getText().trim().isEmpty();
        }
        salvarButton.setDisable(!(camposBasicosPreenchidos && campoEspecificoPreenchido));
    }

    @FXML
    private void atualizarCamposEspecificos() {
        String tipo = tipoComboBox.getValue();
        if ("Sala de Reunião".equals(tipo)) {
            campoEspecificoLabel.setText("Taxa Fixa:");
            campoEspecificoLabel.setVisible(true);
            campoEspecificoField.setVisible(true);
        } else if ("Auditório".equals(tipo)) {
            campoEspecificoLabel.setText("Custo Adicional:");
            campoEspecificoLabel.setVisible(true);
            campoEspecificoField.setVisible(true);
        } else {
            campoEspecificoLabel.setVisible(false);
            campoEspecificoField.setVisible(false);
        }
    }

    @FXML
    private void salvar() {
        try {
            String tipo = tipoComboBox.getValue();
            String nome = nomeField.getText().trim();
            String capText = capacidadeField.getText().trim();
            String precoText = precoField.getText().trim();
            String especificoText = campoEspecificoField.getText().trim();

            List<String> erros = ValidacaoUtil.validarCamposEspaco(nome, capText, precoText, tipo, especificoText);
            if (!erros.isEmpty()) {
                throw new ValidacaoException(erros);
            }

            int capacidade = Integer.parseInt(capText);
            double preco = Double.parseDouble(precoText.replace(",", "."));
            if ("Sala de Reunião".equals(tipo)) {
                double taxa = Double.parseDouble(especificoText.replace(",", "."));
                espacoService.cadastrarSalaDeReuniao(nome, capacidade, preco, taxa);
            } else if ("Cabine Individual".equals(tipo)) {
                espacoService.cadastrarCabineIndividual(nome, capacidade, preco);
            } else if ("Auditório".equals(tipo)) {
                double custo = Double.parseDouble(especificoText.replace(",", "."));
                espacoService.cadastrarAuditorio(nome, capacidade, preco, custo);
            }
            MensagemUtil.limparMensagens(errosLabel);
            MensagemUtil.mostrarAlertaInformacao("Sucesso", "Espaço criado com sucesso!");
            limpar();
        } catch (ValidacaoException e) {
            MensagemUtil.definirErro(errosLabel, "Erros encontrados:\n" + String.join("\n", e.getErros()));
        } catch (EspacoJaExistenteException e) {
            MensagemUtil.definirErro(errosLabel, "Erro: " + e.getMessage());
        } catch (Exception e) {
            MensagemUtil.definirErro(errosLabel, "Erro inesperado: " + e.getMessage());
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