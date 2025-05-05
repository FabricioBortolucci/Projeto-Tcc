package com.produto.oficina.model;

import com.produto.oficina.model.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MovimentacaoCaixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caixa_id")
    private Caixa caixa;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "data_movimentacao")
    private LocalDate dataMovimentacao;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo; // ENTRADA ou SAÍDA

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_pagar_id")
    private ContaPagar contaPagar; // Se for pagamento

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_receber_id")
    private ContaReceber contaReceber; // Se for recebimento

    @Column(name = "descricao")
    private String descricao;

}
