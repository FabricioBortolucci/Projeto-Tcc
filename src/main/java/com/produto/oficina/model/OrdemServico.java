package com.produto.oficina.model;

import com.produto.oficina.model.enums.PlanoPagamento;
import com.produto.oficina.model.enums.StatusOS;
import com.produto.oficina.model.enums.TipoPagamento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "os_id")
    private Long id;

    @Column(name = "os_data_abertura")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dataAbertura = LocalDateTime.now();

    @Column(name = "os_data_fechamento")
    private LocalDateTime dataFechamento;

    @Column(name = "os_data_cancelamento")
    private LocalDateTime dataCancelamento;

    @Column(name = "os_valor_total")
    private BigDecimal valorTotal;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "os_cliente_id")
    private Pessoa cliente;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "funcionario_id")
    private Pessoa funcionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_usuario_cancelou")
    private Usuario usuarioCancelou;

    @Column(name = "os_tipo_pagamento")
    @Enumerated(EnumType.STRING)
    private TipoPagamento tipoPagamento;

    @Column(name = "os_plano_pagamento")
    @Enumerated(EnumType.STRING)
    private PlanoPagamento planoPagamento;

    @Column(name = "os_int_dias")
    private Integer intDias;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<OrdemServicoItem> itensServico = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<OrdemServicoPeca> pecasUsadas = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ContaReceber> contaRecebers = new ArrayList<>();

    @Column(name = "os_status")
    private StatusOS status;

    @Column(name = "os_obs")
    private String observacao;

    @Column(name = "os_obs_cancel")
    private String observacaoCancelamento;

    @Column(name = "os_mp_desperdicio")
    private BigDecimal desperdicio;

    @Column(name = "os_quantParcelas")
    private Integer quantParcelas;

    @Transient
    private Long idServico;

    @Transient
    private Long idProds;

    @Transient
    private List<BigDecimal> parcelas = new ArrayList<>();


    public BigDecimal getCalculaTotalServicoItens() {
        BigDecimal valorTotalServicosItens = BigDecimal.ZERO;
        if (!itensServico.isEmpty()) {
            for (OrdemServicoItem ordemServicoItem : itensServico) {
                valorTotalServicosItens = valorTotalServicosItens.add(ordemServicoItem.getSubTotal());
            }
        }
        return valorTotalServicosItens;
    }

    public BigDecimal getCalculaTotalProdsItens() {
        BigDecimal valorTotalProdsItens = BigDecimal.ZERO;
        if (!pecasUsadas.isEmpty()) {
            for (OrdemServicoPeca ordemServicoPeca : pecasUsadas) {
                valorTotalProdsItens = valorTotalProdsItens.add(ordemServicoPeca.getSubTotal());
            }
        }
        return valorTotalProdsItens;
    }

    public List<BigDecimal> getValoresParcelas() {
        List<BigDecimal> valoresParcelas = new ArrayList<>();
        for (int i = 0; i < quantParcelas; i++) {
            valoresParcelas.add((this.getCalculaTotalProdsItens().add(this.getCalculaTotalServicoItens())).divide(new BigDecimal(quantParcelas), RoundingMode.HALF_UP));
        }
        return valoresParcelas;
    }

    public boolean validaCampos() {
        return this.getFuncionario() == null || this.getCliente() == null || this.getItensServico().isEmpty() || this.getPecasUsadas().isEmpty();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrdemServico produto = (OrdemServico) o;
        return getId() != null && Objects.equals(getId(), produto.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
