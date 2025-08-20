package com.produto.oficina.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class LancamentoFinanceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lan_desc")
    private String descricao;

    @Column(name = "lan_valor")
    private BigDecimal valor;

    @Column(name = "lan_data_comp")
    private LocalDate dataCompetencia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plano_contas_id")
    private PlanoDeContas planoDeContas;

    @ManyToOne
    @JoinColumn(name = "ordem_servico_id")
    private OrdemServico ordemServico;

    public LancamentoFinanceiro(String descricao, BigDecimal valor, LocalDate dataCompetencia, PlanoDeContas planoDeContas, OrdemServico ordemServico) {
        this.descricao = descricao;
        this.valor = valor;
        this.dataCompetencia = dataCompetencia;
        this.planoDeContas = planoDeContas;
        this.ordemServico = ordemServico;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        LancamentoFinanceiro produto = (LancamentoFinanceiro) o;
        return getId() != null && Objects.equals(getId(), produto.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
