package com.produto.oficina.model;

import com.produto.oficina.model.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp; // Para data da movimentação
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimentacao_caixa")
public class MovimentacaoCaixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mov_caixa_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caixa_id", nullable = false)
    private Caixa caixa;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_movimentacao", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataMovimentacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimentacao", nullable = false, length = 20)
    private TipoMovimentacao tipo;

    @Column(name = "descricao", nullable = false, length = 255, columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_pagar_id")
    private ContaPagar contaPagar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_receber_id")
    private ContaReceber contaReceber;

    @Column(name = "origem_id")
    private Long origemId;

    @Column(name = "origem_tipo", length = 50)
    private String origemTipo;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_contas_id")
    private PlanoDeContas planoDeContas;


    public MovimentacaoCaixa(Caixa caixa, BigDecimal valor, TipoMovimentacao tipo, String descricao,
                             ContaPagar contaPagar, ContaReceber contaReceber,
                             Long origemId, String origemTipo) {
        this.caixa = caixa;
        this.valor = valor;
        this.tipo = tipo;
        this.descricao = descricao;
        this.contaPagar = contaPagar;
        this.contaReceber = contaReceber;
        this.origemId = origemId;
        this.origemTipo = origemTipo;
    }



}