package main.java.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import main.java.service.EspacoService;
import main.java.service.RelatorioService;
import main.java.service.ReservaService;
import main.java.util.VerificacaoUtil;
import main.java.view.MainCoworking;

public class MenuInicialController {
    @FXML private Button gerenciarEspacosButton;
    @FXML private Button gerenciarReservasButton;
    @FXML private Button relatoriosButton;
    @FXML private Button voltarButton;
    @FXML private Label mensagemLabel;

    private MainCoworking mainApp;
    private EspacoService espacoService;
    private ReservaService reservaService;
    private RelatorioService relatorioService;

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setServices(EspacoService espacoService, ReservaService reservaService, RelatorioService relatorioService) {
        this.espacoService = espacoService;
        this.reservaService = reservaService;
        this.relatorioService = relatorioService;
        // Usar VerificacaoUtil
        mensagemLabel.setText(VerificacaoUtil.verificarEstadoDados(espacoService, reservaService));
    }

    @FXML
    private void gerenciarEspacos() {
        mainApp.mudarScene("MenuEspacos.fxml");
    }

    @FXML
    private void gerenciarReservas() {
        mainApp.mudarScene("ReservaMenu.fxml");
    }

    @FXML
    private void relatorios() {
        mainApp.mudarScene("Relatorios.fxml");
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("TelaInicial.fxml");
    }
}