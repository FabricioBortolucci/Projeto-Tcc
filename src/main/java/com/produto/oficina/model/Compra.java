package com.produto.oficina.model;

import com.produto.oficina.model.enums.PlanoPagamento;
import com.produto.oficina.model.enums.TipoPagamento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mov_comp_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Pessoa fornecedor;

    @Column(name = "mov_comp_data")
    private LocalDateTime dataCompra;

    @Column(name = "mov_comp_data_finalizada")
    private LocalDateTime dataCompraFinalizada;

    @Column(name = "mov_comp_valor_total")
    private BigDecimal valorTotal;

    @Column(name = "comp_tipopagamento")
    @Enumerated(EnumType.STRING)
    private TipoPagamento tipoPagamento;

    @Column(name = "comp_planopagamento")
    @Enumerated(EnumType.STRING)
    private PlanoPagamento planoPagamento;

    @Column(name = "mov_comp_observacao")
    private String observacao;

    @Column(name = "total_parcelas")
    private Integer totalParcelas;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<ItemCompra> itens = new ArrayList<>();

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<ContaPagar> contaPagars = new ArrayList<>();

    public BigDecimal getValorTotalItens() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ItemCompra item : itens) {
            valorTotal = valorTotal.add(item.getValorTotal());
        }
        return valorTotal;
    }
}