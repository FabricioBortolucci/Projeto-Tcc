package com.produto.oficina.model.enums;

public enum PesTipo {
    FUNCIONARIO("Funcionário"),
    FORNECEDOR("Fornecedor"),
    CLIENTE("Cliente");

    public final String descricao;

    PesTipo(String descricao) {
        this.descricao = descricao;
    }

    public static PesTipo fromString(String value) {
        return valueOf(value.toUpperCase());
    }

}
