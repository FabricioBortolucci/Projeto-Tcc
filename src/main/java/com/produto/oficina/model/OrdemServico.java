package com.produto.oficina.model;

import com.produto.oficina.model.enums.PlanoPagamento;
import com.produto.oficina.model.enums.StatusOS;
import com.produto.oficina.model.enums.TipoPagamento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private LocalDateTime dataAbertura;

    @Column(name = "os_data_fechamento")
    private LocalDateTime dataFechamento;

    @Column(name = "os_valor_total")
    private BigDecimal valorTotal;

    @ManyToOne
    @JoinColumn(name = "os_cliente_id")
    private Pessoa cliente;

    @ManyToOne
    @JoinColumn(name = "funcionario_id")
    private Pessoa funcionario;

    @Column(name = "os_tipo_pagamento")
    @Enumerated(EnumType.STRING)
    private TipoPagamento tipoPagamento;

    @Column(name = "os_plano_pagamento")
    @Enumerated(EnumType.STRING)
    private PlanoPagamento planoPagamento;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<OrdemServicoItem> itensServico;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<OrdemServicoPeca> pecasUsadas;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<OrdemServicoMP> mpUsadas;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ContaReceber> contaRecebers;

    @Column(name = "os_status")
    private StatusOS status;

    @Column(name = "os_obs")
    private String observacao;

    @Column(name = "os_mp_desperdicio")
    private BigDecimal desperdicio;




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
