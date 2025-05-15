package com.produto.oficina.dto;

import com.produto.oficina.model.ItemCompra;
import com.produto.oficina.model.Pessoa;
import com.produto.oficina.model.enums.PlanoPagamento;
import com.produto.oficina.model.enums.TipoPagamento;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@ToString
public class CompraDTO implements Serializable {
    Long id;
    Pessoa fornecedor;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime dataCompra = LocalDateTime.now();
    LocalDateTime dataCompraFinalizada;
    BigDecimal valorTotal;
    TipoPagamento tipoPagamento;
    PlanoPagamento planoPagamento;
    String observacao;
    Integer totalParcelas = 1;

    Integer quantItens = 1;
    BigDecimal valorUnitarioItens;
    BigDecimal valorTotalItens;

    List<ItemCompra> itemCompraList = new ArrayList<>();

    public CompraDTO() {
    }

    public CompraDTO(Long id, Pessoa fornecedor, LocalDateTime dataCompra, LocalDateTime dataCompraFinalizada, BigDecimal valorTotal, TipoPagamento tipoPagamento, PlanoPagamento planoPagamento, String observacao, Integer totalParcelas, Integer quantItens, BigDecimal valorUnitarioItens, BigDecimal valorTotalItens) {
        this.id = id;
        this.fornecedor = fornecedor;
        this.dataCompra = dataCompra;
        this.dataCompraFinalizada = dataCompraFinalizada;
        this.valorTotal = valorTotal;
        this.tipoPagamento = tipoPagamento;
        this.planoPagamento = planoPagamento;
        this.observacao = observacao;
        this.totalParcelas = totalParcelas;
        this.quantItens = quantItens;
        this.valorUnitarioItens = valorUnitarioItens;
        this.valorTotalItens = valorTotalItens;
    }

    public BigDecimal getCalculaValorTotalItens() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ItemCompra item : itemCompraList) {
            valorTotal = valorTotal.add(item.getSubTotal());
        }
        return valorTotal;
    }
}