package com.produto.oficina.model.enums;

public enum TipoMovimentacao {
    ENTRADA("Entrada"),
    SAIDA("saída");


    public final String descricao;

    TipoMovimentacao(String descricao) {
        this.descricao = descricao;
    }
}
