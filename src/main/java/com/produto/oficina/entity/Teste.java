package com.produto.oficina.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Teste {

    @Id
    private Long id;
    private String nome;
    private String descricao;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return id.toString() + " " + nome + " " + descricao;
    }
}
