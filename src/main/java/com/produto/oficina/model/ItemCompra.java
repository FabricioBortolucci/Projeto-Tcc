package com.produto.oficina.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ItemCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_comp_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mov_comp_id")
    private Compra compra;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(name = "item_comp_quantidade")
    private Integer quantidade;

    @Column(name = "item_comp_valor_unitario")
    private BigDecimal valorUnitario;

    @Column(name = "item_comp_valor_total")
    private BigDecimal valorTotal;


    public BigDecimal getSubTotal() {
        return this.valorUnitario.multiply(new BigDecimal(this.quantidade));
    }
}