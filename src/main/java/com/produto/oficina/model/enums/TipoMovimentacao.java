package com.produto.oficina.model.enums;

public enum TipoMovimentacao {
    ENTRADA("Entrada"),
    SAIDA("Saída"),
    SUPRIMENTO("Suprimento de Caixa"),
    SANGRIA("Sangria de Caixa");

    public final String descricao;

    TipoMovimentacao(String descricao) {
        this.descricao = descricao;
    }

    public static TipoMovimentacao[] apenasEntradas() {
        return new TipoMovimentacao[]{ENTRADA, SUPRIMENTO};
    }

    public static TipoMovimentacao[] apenasSaidas() {
        return new TipoMovimentacao[]{SAIDA, SANGRIA};
    }
}
