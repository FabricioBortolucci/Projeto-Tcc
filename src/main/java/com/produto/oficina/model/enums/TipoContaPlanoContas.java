package com.produto.oficina.model.enums;

public enum TipoContaPlanoContas {
    SINTETICA("Sintética"), // Agrupadora
    ANALITICA("Analítica"); // Aceita lançamentos

    public final String descricao;

    TipoContaPlanoContas(String descricao) {
        this.descricao = descricao;
    }
}
