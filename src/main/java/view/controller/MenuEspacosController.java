package main.java.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.java.service.EspacoService;
import main.java.view.MainCoworking;

public class MenuEspacosController {
    @FXML private Button criarEspacoButton;
    @FXML private Button editarEspacosButton;
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
    private void criarEspaco() {
        mainApp.mudarScene("CadastroEspaco.fxml");
    }

    @FXML
    private void editarEspacos() {
        mainApp.mudarScene("EditarEspacos.fxml");
    }

    @FXML
    private void voltar() {
        mainApp.mudarScene("MenuInicial.fxml");
    }
}
