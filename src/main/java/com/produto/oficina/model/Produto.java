package com.produto.oficina.model;

import com.produto.oficina.model.enums.ProdutoTipo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
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

    @Column(name = "prod_preco", precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "prod_preco_custo", precision = 10, scale = 2)
    private BigDecimal precoCusto;

    @Column(name = "prod_desc")
    private String descricao;

    @Column(name = "prod_tamanho_metros", precision = 4, scale = 2)
    private BigDecimal tamanhoMetros;

    @Column(name = "prod_estoque")
    private Integer estoque;

    @Column(name = "prod_tipo")
    @Enumerated(EnumType.STRING)
    private ProdutoTipo produtoTipo;

    @Column(name = "prod_ativo")
    private Boolean ativo = true;

    /**
     * Conta de Receita para onde vai o valor da venda deste produto.
     * Aplicável principalmente para PEÇAS.
     * Ex: "1.01.01 - Receita com Venda de Peças"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_receita_id")
    @ToString.Exclude
    private PlanoDeContas contaReceitaPadrao;

    /**
     * Conta de Ativo onde o produto é armazenado.
     * Ex: "1.01.04.01 - Estoque de Peças" ou "1.01.04.02 - Estoque de Matéria-Prima"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_estoque_id")
    private PlanoDeContas contaEstoque;

    /**
     * Conta de Custo para onde vai o custo do produto quando ele é vendido/usado.
     * Ex: "2.01.01 - Custo das Peças Vendidas (CMV)" ou "2.01.03 - Custo com Matéria-Prima Consumida"
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_custo_id")
    private PlanoDeContas contaCusto;


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
