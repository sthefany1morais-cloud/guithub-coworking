package main.java.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.execoes.*;
import main.java.model.espacos.Auditorio;
import main.java.model.espacos.Espaco;
import main.java.model.espacos.SalaDeReuniao;
import main.java.service.EspacoService;
import main.java.util.CampoUtil;
import main.java.util.FormatadorUtil;
import main.java.util.MensagemUtil;
import main.java.view.MainCoworking;
import main.java.service.SistemaService;

public class EditarEspacoDetalhesController {

    private SistemaService sistemaService;

    @FXML private TextField nomeField;
    @FXML private TextField capacidadeField;
    @FXML private TextField precoField;
    @FXML private TextField campoEspecificoField;
    @FXML private CheckBox disponivelCheckBox;
    @FXML private Button salvarButton;
    @FXML private Button voltarButton;
    @FXML private Label errosLabel;
    @FXML private Label campoEspecificoLabel;

    private static Espaco espacoSelecionado;
    private MainCoworking mainApp;
    private EspacoService espacoService;
    private boolean isLoading = false;

    public static void setEspacoSelecionado(Espaco espaco) {
        espacoSelecionado = espaco;
    }

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setEspacoService(EspacoService espacoService) {
        this.espacoService = espacoService;
    }

    public void setSistemaService(SistemaService sistemaService) {
        this.sistemaService = sistemaService;
        if (sistemaService != null && espacoSelecionado != null) {
            carregarDados();
        }
        if (sistemaService != null) {
            CampoUtil.adicionarListenerFormatacaoDinheiro(precoField);
            CampoUtil.adicionarListenerFormatacaoDinheiro(campoEspecificoField);
        }
    }

    private void carregarDados() {
        isLoading = true;
        nomeField.setPromptText(espacoSelecionado.getNome());
        capacidadeField.setPromptText(String.valueOf(espacoSelecionado.getCapacidade()));
        precoField.setText(FormatadorUtil.formatarDinheiro(espacoSelecionado.getPrecoPorHora()));
        if (espacoSelecionado instanceof SalaDeReuniao) {
            campoEspecificoLabel.setText("Taxa Fixa:");
            campoEspecificoLabel.setVisible(true);
            campoEspecificoField.setText(FormatadorUtil.formatarDinheiro(((SalaDeReuniao) espacoSelecionado).getTaxaFixa()));
            campoEspecificoField.setVisible(true);
        } else if (espacoSelecionado instanceof Auditorio) {
            campoEspecificoLabel.setText("Custo Adicional:");
            campoEspecificoLabel.setVisible(true);
            campoEspecificoField.setText(FormatadorUtil.formatarDinheiro(((Auditorio) espacoSelecionado).getCustoAdicional()));
            campoEspecificoField.setVisible(true);
        } else {
            campoEspecificoLabel.setVisible(false);
            campoEspecificoField.setVisible(false);
        }
        disponivelCheckBox.setSelected(espacoSelecionado.isDisponivel());
        isLoading = false;
    }

    @FXML
    private void salvar() {
        try {
            String nome = nomeField.getText().trim().isEmpty() ? espacoSelecionado.getNome() : nomeField.getText().trim();
            String capText = capacidadeField.getText().trim();
            int capacidade = capText.isEmpty() ? espacoSelecionado.getCapacidade() : Integer.parseInt(capText);
            String precoText = precoField.getText().trim();
            double preco = precoText.isEmpty() || "0,00".equals(precoText) ? espacoSelecionado.getPrecoPorHora() : Double.parseDouble(precoText.replace(",", "."));
            boolean disponivel = disponivelCheckBox.isSelected();
            if (!capText.isEmpty() && capacidade <= 0) throw new CapacidadeInvalidaException("Capacidade deve ser maior que 0.");
            if (!precoText.isEmpty() && !precoText.equals("0,00") && preco <= 0) throw new PrecoPorHoraInvalidoException("Preço deve ser maior que 0.");

            if (espacoSelecionado instanceof SalaDeReuniao) {
                String taxaText = campoEspecificoField.getText().trim();
                double taxa = taxaText.isEmpty() || "0,00".equals(taxaText) ? ((SalaDeReuniao) espacoSelecionado).getTaxaFixa() : Double.parseDouble(taxaText.replace(",", "."));
                if (!taxaText.isEmpty() && !taxaText.equals("0,00") && taxa <= 0) throw new TaxaFixaInvalidaException("Taxa fixa deve ser maior que 0.");
                espacoService.atualizarSalaDeReuniao(espacoSelecionado.getId(), nome, capacidade, preco, taxa, disponivel);
            } else if (espacoSelecionado instanceof Auditorio) {
                String custoText = campoEspecificoField.getText().trim();
                double custo = custoText.isEmpty() || "0,00".equals(custoText) ? ((Auditorio) espacoSelecionado).getCustoAdicional() : Double.parseDouble(custoText.replace(",", "."));
                if (!custoText.isEmpty() && !custoText.equals("0,00") && custo <= 0) throw new CustoAdicionalInvalidoException("Custo adicional deve ser maior que 0.");
                espacoService.atualizarAuditorio(espacoSelecionado.getId(), nome, capacidade, preco, custo, disponivel);
            } else {
                espacoService.atualizarCabineIndividual(espacoSelecionado.getId(), nome, capacidade, preco, disponivel);
            }
            MensagemUtil.limparMensagens(errosLabel);
            MensagemUtil.mostrarAlertaInformacao("Sucesso", "Espaço atualizado com sucesso!");
            voltar();
        } catch (CapacidadeInvalidaException | PrecoPorHoraInvalidoException | EspacoJaExistenteException |
                 TaxaFixaInvalidaException | CustoAdicionalInvalidoException e) {
            MensagemUtil.definirErro(errosLabel, "Erro: " + e.getMessage());
        } catch (Exception e) {
            MensagemUtil.definirErro(errosLabel, "Erro inesperado: " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        CampoUtil.configurarCampoInteiro(capacidadeField);
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("EditarEspacos.fxml");
    }
}