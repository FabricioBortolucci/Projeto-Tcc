package com.produto.oficina.model;

import com.produto.oficina.model.enums.ProdutoTipo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_id")
    private Long id;

    @Column(name = "prod_nome")
    private String nome;

    @Column(name = "prod_preco")
    private BigDecimal precoUnitario;

    @Column(name = "prod_preco_custo")
    private BigDecimal precoCusto;

    @Column(name = "prod_desc")
    private String descricao;

    @Column(name = "mp_unidade")
    private String unidade;

    @Column(name = "prod_estoque")
    private Integer estoque;

    @Column(name = "prod_tipo")
    @Enumerated(EnumType.STRING)
    private ProdutoTipo produtoTipo;

    @ManyToMany
    @JoinTable(
            name = "produto_fornecedor",
            joinColumns = @JoinColumn(name = "produto_id"),
            inverseJoinColumns = @JoinColumn(name = "fornecedor_id")
    )
    @ToString.Exclude
    private List<Pessoa> fornecedores = new ArrayList<>();

    @Column(name = "prod_ativo")
    private Boolean ativo = true;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Produto produto = (Produto) o;
        return getId() != null && Objects.equals(getId(), produto.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
