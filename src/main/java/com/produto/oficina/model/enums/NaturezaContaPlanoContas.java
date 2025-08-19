package com.produto.oficina.model.enums;

public enum NaturezaContaPlanoContas {
    RECEITA("Receita"),
    DESPESA("Despesa"),
    CUSTO("Custo"),
    ATIVO("Ativo"),
    PASSIVO("Passivo"),
    PATRIMONIO_LIQUIDO("Patrimônio líquido"),;

    public final String descricao;

    NaturezaContaPlanoContas(String descricao) {
        this.descricao = descricao;
    }
}
