package main.java.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.java.view.MainCoworking;

public class TelaInicialController {
    @FXML private Button iniciarButton;
    @FXML private Button sairButton;

    private MainCoworking mainApp;

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void iniciarSistema() {
        mainApp.mudarScene("MenuInicial.fxml");
    }

    @FXML
    private void sair() {
        System.exit(0);
    }
}