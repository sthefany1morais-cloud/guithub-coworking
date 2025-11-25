package main.java.service;

import main.java.dao.base.DAOBase;

public class SistemaService {

    public void persistirDados() {
        DAOBase.persistirDadosGlobais();
    }

    public String formatarDinheiro(String input) {
        String digits = input.replaceAll("\\D", "");
        if (digits.isEmpty()) return "0,00";
        if (digits.length() == 1) return "0,0" + digits;
        if (digits.length() == 2) return "0," + digits;
        String integerPart = digits.substring(0, digits.length() - 2).replaceFirst("^0+", "");
        if (integerPart.isEmpty()) integerPart = "0";
        String decimalPart = digits.substring(digits.length() - 2);
        return integerPart + "," + decimalPart;
    }

}