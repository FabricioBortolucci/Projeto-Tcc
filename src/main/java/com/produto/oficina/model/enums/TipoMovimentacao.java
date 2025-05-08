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
}
