package com.produto.oficina.Utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@Component
public class JavaUtils {



    public static BigDecimal parseBigDecimal(String valor) {
        try {
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.of("pt", "BR"));
            return new BigDecimal(nf.parse(valor).toString());
        } catch (ParseException e) {
            System.err.println("Erro ao converter valor para BigDecimal: " + valor);
            return null;
        }
    }

    public static BigDecimal parseMonetaryString(String valueStr) {
        if (valueStr == null || valueStr.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            // Tenta primeiro com o formato que o JavaScript envia (com ponto decimal)
            return new BigDecimal(valueStr);
        } catch (NumberFormatException e) {
            // Se falhar, tenta interpretar como formato brasileiro R$ 1.234,56 (se vier assim)
            // Esta parte pode precisar de mais robustez dependendo de como o valor chega
            NumberFormat format = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
            try {
                Number number = format.parse(valueStr.replace("R$", "").trim());
                return BigDecimal.valueOf(number.doubleValue());
            } catch (ParseException pe) {
                // Logar o erro ou tratar
                System.err.println("Erro ao parsear valor monetário (fallback): " + valueStr + " - " + pe.getMessage());
                return BigDecimal.ZERO;
            }
        }
    }

}
