package com.produto.oficina.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ConsumoMateriaPrima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cmp_produto_id")
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "cmp_materia_prima_id")
    private MateriaPrima materiaPrima;

    @Column(name = "cmp_quant_necessaria")
    private BigDecimal quantidadeNecessaria; // 🔹 Quantidade por unidade do produto

    @Column(name = "cmp_quant_real_usada")
    private BigDecimal quantidadeRealUsada; // 🔹 Quantidade efetivamente usada

    @Column(name = "cmp_desperdicio")
    private BigDecimal desperdicio; // 🔹 Diferença entre o previsto e o real

    public void calcularDesperdicio() {
        this.desperdicio = quantidadeRealUsada.subtract(quantidadeNecessaria);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ConsumoMateriaPrima that = (ConsumoMateriaPrima) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
