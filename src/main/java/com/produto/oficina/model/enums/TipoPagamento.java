package com.produto.oficina.model.enums;

public enum TipoPagamento {
    DINHEIRO("Dinheiro"),
    PARCELADO("Parcelado - Boleto"),
    PIX("Pix"),
    CHEQUE("Cheque");

    public final String descricao;

    TipoPagamento(String descricao) {
        this.descricao = descricao;
    }
}
