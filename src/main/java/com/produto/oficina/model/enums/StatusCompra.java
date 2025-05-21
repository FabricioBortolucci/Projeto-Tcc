package com.produto.oficina.model.enums;

public enum StatusCompra {
    ABERTA("Aberta"),
    FINALIZADA( "Finalizada"),
    CANCELADA("Cancelada");

    public final String descricao;

    StatusCompra(String descricao) {
        this.descricao = descricao;
    }

    public String getBootstrapBadgeClass(){
        if (this.equals(StatusCompra.ABERTA)) {
            return "bg-warning";
        } else if (this.equals(StatusCompra.FINALIZADA)) {
            return "bg-success";
        }
        return null;
    }
}
