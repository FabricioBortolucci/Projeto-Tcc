package com.produto.oficina.model.enums;

public enum ProdutoTipo {
    MATERIA_PRIMA("Matéria-Prima"),
    PECA("Peça");

    public final String descricao;

    ProdutoTipo(String descricao) {
        this.descricao = descricao;
    }
}
