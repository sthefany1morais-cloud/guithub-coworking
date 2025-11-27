package main.java.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class MensagemUtil {

    /**
     * Mostra um alert de informação.
     */
    public static void mostrarAlertaInformacao(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensagem);
        alert.setTitle(titulo);
        alert.showAndWait();
    }

    /**
     * Mostra um alert de erro.
     */
    public static void mostrarAlertaErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensagem);
        alert.setTitle(titulo);
        alert.showAndWait();
    }

    /**
     * Define texto de erro em um label.
     */
    public static void definirErro(Label label, String mensagem) {
        label.setText(mensagem);
    }

    /**
     * Limpa mensagens em labels.
     */
    public static void limparMensagens(Label... labels) {
        for (Label label : labels) {
            label.setText("");
        }
    }
}
