package com.produto.oficina.model.enums;

public enum StatusCompra {
    ABERTA("Aberta"),
    FINALIZADA( "Finalizada"),
    CANCELADA("Cancelada");

    public final String descricao;

    StatusCompra(String descricao) {
        this.descricao = descricao;
    }
}
