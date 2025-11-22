package main.java.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.dao.adaptacao.EspacoDAO;
import main.java.dao.adaptacao.PagamentoDAO;
import main.java.dao.adaptacao.ReservaDAO;
import main.java.service.EspacoService;
import main.java.service.PagamentoService;
import main.java.service.RelatorioService;
import main.java.service.ReservaService;
import main.java.view.controller.*;

public class MainCoworking extends Application {
    private Stage primaryStage;

    // Services globais
    private EspacoService espacoService = new EspacoService(new EspacoDAO());
    private PagamentoService pagamentoService = new PagamentoService(new PagamentoDAO());
    private ReservaService reservaService = new ReservaService(new ReservaDAO(), espacoService, pagamentoService);
    private RelatorioService relatorioService = new RelatorioService(reservaService, pagamentoService);

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setOnCloseRequest(e -> {
            // Confirmação simples para fechar (expanda nos controllers se necessário)
            if (!confirmarSaida()) {
                e.consume(); // Cancela fechamento
            }
        });
        mudarScene("TelaInicial.fxml");
    }

    public void mudarScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();
            Object controller = loader.getController();

            // Injete services e referência ao mainApp para todos os controllers
            if (controller instanceof TelaInicialController) {
                ((TelaInicialController) controller).setMainApp(this);
            } else if (controller instanceof MenuInicialController) {
                ((MenuInicialController) controller).setMainApp(this);
                ((MenuInicialController) controller).setServices(espacoService, reservaService, relatorioService);
            } else if (controller instanceof MenuEspacosController) {
                ((MenuEspacosController) controller).setMainApp(this);
                ((MenuEspacosController) controller).setEspacoService(espacoService);
            } else if (controller instanceof CadastroEspacoController) {
                ((CadastroEspacoController) controller).setMainApp(this);
                ((CadastroEspacoController) controller).setEspacoService(espacoService);
            } else if (controller instanceof EditarEspacosController) {
                ((EditarEspacosController) controller).setMainApp(this);
                ((EditarEspacosController) controller).setEspacoService(espacoService);
                ((EditarEspacosController) controller).setReservaService(reservaService);  // Adicionado para excluir()
            } else if (controller instanceof EditarEspacoDetalhesController) {
                ((EditarEspacoDetalhesController) controller).setMainApp(this);
                ((EditarEspacoDetalhesController) controller).setEspacoService(espacoService);
            } else if (controller instanceof ReservaMenuController) {
                ((ReservaMenuController) controller).setMainApp(this);
                ((ReservaMenuController) controller).setReservaService(reservaService);
            } else if (controller instanceof CriarReservaController) {
                ((CriarReservaController) controller).setMainApp(this);
                ((CriarReservaController) controller).setServices(espacoService, reservaService);
            } else if (controller instanceof DetalhesReservaController) {
                ((DetalhesReservaController) controller).setMainApp(this);
                ((DetalhesReservaController) controller).setServices(espacoService, reservaService);
            } else if (controller instanceof CancelarReservaController) {
                ((CancelarReservaController) controller).setMainApp(this);
                ((CancelarReservaController) controller).setReservaService(reservaService);
            } else if (controller instanceof RelatoriosController) {
                ((RelatoriosController) controller).setMainApp(this);
                ((RelatoriosController) controller).setRelatorioService(relatorioService);
            } else if (controller instanceof RelatorioPopupController) {
                ((RelatorioPopupController) controller).setRelatorioService(relatorioService);
            }

            Scene scene = new Scene(root, 600, 400);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Sistema Coworking");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Adicione Alert para erro de carregamento se quiser
        }
    }

    private boolean confirmarSaida() {
        // Simples confirmação (expanda nos controllers se necessário)
        return true; // Permite sair; use Alert para confirmação real
    }

    public static void main(String[] args) {
        launch(args);
    }
}
