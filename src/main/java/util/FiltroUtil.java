package main.java.util;

import javafx.collections.transformation.FilteredList;
import main.java.model.espacos.Espaco;
import main.java.model.reservas.Reserva;

public class FiltroUtil {

    public static void aplicarFiltroEspacos(FilteredList<Espaco> filteredList, String busca, String tipo, boolean somenteDisponiveis) {
        filteredList.setPredicate(espaco -> {
            boolean matchesBusca = busca.isEmpty() || String.valueOf(espaco.getId()).contains(busca) || espaco.getNome().toLowerCase().contains(busca);
            boolean matchesTipo = "Todos".equals(tipo) || espaco.getClass().getSimpleName().equals(mapTipo(tipo));
            boolean matchesDisponivel = !somenteDisponiveis || espaco.isDisponivel();
            return matchesBusca && matchesTipo && matchesDisponivel;
        });
    }

    public static void aplicarFiltroReservas(FilteredList<Reserva> filteredList, String busca, String tipo) {
        filteredList.setPredicate(reserva -> {
            boolean matchesBusca = busca.isEmpty() || String.valueOf(reserva.getId()).contains(busca) || reserva.getEspaco().getNome().toLowerCase().contains(busca);
            boolean matchesTipo = "Todos".equals(tipo) || reserva.getEspaco().getClass().getSimpleName().equals(mapTipo(tipo));
            return matchesBusca && matchesTipo;
        });
    }

    private static String mapTipo(String tipo) {
        switch (tipo) {
            case "Sala de Reunião": return "SalaDeReuniao";
            case "Cabine Individual": return "CabineIndividual";
            case "Auditório": return "Auditorio";
            default: return tipo;
        }
    }
}
