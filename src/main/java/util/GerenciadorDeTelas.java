package main.java.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import main.java.service.*;
import main.java.view.MainCoworking;
import main.java.view.controller.*;

public class GerenciadorDeTelas {

    private final MainCoworking mainApp;

    private final EspacoService espacoService;
    private final ReservaService reservaService;
    private final PagamentoService pagamentoService;
    private final RelatorioService relatorioService;
    private final SistemaService sistemaService;

    public GerenciadorDeTelas(
            MainCoworking mainApp,
            EspacoService espacoService,
            ReservaService reservaService,
            PagamentoService pagamentoService,
            RelatorioService relatorioService,
            SistemaService sistemaService
    ) {
        this.mainApp = mainApp;
        this.espacoService = espacoService;
        this.reservaService = reservaService;
        this.pagamentoService = pagamentoService;
        this.relatorioService = relatorioService;
        this.sistemaService = sistemaService;
    }

    public void mudarCena(String fxmlFile) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();
            Object controller = loader.getController();

            injetarDependencias(controller);

            Scene scene = new Scene(root, 800, 600);
            mainApp.getPrimaryStage().setScene(scene);
            mainApp.getPrimaryStage().setTitle("Sistema Coworking");
            mainApp.getPrimaryStage().setResizable(true);
            mainApp.getPrimaryStage().show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro(fxmlFile);
        }
    }

    private void mostrarErro(String fxmlFile) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro ao Carregar Tela");
        alert.setHeaderText("Não foi possível carregar a interface");
        alert.setContentText("O arquivo " + fxmlFile + " não pôde ser carregado.\n" +
                "Verifique se o FXML existe e está correto.");
        alert.showAndWait();
    }

    private void injetarDependencias(Object controller) {

        if (controller instanceof TelaInicialController c) {
            c.setMainApp(mainApp);

        } else if (controller instanceof MenuInicialController c) {
            c.setMainApp(mainApp);
            c.setServices(espacoService, reservaService, relatorioService);

        } else if (controller instanceof MenuEspacosController c) {
            c.setMainApp(mainApp);
            c.setEspacoService(espacoService);

        } else if (controller instanceof CadastroEspacoController c) {
            c.setMainApp(mainApp);
            c.setEspacoService(espacoService);
            c.setSistemaService(sistemaService);

        } else if (controller instanceof EditarEspacosController c) {
            c.setMainApp(mainApp);
            c.setEspacoService(espacoService);
            c.setReservaService(reservaService);

        } else if (controller instanceof EditarEspacoDetalhesController c) {
            c.setMainApp(mainApp);
            c.setEspacoService(espacoService);
            c.setSistemaService(sistemaService);

        } else if (controller instanceof ReservaMenuController c) {
            c.setMainApp(mainApp);
            c.setReservaService(reservaService);

        } else if (controller instanceof CriarReservaController c) {
            c.setMainApp(mainApp);
            c.setServices(espacoService);

        } else if (controller instanceof DetalhesReservaController c) {
            c.setMainApp(mainApp);
            c.setServices(espacoService, reservaService);

        } else if (controller instanceof CancelarReservaController c) {
            c.setMainApp(mainApp);
            c.setReservaService(reservaService);

        } else if (controller instanceof RelatoriosController c) {
            c.setMainApp(mainApp);
            c.setRelatorioService(relatorioService);
            c.setEspacoService(espacoService);

        } else if (controller instanceof RelatorioPopupController c) {
            c.setRelatorioService(relatorioService);
        }
    }
}

