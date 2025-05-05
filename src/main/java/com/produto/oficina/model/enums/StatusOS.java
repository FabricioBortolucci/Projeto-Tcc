package com.produto.oficina.model.enums;

public enum StatusOS {
    ABERTA("Aberta"),
    EM_EXECUCAO("Em execução"),
    FINALIZADA( "Finalizada"),
    CANCELADA("Cancelada");

    public final String descricao;

    StatusOS(String descricao) {
        this.descricao = descricao;
    }
}
