package com.produto.oficina.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.produto.oficina.model.enums.PesTipo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pes_id")
    private Long id;

    @Column(name = "pes_nome")
    private String pesNome;

    @Column(name = "pes_cpfcnpj", length = 18, unique = true)
    private String pesCpfCnpj;

    @Column(name = "pes_email")
    private String pesEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "pes_tipo")
    private PesTipo pesTipo;

    @Column(name = "pes_ativo")
    private Boolean pesAtivo;

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<Endereco> enderecos = new ArrayList<>();

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<Telefone> telefones = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @ToString.Exclude
    private Usuario usuario;

    @Column(name = "pes_data_cadastro")
    private LocalDateTime pesDataCadastro;

    @Column(name = "pes_data_atualizacao")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime pesDataAtualizacao;

    @Column(name = "pes_data_nascimento")
    private LocalDate pesDataNascimento;

    @Column(name = "pes_rg", length = 20)
    private String pesRg;

    @Column(name = "pes_genero")
    private String pesGenero;

    @Column(name = "pes_fsc_jrc")
    private String pesFisicoJuridico;

    @ManyToMany(mappedBy = "fornecedores", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Produto> produtosFornecidos;

    @Column(name = "data_ultimo_fornecimento")
    private LocalDateTime dataUltimoFornecimento;


    public String getTelefonePrincipal() {
        return telefones != null && !telefones.isEmpty() ? Objects.requireNonNull(telefones.stream().filter(Telefone::isTelPrincipal)
                .findFirst().orElse(null)).getTelNumero() : "Sem telefone principal cadastrado.";
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Pessoa pessoa = (Pessoa) o;
        return getId() != null && Objects.equals(getId(), pessoa.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
