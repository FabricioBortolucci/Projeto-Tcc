package com.produto.oficina.model.enums;

public enum TipoPagamento {
    DINHEIRO("Dinheiro"),
    PARCELADO("Parcelado - Boleto"),
    BOLETO("Boleto"),
    CARTAO_CREDITO("Cartão de Crédito"),
    PIX("Pix"),
    CHEQUE("Cheque");

    public final String descricao;

    TipoPagamento(String descricao) {
        this.descricao = descricao;
    }

    public static TipoPagamento[] apenasAvista() {
        return new TipoPagamento[]{DINHEIRO, PIX, BOLETO};
    }
    public static TipoPagamento[] apenasAtrasado() {
        return new TipoPagamento[]{PARCELADO, CHEQUE, CARTAO_CREDITO};
    }
    public static TipoPagamento[] apenasAprovado() {
        return new TipoPagamento[]{BOLETO, PIX, CHEQUE, DINHEIRO, CARTAO_CREDITO};
    }
}
