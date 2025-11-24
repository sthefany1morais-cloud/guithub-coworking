package main.java.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import main.java.service.EspacoService;
import main.java.view.MainCoworking;

public class MenuEspacosController {
    @FXML private Button criarEspacoButton;
    @FXML private Button editarEspacosButton;
    @FXML private Button voltarButton;
    @FXML private Label mensagemLabel;

    private MainCoworking mainApp;
    private EspacoService espacoService;

    public void setMainApp(MainCoworking mainApp) {
        this.mainApp = mainApp;
    }

    public void setEspacoService(EspacoService espacoService) {
        this.espacoService = espacoService;
        verificarEspacos();
    }

    private void verificarEspacos() {
        if (espacoService.listarTodos().isEmpty()) {
            mensagemLabel.setText("Nenhum espaço encontrado. Crie um novo espaço primeiro.");
        } else {
            mensagemLabel.setText("");
        }
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
