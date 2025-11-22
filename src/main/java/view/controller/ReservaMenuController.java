package main.java.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.java.service.ReservaService;
import main.java.view.MainCoworking;

public class ReservaMenuController {
    @FXML private Button criarReservaButton;
    @FXML private Button cancelarReservaButton;
    @FXML private Button voltarButton;

    private MainCoworking mainApp;
    private ReservaService reservaService;

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setReservaService(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @FXML
    private void criarReserva() {
        mainApp.mudarScene("CriarReserva.fxml");
    }

    @FXML
    private void cancelarReserva() {
        mainApp.mudarScene("CancelarReserva.fxml");
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("MenuInicial.fxml");
    }
}