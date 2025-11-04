package com.produto.oficina.model;

import com.produto.oficina.model.enums.StatusCaixa;
import com.produto.oficina.model.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "caixa") // Explicitando o nome da tabela
public class Caixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "caixa_id")
    private Long id;

    @Column(name = "data_abertura", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataAbertura = LocalDateTime.now();

    @Column(name = "data_fechamento")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataFechamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_abertura_id", nullable = false)
    private Pessoa usuarioAbertura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_fechamento_id")
    private Pessoa usuarioFechamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_caixa", nullable = false, length = 20)
    private StatusCaixa status;

    @Column(name = "valor_abertura", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorAbertura;

    @Column(name = "total_entradas_mov", precision = 10, scale = 2)
    private BigDecimal totalEntradasMovimentacoes;

    @Column(name = "total_saidas_mov", precision = 10, scale = 2)
    private BigDecimal totalSaidasMovimentacoes;

    // valorAbertura + SOMA(Entradas) - SOMA(Saidas)
    @Column(name = "saldo_esperado_sistema", precision = 10, scale = 2)
    private BigDecimal saldoEsperadoSistema;

    @Column(name = "valor_fechamento_contado", precision = 10, scale = 2)
    private BigDecimal valorFechamentoContado;

    // valorFechamentoContado - saldoEsperadoSistema
    @Column(name = "diferenca_caixa", precision = 10, scale = 2)
    private BigDecimal diferencaCaixa;

    @Column(name = "observacao_abertura", length = 500, columnDefinition = "TEXT")
    private String observacaoAbertura;

    @Column(name = "observacao_fechamento", length = 500)
    private String observacaoFechamento;

    @OneToMany(mappedBy = "caixa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MovimentacaoCaixa> movimentacoes = new ArrayList<>();

    public Caixa(Pessoa usuarioAbertura, BigDecimal valorAbertura, String observacaoAbertura) {
        this.usuarioAbertura = usuarioAbertura;
        this.valorAbertura = valorAbertura;
        this.observacaoAbertura = observacaoAbertura;
        this.status = StatusCaixa.ABERTO;
        this.totalEntradasMovimentacoes = BigDecimal.ZERO;
        this.totalSaidasMovimentacoes = BigDecimal.ZERO;
    }

    public BigDecimal getValorAtualCaixa() {
        if (this.getStatus() != StatusCaixa.ABERTO && this.getStatus() != StatusCaixa.EM_CONFERENCIA) {
            return this.valorFechamentoContado != null ? this.valorFechamentoContado : BigDecimal.ZERO;
        }
        BigDecimal saldoAtual = this.getValorAbertura();

        for (MovimentacaoCaixa movimentacao : this.getMovimentacoes()) {
            TipoMovimentacao tipo = movimentacao.getTipo();
            if (tipo == TipoMovimentacao.ENTRADA || tipo == TipoMovimentacao.SUPRIMENTO) {
                saldoAtual = saldoAtual.add(movimentacao.getValor());
            } else if (tipo == TipoMovimentacao.SANGRIA || tipo == TipoMovimentacao.SAIDA) {
                saldoAtual = saldoAtual.subtract(movimentacao.getValor());
            }
        }
        return saldoAtual;
    }

    public BigDecimal getValorTotalEntradas() {
        BigDecimal totalEntradas = BigDecimal.ZERO;
        for (MovimentacaoCaixa movimentacao : this.getMovimentacoes()) {
            if (movimentacao.getTipo() == TipoMovimentacao.ENTRADA || movimentacao.getTipo() == TipoMovimentacao.SUPRIMENTO) {
                totalEntradas = totalEntradas.add(movimentacao.getValor());
            }
        }
        return totalEntradas;
    }

    public BigDecimal getValorTotalSaidas() {
        BigDecimal totalSaidas = BigDecimal.ZERO;
        for (MovimentacaoCaixa movimentacao : this.getMovimentacoes()) {
            if (movimentacao.getTipo() == TipoMovimentacao.SAIDA || movimentacao.getTipo() == TipoMovimentacao.SANGRIA) {
                totalSaidas = totalSaidas.add(movimentacao.getValor());
            }
        }
        return totalSaidas;
    }

    public BigDecimal getCalculaSaldoEsperado() {
        BigDecimal saldoEsperado = this.getValorAbertura();
        saldoEsperado = saldoEsperado.add(this.getValorTotalEntradas());
        saldoEsperado = saldoEsperado.subtract(this.getValorTotalSaidas());
        return saldoEsperado;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Caixa caixa = (Caixa) o;
        return getId() != null && Objects.equals(getId(), caixa.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}