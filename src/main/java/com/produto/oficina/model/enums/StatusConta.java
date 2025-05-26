package com.produto.oficina.model.enums;

public enum StatusConta {
    PENDENTE("Pendente"),
    PAGO("Pago"),
    ATRASADO("Atrasado"),
    CANCELADO("Cancelado"),
    CANCELADO_CREDITO("Cancelado (Gerou Crédito)");

    public final String descricao;

    StatusConta(String descricao) {
        this.descricao = descricao;
    }
}
