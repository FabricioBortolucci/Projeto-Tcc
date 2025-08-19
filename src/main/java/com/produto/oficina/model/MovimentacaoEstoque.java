package com.produto.oficina.model;

import com.produto.oficina.model.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(nullable = false)
    private BigDecimal quantidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    @Column(nullable = false)
    private LocalDateTime dataMovimentacao = LocalDateTime.now();

    @Column(name = "custo_unitario", precision = 10, scale = 2)
    private BigDecimal custoUnitario;

    @Column(name = "origem_documento_id")
    private Long origemId;

    @Column(name = "origem_documento_tipo")
    private String origemTipo;

    @ManyToOne
    @JoinColumn(name = "usuario_responsavel_id")
    private Pessoa usuarioResponsavel;

    @Column(length = 500)
    private String observacao;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        MovimentacaoEstoque endereco = (MovimentacaoEstoque) o;
        return getId() != null && Objects.equals(getId(), endereco.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
