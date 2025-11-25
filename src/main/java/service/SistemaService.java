package main.java.service;

import main.java.dao.base.DAOBase;

public class SistemaService {

    public void persistirDados() {
        // Chama o método do DAO para sincronizar dados com ObjectDB
        DAOBase.persistirDadosGlobais();
    }

    public String formatarDinheiro(String input) {
        String digits = input.replaceAll("\\D", "");
        if (digits.isEmpty()) return "0,00";
        if (digits.length() == 1) return "0,0" + digits;
        if (digits.length() == 2) return "0," + digits;
        String integerPart = digits.substring(0, digits.length() - 2).replaceFirst("^0+", "");  // Remove zeros à esquerda
        if (integerPart.isEmpty()) integerPart = "0";
        String decimalPart = digits.substring(digits.length() - 2);
        return integerPart + "," + decimalPart;
    }

    public String formatarHoras(String input) {
        String digits = input.replaceAll("\\D", "");
        if (digits.isEmpty()) return "00:00";
        if (digits.length() == 1) return "00:0" + digits;
        if (digits.length() == 2) return "00:" + digits;
        if (digits.length() == 3) return "0" + digits.charAt(0) + ":" + digits.substring(1);
        if (digits.length() >= 4) return digits.substring(0, 2) + ":" + digits.substring(2, 4);
        return "00:00";
    }



}
