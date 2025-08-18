package com.produto.oficina.model;

import com.produto.oficina.model.enums.StatusConta;
import com.produto.oficina.model.enums.TipoPagamento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SortComparator;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ContaPagar implements Comparable<ContaPagar> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conta_pagar_id")
    private Long id;

    @Column(name = "conta_pagar_valor")
    private BigDecimal valor;

    @Column(name = "conta_pagar_valorpago")
    private BigDecimal valorPago;

    @Column(name = "conta_pagar_data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "conta_pagar_data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "numero_parcela")
    private Integer numeroParcela;

    @Column(name = "total_parcelas")
    private Integer totalParcelas;

    @Column(name = "valor_total_original")
    private BigDecimal valorTotalOriginal;

    @Column(name = "tipo_contapagar")
    private TipoPagamento tipoPagamento;

    @Column(name = "conta_pagar_status")
    @Enumerated(EnumType.STRING)
    private StatusConta status;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private Pessoa fornecedor;

    @ManyToOne
    @JoinColumn(name = "compra_id")
    private Compra compra;

    @OneToMany(mappedBy = "contaPagar", cascade = CascadeType.ALL)
    private List<MovimentacaoCaixa> movimentacoes;

    @Column(name = "cp_observacao", columnDefinition = "TEXT")
    private String observacao;

    public ContaPagar(Long id, BigDecimal valor, LocalDate dataVencimento, LocalDate dataPagamento, Integer numeroParcela, Integer totalParcelas, BigDecimal valorTotalOriginal, TipoPagamento tipoPagamento, StatusConta status, Pessoa fornecedor, Compra compra, String observacao) {
        this.id = id;
        this.valor = valor;
        this.valorPago = valor;
        this.dataVencimento = dataVencimento;
        this.dataPagamento = dataPagamento;
        this.numeroParcela = numeroParcela;
        this.totalParcelas = totalParcelas;
        this.valorTotalOriginal = valorTotalOriginal;
        this.tipoPagamento = tipoPagamento;
        this.status = status;
        this.fornecedor = fornecedor;
        this.compra = compra;
        this.observacao = observacao;
    }

    @Override
    public int compareTo(ContaPagar o) {
        return 0;
    }
}