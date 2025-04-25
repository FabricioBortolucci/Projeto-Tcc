package com.produto.oficina.model;

import com.produto.oficina.model.enums.StatusConta;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ContaPagar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conta_pagar_id")
    private Long id;

    @Column(name = "conta_pagar_valor")
    private BigDecimal valor;

    @Column(name = "conta_pagar_data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "conta_pagar_data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "conta_pagar_status")
    @Enumerated(EnumType.STRING)
    private StatusConta status;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private Pessoa fornecedor;

    @OneToOne(mappedBy = "contaPagar")
    private Compra compra;
}