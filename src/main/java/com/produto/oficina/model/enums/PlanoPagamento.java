package com.produto.oficina.model.enums;

public enum PlanoPagamento {
    AVISTA("À Vísta"),
    APRAZO("A prazo");

    public final String descricao;

    PlanoPagamento(String descricao) {
        this.descricao = descricao;
    }
}
