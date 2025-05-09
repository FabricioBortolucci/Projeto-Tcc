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
@Table(name = "movimentacao_caixa") // Explicitando o nome da tabela
public class MovimentacaoCaixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mov_caixa_id")
    private Long id;

    /**
     * Caixa ao qual esta movimentação pertence.
     * Uma movimentação sempre deve estar associada a um caixa.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caixa_id", nullable = false) // Tornando obrigatório
    private Caixa caixa;

    /**
     * Valor da movimentação.
     */
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    /**
     * Data e hora em que a movimentação foi registrada.
     * Preenchido automaticamente na criação.
     */
    @Column(name = "data_movimentacao", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataMovimentacao;

    /**
     * Tipo da movimentação (ENTRADA, SAIDA, AJUSTE_POSITIVO, AJUSTE_NEGATIVO).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimentacao", nullable = false, length = 20)
    private TipoMovimentacao tipo;

    /**
     * Descrição da movimentação (ex: "Venda pedido 123", "Pagamento fornecedor X", "Suprimento inicial").
     */
    @Column(name = "descricao", nullable = false, length = 255, columnDefinition = "TEXT")
    private String descricao;

    /**
     * Referência à Conta a Pagar que originou esta movimentação (se aplicável).
     * Ex: Saída de caixa para pagar uma despesa.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_pagar_id") // Opcional
    private ContaPagar contaPagar;

    /**
     * Referência à Conta a Receber que originou esta movimentação (se aplicável).
     * Ex: Entrada de caixa pelo recebimento de uma fatura de cliente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_receber_id") // Opcional
    private ContaReceber contaReceber;

    /**
     * Identificador da entidade que originou a movimentação, caso não seja uma ContaPagar/Receber.
     * Ex: ID da Venda, ID do Pedido de Suprimento.
     * Usado em conjunto com 'origemTipo'.
     */
    @Column(name = "origem_id")
    private Long origemId;

    /**
     * Tipo da entidade que originou a movimentação.
     * Ex: "VENDA", "SUPRIMENTO", "SANGRIA".
     * Ajuda a rastrear a fonte da movimentação de forma mais genérica.
     */
    @Column(name = "origem_tipo", length = 50)
    private String origemTipo;

    // Construtor para facilitar a criação de movimentações comuns
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
        // dataMovimentacao será preenchida por @CreationTimestamp
    }



}