package main.java.util;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter.Change;
import java.util.List;
import java.util.function.UnaryOperator;

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

    public static void habilitarBotaoSeCamposPreenchidos(Button botao, List<DatePicker> datePickers, List<TextField> textFields, List<ComboBox<?>> comboBoxes, List<Spinner<?>> spinners) {
        Runnable verificacao = () -> {
            boolean todosPreenchidos = true;

            for (DatePicker dp : datePickers) {
                if (dp.getValue() == null) {
                    todosPreenchidos = false;
                    break;
                }
            }

            for (TextField tf : textFields) {
                if (tf.getText() == null || tf.getText().trim().isEmpty()) {
                    todosPreenchidos = false;
                    break;
                }
            }

            for (ComboBox<?> cb : comboBoxes) {
                if (cb.getValue() == null) {
                    todosPreenchidos = false;
                    break;
                }
            }

            for (Spinner<?> sp : spinners) {
                if (sp.getValue() == null || (sp.getValue() instanceof Integer && (Integer) sp.getValue() <= 0)) {
                    todosPreenchidos = false;
                    break;
                }
            }

            botao.setDisable(!todosPreenchidos);
        };

        // Adiciona listeners a todos os campos
        for (DatePicker dp : datePickers) {
            dp.valueProperty().addListener((obs, oldVal, newVal) -> verificacao.run());
        }

        for (TextField tf : textFields) {
            tf.textProperty().addListener((obs, oldVal, newVal) -> verificacao.run());
        }

        for (ComboBox<?> cb : comboBoxes) {
            cb.valueProperty().addListener((obs, oldVal, newVal) -> verificacao.run());
        }

        for (Spinner<?> sp : spinners) {
            sp.valueProperty().addListener((obs, oldVal, newVal) -> verificacao.run());
        }

        // Executa verificação inicial
        verificacao.run();
    }
}