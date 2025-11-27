package main.java.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.model.espacos.Espaco;
import main.java.model.reservas.Reserva;

import java.util.List;

public class TabelaUtil {

    /**
     * Configura colunas para TableView de Espaços.
     */
    public static void configurarColunasEspacos(TableView<Espaco> tableView,
                                                TableColumn<Espaco, Integer> idColumn,
                                                TableColumn<Espaco, String> nomeColumn,
                                                TableColumn<Espaco, String> tipoColumn,
                                                TableColumn<Espaco, Integer> capacidadeColumn,
                                                TableColumn<Espaco, Double> precoColumn,
                                                TableColumn<Espaco, Boolean> disponivelColumn) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        tipoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClass().getSimpleName()));
        capacidadeColumn.setCellValueFactory(new PropertyValueFactory<>("capacidade"));
        if (precoColumn != null) {
            precoColumn.setCellValueFactory(new PropertyValueFactory<>("precoPorHora"));
        }
        disponivelColumn.setCellValueFactory(new PropertyValueFactory<>("disponivel"));
    }

    /**
     * Configura colunas para TableView de Reservas.
     */
    public static void configurarColunasReservas(TableView<Reserva> tableView,
                                                 TableColumn<Reserva, Integer> idColumn,
                                                 TableColumn<Reserva, String> espacoColumn,
                                                 TableColumn<Reserva, String> tipoColumn,
                                                 TableColumn<Reserva, String> inicioColumn,
                                                 TableColumn<Reserva, String> fimColumn,
                                                 TableColumn<Reserva, Double> valorColumn) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        espacoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEspaco().getNome()));
        tipoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEspaco().getClass().getSimpleName()));
        inicioColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInicio().toString()));
        fimColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFim().toString()));
        valorColumn.setCellValueFactory(new PropertyValueFactory<>("valorCalculado"));
    }

    /**
     * Carrega uma lista em um TableView.
     */
    public static <T> void carregarLista(TableView<T> tableView, List<T> lista) {
        ObservableList<T> observableList = FXCollections.observableArrayList(lista);
        tableView.setItems(observableList);
    }

    /**
     * Configura colunas dinâmicas para relatórios (usado em RelatorioPopupController).
     */
    public static void configurarColunasDinamicas(TableView<ObservableList<String>> tableView,
                                                  List<TableColumn<ObservableList<String>, String>> colunas,
                                                  List<String> nomesColunas) {
        for (int i = 0; i < colunas.size() && i < nomesColunas.size(); i++) {
            colunas.get(i).setText(nomesColunas.get(i));
            final int index = i;
            colunas.get(i).setCellValueFactory(data -> new SimpleStringProperty(data.getValue().size() > index ? data.getValue().get(index) : ""));
        }
    }
}

