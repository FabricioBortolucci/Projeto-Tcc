package com.produto.oficina.model.enums;

public enum TipoTelefone {
    CELULAR("Celular"),
    FIXO("Fixo"),
    COMERCIAL("Comercial");

    public final String descricao;

    TipoTelefone(String descricao) {
        this.descricao = descricao;
    }

}
