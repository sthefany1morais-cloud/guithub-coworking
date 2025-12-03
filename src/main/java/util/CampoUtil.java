package main.java.util;

import javafx.scene.control.TextField;
import java.util.function.UnaryOperator;
import javafx.scene.control.TextFormatter.Change;

public class CampoUtil {

    public static void configurarCampoInteiro(TextField field) {
        UnaryOperator<Change> intFilter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
        };
        field.setTextFormatter(new javafx.scene.control.TextFormatter<>(intFilter));
    }

    public static void configurarCampoHora(TextField field) {
        UnaryOperator<Change> horaFilter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("[\\d:]{0,5}") ? change : null;
        };
        field.setTextFormatter(new javafx.scene.control.TextFormatter<>(horaFilter));
    }

    public static void adicionarListenerFormatacaoDinheiro(TextField field) {
        field.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.equals(FormatadorUtil.formatarDinheiro(newText))) {
                field.setText(FormatadorUtil.formatarDinheiro(newText));
            }
        });
    }
}
