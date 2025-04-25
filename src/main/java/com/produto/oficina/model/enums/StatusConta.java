package com.produto.oficina.model.enums;

public enum StatusConta {
    PENDENTE("Pendente"),
    PAGO("Pago"),
    CANCELADO("Cancelado");

    public final String descricao;

    StatusConta(String descricao) {
        this.descricao = descricao;
    }
}
