package com.produto.oficina.model.enums;

public enum PesTipo {
    FUNCIONARIO("Funcionário"),
    FORNECEDOR("Fornecedor"),
    CLIENTE("Cliente");

    public final String descricao;

    PesTipo(String descricao) {
        this.descricao = descricao;
    }


}
