package com.produto.oficina.model;

import com.produto.oficina.model.enums.NaturezaContaPlanoContas;
import com.produto.oficina.model.enums.TipoContaPlanoContas;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class PlanoDeContas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O código estruturado é obrigatório.")
    @Pattern(regexp = "^\\d+(\\.\\d+)*$", message = "O código deve seguir o formato numérico com pontos (ex: 1.01.01).")
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @NotBlank(message = "A descrição é obrigatória.")
    @Column(nullable = false, length = 255)
    private String descricao;

    @NotNull(message = "O tipo da conta é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoContaPlanoContas tipoConta;

    @NotNull(message = "A natureza da conta é obrigatória.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NaturezaContaPlanoContas naturezaConta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_pai_id")
    @ToString.Exclude
    private PlanoDeContas contaPai;

    @OneToMany(mappedBy = "contaPai", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<PlanoDeContas> contasFilhas;

    @Column(nullable = false)
    private boolean aceitaLancamentos;
}


