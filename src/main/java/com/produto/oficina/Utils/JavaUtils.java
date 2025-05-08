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


}
