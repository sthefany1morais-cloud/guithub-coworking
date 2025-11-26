package main.java.view;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import main.java.dao.adaptacao.*;
import main.java.service.*;
import main.java.view.util.GerenciadorDeTelas;

import java.util.Optional;

public class MainCoworking extends Application {

    private Stage primaryStage;
    private GerenciadorDeTelas gerenciador;

    private EspacoService espacoService = new EspacoService(new EspacoDAO());
    private PagamentoService pagamentoService = new PagamentoService(new PagamentoDAO());
    private ReservaService reservaService = new ReservaService(new ReservaDAO(), espacoService, pagamentoService);
    private RelatorioService relatorioService = new RelatorioService(reservaService, pagamentoService, espacoService);
    private SistemaService sistemaService = new SistemaService();

    public void mudarScene(String fxml) {
        gerenciador.mudarCena(fxml);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        gerenciador = new GerenciadorDeTelas(
                this,
                espacoService,
                reservaService,
                pagamentoService,
                relatorioService,
                sistemaService
        );

        primaryStage.setOnCloseRequest(e -> {
            if (!confirmarSaida()) {
                e.consume();
            }
        });

        this.mudarScene("TelaInicial.fxml");
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private boolean confirmarSaida() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja salvar os dados antes de sair?",
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

        alert.setTitle("Confirmação de Saída");
        alert.setHeaderText("Sair do Sistema");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == ButtonType.YES) {
                sistemaService.persistirDados();
                return true;

            } else if (result.get() == ButtonType.NO) {
                return true;

            } else {
                return false;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
