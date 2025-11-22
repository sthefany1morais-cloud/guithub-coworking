package main.java.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import main.java.model.espacos.Auditorio;
import main.java.model.espacos.Espaco;
import main.java.model.espacos.SalaDeReuniao;
import main.java.service.EspacoService;
import main.java.view.MainCoworking;

public class EditarEspacoDetalhesController {
    @FXML private TextField nomeField;
    @FXML private TextField capacidadeField;
    @FXML private TextField precoField;
    @FXML private TextField campoEspecificoField;
    @FXML private CheckBox disponivelCheckBox;
    @FXML private Button salvarButton;
    @FXML private Button voltarButton;

    private static Espaco espacoSelecionado;
    private MainCoworking mainApp;
    private EspacoService espacoService;

    public static void setEspacoSelecionado(Espaco espaco) {
        espacoSelecionado = espaco;
    }

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setEspacoService(EspacoService espacoService) {
        this.espacoService = espacoService;
        if (espacoSelecionado != null) {
            carregarDados();
        }
    }

    private void carregarDados() {
        nomeField.setText(espacoSelecionado.getNome());
        capacidadeField.setText(String.valueOf(espacoSelecionado.getCapacidade()));
        precoField.setText(String.valueOf(espacoSelecionado.getPrecoPorHora()));
        disponivelCheckBox.setSelected(espacoSelecionado.isDisponivel());

        // Campos específicos
        if (espacoSelecionado instanceof SalaDeReuniao) {
            campoEspecificoField.setPromptText("Taxa Fixa (deixe em branco para manter)");
            campoEspecificoField.setText(String.valueOf(((SalaDeReuniao) espacoSelecionado).getTaxaFixa()));
            campoEspecificoField.setVisible(true);
        } else if (espacoSelecionado instanceof Auditorio) {
            campoEspecificoField.setPromptText("Custo Adicional (deixe em branco para manter)");
            campoEspecificoField.setText(String.valueOf(((Auditorio) espacoSelecionado).getCustoAdicional()));
            campoEspecificoField.setVisible(true);
        } else {
            campoEspecificoField.setVisible(false);
        }
    }

    @FXML
    private void salvar() {
        try {
            // Valores: use original se vazio
            String nome = nomeField.getText().isEmpty() ? espacoSelecionado.getNome() : nomeField.getText();
            int capacidade = capacidadeField.getText().isEmpty() ? espacoSelecionado.getCapacidade() : Integer.parseInt(capacidadeField.getText());
            double preco = precoField.getText().isEmpty() ? espacoSelecionado.getPrecoPorHora() : Double.parseDouble(precoField.getText());
            boolean disponivel = disponivelCheckBox.isSelected();

            if (espacoSelecionado instanceof SalaDeReuniao) {
                double taxa = campoEspecificoField.getText().isEmpty() ? ((SalaDeReuniao) espacoSelecionado).getTaxaFixa() : Double.parseDouble(campoEspecificoField.getText());
                espacoService.atualizarSalaDeReuniao(espacoSelecionado.getId(), nome, capacidade, preco, taxa, disponivel);
            } else if (espacoSelecionado instanceof Auditorio) {
                double custo = campoEspecificoField.getText().isEmpty() ? ((Auditorio) espacoSelecionado).getCustoAdicional() : Double.parseDouble(campoEspecificoField.getText());
                espacoService.atualizarAuditorio(espacoSelecionado.getId(), nome, capacidade, preco, custo, disponivel);
            } else {
                espacoService.atualizarCabineIndividual(espacoSelecionado.getId(), nome, capacidade, preco, disponivel);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Espaço atualizado com sucesso!");
            alert.showAndWait();
            voltar();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("EditarEspacos.fxml");
    }
}
