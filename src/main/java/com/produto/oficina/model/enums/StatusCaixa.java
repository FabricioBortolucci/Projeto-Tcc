package com.produto.oficina.model.enums;

public enum StatusCaixa {
    ABERTO("Aberto"),                 // O caixa está aberto e operacional.
    EM_CONFERENCIA("Em Conferência"), // O caixa iniciou o processo de fechamento, valores estão sendo apurados.
    FECHADO("Fechado"),               // O caixa foi conferido e fechado.
    CANCELADO("Cancelado");           // O caixa foi aberto incorretamente e cancelado.

    private final String descricao;

    StatusCaixa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
