package main.java.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.execoes.*;
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
    @FXML private Label errosLabel;

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
            String nome = nomeField.getText().trim().isEmpty() ? espacoSelecionado.getNome() : nomeField.getText().trim();
            String capText = capacidadeField.getText().trim().isEmpty() ? String.valueOf(espacoSelecionado.getCapacidade()) : capacidadeField.getText().trim();
            String precoText = precoField.getText().trim().isEmpty() ? String.valueOf(espacoSelecionado.getPrecoPorHora()) : precoField.getText().trim();
            boolean disponivel = disponivelCheckBox.isSelected();
            int capacidade = Integer.parseInt(capText);
            double preco = Double.parseDouble(precoText);

            if (espacoSelecionado instanceof SalaDeReuniao) {
                double taxa = campoEspecificoField.getText().isEmpty() ? ((SalaDeReuniao) espacoSelecionado).getTaxaFixa() : Double.parseDouble(campoEspecificoField.getText());
                espacoService.atualizarSalaDeReuniao(espacoSelecionado.getId(), nome, capacidade, preco, taxa, disponivel);
            } else if (espacoSelecionado instanceof Auditorio) {
                double custo = campoEspecificoField.getText().isEmpty() ? ((Auditorio) espacoSelecionado).getCustoAdicional() : Double.parseDouble(campoEspecificoField.getText());
                espacoService.atualizarAuditorio(espacoSelecionado.getId(), nome, capacidade, preco, custo, disponivel);
            } else {
                espacoService.atualizarCabineIndividual(espacoSelecionado.getId(), nome, capacidade, preco, disponivel);
            }

            errosLabel.setText("");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Espaço atualizado com sucesso!");
            alert.showAndWait();
            voltar();
        } catch (CapacidadeInvalidaException | PrecoPorHoraInvalidoException | EspacoJaExistenteException |
                 TaxaFixaInvalidaException | CustoAdicionalInvalidoException e) {
            errosLabel.setText("Erro: " + e.getMessage());
        } catch (Exception e) {
            errosLabel.setText("Erro inesperado: " + e.getMessage());
        }
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("EditarEspacos.fxml");
    }
}