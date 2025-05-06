package com.produto.oficina.Utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

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

    public static String getUsuarioLogadoUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal()).getUsername();
        }
        return null;
    }
}
