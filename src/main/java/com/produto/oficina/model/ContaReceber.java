package com.produto.oficina.model;

import com.produto.oficina.model.enums.StatusConta;
import com.produto.oficina.model.enums.TipoPagamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ContaReceber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conta_receber_id")
    private Long id;

    @Column(name = "conta_receber_valor")
    private BigDecimal valor;

    @Column(name = "conta_receber_data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "conta_receber_data_recebimento")
    private LocalDate dataRecebimento;

    @Column(name = "numero_parcela")
    private Integer numeroParcela;

    @Column(name = "total_parcelas")
    private Integer totalParcelas;

    @Column(name = "valor_total_original")
    private BigDecimal valorTotalOriginal;

    @Column(name = "conta_receber_status")
    @Enumerated(EnumType.STRING)
    private StatusConta status;

    @Column(name = "tipo_contapagar")
    private TipoPagamento tipoPagamento;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Pessoa cliente;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id")
    private OrdemServico ordemServico;

    @OneToMany(mappedBy = "contaReceber")
    private List<MovimentacaoCaixa> movimentacoes;
}
