package main.java.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import main.java.dao.adaptacao.EspacoDAO;
import main.java.dao.adaptacao.PagamentoDAO;
import main.java.dao.adaptacao.ReservaDAO;
import main.java.service.EspacoService;
import main.java.service.PagamentoService;
import main.java.service.RelatorioService;
import main.java.service.ReservaService;
import main.java.view.controller.*;
import main.java.service.SistemaService;

import java.util.Optional;

public class MainCoworking extends Application {
    private Stage primaryStage;

    // Services globais
    private EspacoService espacoService = new EspacoService(new EspacoDAO());
    private PagamentoService pagamentoService = new PagamentoService(new PagamentoDAO());
    private ReservaService reservaService = new ReservaService(new ReservaDAO(), espacoService, pagamentoService);
    private RelatorioService relatorioService = new RelatorioService(reservaService, pagamentoService);
    private SistemaService sistemaService = new SistemaService();  // Novo service para persistência

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setOnCloseRequest(e -> {
            if (!confirmarSaida()) {
                e.consume();  // Cancela fechamento se não confirmar
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
                ((CadastroEspacoController) controller).setSistemaService(sistemaService);
            } else if (controller instanceof EditarEspacosController) {
                ((EditarEspacosController) controller).setMainApp(this);
                ((EditarEspacosController) controller).setEspacoService(espacoService);
                ((EditarEspacosController) controller).setReservaService(reservaService);  // Adicionado para excluir()
            } else if (controller instanceof EditarEspacoDetalhesController) {
                ((EditarEspacoDetalhesController) controller).setMainApp(this);
                ((EditarEspacoDetalhesController) controller).setEspacoService(espacoService);
                ((EditarEspacoDetalhesController) controller).setSistemaService(sistemaService);
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

            Scene scene = new Scene(root, 800, 600);  // Aumentado de 600x400 para 800x600
            primaryStage.setScene(scene);
            primaryStage.setTitle("Sistema Coworking");
            primaryStage.setResizable(true);  // Permite redimensionar
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Adicione Alert para erro de carregamento se quiser
        }
    }

    private boolean confirmarSaida() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deseja salvar os dados antes de sair?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle("Confirmação de Saída");
        alert.setHeaderText("Sair do Sistema");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.YES) {
                // Persistir dados: chama service para sincronizar com ObjectDB
                sistemaService.persistirDados();  // Respeita MVC
                return true;  // Sai após salvar
            } else if (result.get() == ButtonType.NO) {
                return true;  // Sai sem salvar
            } else {
                return false;  // Cancela saída
            }
        }
        return false;  // Cancela por padrão
    }

    public static void main(String[] args) {
        launch(args);
    }
}
